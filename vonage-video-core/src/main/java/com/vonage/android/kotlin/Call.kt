package com.vonage.android.kotlin

import android.content.Context
import android.media.projection.MediaProjection
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.ext.extractSenderName
import com.vonage.android.kotlin.ext.firstScreenSharing
import com.vonage.android.kotlin.ext.sorted
import com.vonage.android.kotlin.internal.ActiveSpeakerTracker
import com.vonage.android.kotlin.internal.CaptionsHideScheduler
import com.vonage.android.kotlin.internal.PublisherFactory
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.CaptionLine
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalFlows
import com.vonage.android.kotlin.model.SignalState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.kotlin.sdk.VonageArchiveListener
import com.vonage.android.kotlin.sdk.VonageCaptionsListener
import com.vonage.android.kotlin.sdk.VonageError
import com.vonage.android.kotlin.sdk.VonagePublisherKitListener
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.sdk.VonageSessionListener
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.kotlin.signal.SignalPlugin
import com.vonage.logger.vonageLogger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Main implementation of CallFacade managing a Vonage video call session.
 *
 * Handles:
 * - Session lifecycle (connect, disconnect, pause, resume)
 * - Participant management (subscribers, publishers, screen sharing)
 * - Active speaker detection with audio level tracking
 * - Signal handling (chat, reactions) via plugins
 * - Captions support
 * - Video visibility optimization based on UI state
 *
 * @param token Authentication token for the session
 * @param session Vonage session wrapper
 * @param publisherFactory Publisher factory
 * @param signalPlugins List of plugins for handling custom signals
 * @param coroutineDispatcher Dispatcher for coroutine operations (defaults to IO)
 * @param activeSpeakerTrackerOverride Optional override for [ActiveSpeakerTracker]; when null
 *   a default instance is created. Intended for testing only.
 */
@OptIn(FlowPreview::class)
@Stable
class Call internal constructor(
    private val token: String,
    private val session: VonageSession,
    private val publisherFactory: PublisherFactory,
    private val signalPlugins: List<SignalPlugin>,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    activeSpeakerTrackerOverride: ActiveSpeakerTracker? = null,
) : CallFacade {

    private lateinit var context: Context

    private val coroutineScope = CoroutineScope(SupervisorJob() + coroutineDispatcher)
    private var participantsOnScreenJob: Job? = null
    private var activeSpeakerTrackerJob: Job? = null
    private var signalsJob: Job? = null

    /** Tracks active speaker based on audio levels across all participants */
    private val activeSpeakerTracker = activeSpeakerTrackerOverride
        ?: ActiveSpeakerTracker(coroutineScope = coroutineScope)

    /** Thread-safe map of all participants (publishers and subscribers) keyed by stream ID */
    private val participants = ConcurrentHashMap<String, Participant>()

    /** Internal flow that emits on every participant change, throttled before exposing */
    private val _participantsInternalFlow =
        MutableStateFlow<ImmutableList<Participant>>(persistentListOf())

    private val _pinnedParticipantIds = MutableStateFlow<Set<String>>(emptySet())
    override val pinnedParticipantIds: StateFlow<Set<String>> = _pinnedParticipantIds
        .map { ids -> ids.filter { participants.containsKey(it) }.toSet() }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet(),
        )

    override fun togglePinParticipant(participantId: String) {
        _pinnedParticipantIds.update { current ->
            if (participantId in current) current - participantId else current + participantId
        }
    }

    override fun forceMuteParticipant(participantId: String) {
        (participants[participantId] as? ParticipantState)?.let {
            session.forceMuteStream(it.stream)
        }
    }

    /**
     * StateFlow of all participants sorted with screen sharing first, then pinned, then by creation time (newest first).
     * Throttled to reduce UI updates and improve performance.
     */
    override val participantsStateFlow: StateFlow<ImmutableList<Participant>> = combine(
        _participantsInternalFlow
            .sample(PARTICIPANTS_DEBOUNCE_MILLIS)
            .distinctUntilChanged(),
        _pinnedParticipantIds,
    ) { participants, pinnedIds ->
        participants.sorted(pinnedIds)
    }.stateIn(
        scope = coroutineScope,
        started = WhileSubscribed(SUBSCRIBE_TIMEOUT_MILLIS),
        initialValue = persistentListOf(),
    )

    /**
     * StateFlow of the local publisher (the current user's camera/screen).
     * Extracted from the participants list for convenient access.
     */
    override val publisher: StateFlow<PublisherState?> = _participantsInternalFlow
        .map { participants ->
            participants.firstOrNull { it.id == PUBLISHER_ID }?.let { it as PublisherState }
        }
        .stateIn(
            scope = coroutineScope,
            started = WhileSubscribed(SUBSCRIBE_TIMEOUT_MILLIS),
            initialValue = null,
        )

    private val _activeSpeaker = MutableStateFlow<Participant?>(null)

    /**
     * StateFlow of the currently active speaker based on audio level analysis.
     * Debounced to prevent rapid changes when multiple people speak.
     */
    override val activeSpeaker: StateFlow<Participant?> = _activeSpeaker
        .debounce(ACTIVE_SPEAKER_DEBOUNCE_MILLIS)
        .distinctUntilChanged()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    private val _participantsCount = MutableStateFlow(0)
    override val participantsCount: StateFlow<Int> = _participantsCount

    private val _signalStateFlow = MutableStateFlow<SignalState?>(null)
    override val signalStateFlow: StateFlow<SignalState?> = _signalStateFlow

    private val _captionsStateFlow =
        MutableStateFlow<ImmutableList<CaptionLine>>(persistentListOf())
    override val captionsStateFlow: StateFlow<ImmutableList<CaptionLine>> = _captionsStateFlow

    private val captionsHideScheduler = CaptionsHideScheduler(
        coroutineScope = coroutineScope,
    ) { streamId ->
        _captionsStateFlow.update { lines ->
            lines.filter { it.streamId != streamId }.toImmutableList()
        }
    }

    /** Hidden subscriber for receiving captions from the publisher's own stream */
    private var selfCaptionsSubscriber: VonageSubscriber? = null
    
    /** Tracks whether captions are currently enabled */
    private var captionsEnabled: Boolean = false
    
    /** Flag for deferred self-captions subscription when publisher stream not yet available */
    private var pendingSelfCaptionsSubscription: Boolean = false

    private val _archivingStateFlow = MutableStateFlow<ArchivingState>(ArchivingState.Idle)
    override val archivingStateFlow: StateFlow<ArchivingState> = _archivingStateFlow

    private val signalState: SignalFlows = mutableMapOf()
    override fun signalState(signalType: SignalType): StateFlow<SignalStateContent?> =
        signalPlugins
            .filter { it.canHandle(signalType.signal) }
            .map { it.output }
            .firstOrNull() ?: MutableStateFlow(null)

    override val chatSignalState: StateFlow<ChatState?> =
        signalState(SignalType.CHAT)
            .map { it as? ChatState }
            .stateIn(scope = coroutineScope, started = SharingStarted.Lazily, initialValue = null)

    override val emojiSignalState: StateFlow<EmojiState?> =
        signalState(SignalType.REACTION)
            .map { it as? EmojiState }
            .stateIn(scope = coroutineScope, started = SharingStarted.Lazily, initialValue = null)

    //region Session lifecycle

    /**
     * Connects to the video session and returns a flow of session events.
     *
     * Establishes the session connection, publishes the local camera stream, and starts
     * listening for remote streams and signals. The returned flow emits events for
     * connection, stream changes, and errors.
     *
     * @param context Android context needed for subscriber creation
     * @return Flow of SessionEvent indicating connection state and stream changes
     */
    override fun connect(context: Context): Flow<SessionEvent> = callbackFlow {
        this@Call.context = context
        session.setSessionListener(object : VonageSessionListener {
            override fun onConnected() {
                publishToSession()
                startActiveSpeakerTracker()
                startListeningSignals()
                trySend(SessionEvent.Connected)
            }

            override fun onDisconnected() {
                trySend(SessionEvent.Disconnected)
            }

            override fun onStreamReceived(stream: VonageStream) {
                addSubscriber(stream)
                trySend(SessionEvent.StreamReceived(stream.streamId))
            }

            override fun onStreamDropped(stream: VonageStream) {
                removeSubscriber(stream)
                trySend(SessionEvent.StreamDropped(stream.streamId))
            }

            override fun onError(error: VonageError) {
                trySend(SessionEvent.Error(error))
            }

            override fun onStreamPropertyChanged(streamId: String, hasVideo: Boolean, hasAudio: Boolean) {
                (participants[streamId] as? ParticipantState)?.updateStreamProperties(hasVideo, hasAudio)
            }
        })
        session.setSignalListener { type, data, conn ->
            signalPlugins.forEach { plugin ->
                val isYou = publisher()?.connectionId == conn?.connectionId
                val senderName = if (!isYou && conn != null) {
                    conn.extractSenderName(participants.values)
                } else {
                    ""
                }
                plugin.handleSignal(type.orEmpty(), data.orEmpty(), senderName, isYou)
            }
        }
        session.setArchiveListener(object : VonageArchiveListener {
            override fun onArchiveStarted(id: String, name: String?) {
                _archivingStateFlow.update { ArchivingState.Started(id) }
            }

            override fun onArchiveStopped(id: String) {
                _archivingStateFlow.update { ArchivingState.Stopped(id) }
            }
        })
        session.connect(token)
        awaitClose { session.setSessionListener(null) }
    }

    /**
     * Pauses the session when the app goes to background.
     * Reduces resource usage while maintaining the connection.
     */
    override fun pauseSession() {
        vonageLogger.d(TAG, "Session paused")
        session.pause()
    }

    /**
     * Resumes the session when the app returns to foreground.
     * Restores full video and audio functionality.
     */
    override fun resumeSession() {
        vonageLogger.d(TAG, "Session resumed")
        session.resume()
    }

    /**
     * Ends the session and cleans up all resources.
     * Unpublishes the local stream and disconnects from the session.
     */
    override fun endSession() {
        // Defensive cleanup of hidden self-captions subscriber
        cleanupHiddenSelfSubscriber()
        
        publisher()?.vonagePublisher?.let { 
            it.setPublisherListener(null)
            session.unpublish(it) 
        }

        session.setSessionListener(null)
        session.setSignalListener(null)
        session.disconnect()

        coroutineScope.cancel()
        participantsOnScreenJob?.cancel()
        activeSpeakerTrackerJob?.cancel()
        signalsJob?.cancel()
    }
    //endregion

    //region Signals

    /**
     * Sends an emoji reaction that will be displayed to all participants.
     *
     * @param emoji The emoji character to send
     */
    override fun sendEmoji(emoji: String) {
        sendSignal(SignalType.REACTION, emoji)
    }

    /**
     * Sends a chat message to all participants.
     *
     * @param message The text message to send
     */
    override fun sendChatMessage(message: String) {
        sendSignal(SignalType.CHAT, message)
    }

    /**
     * Internal helper to route signals to appropriate plugins and send to session.
     */
    private fun sendSignal(signalType: SignalType, data: String) {
        signalPlugins
            .filter { it.canHandle(signalType.signal) }
            .forEach { plugin ->
                plugin.sendSignal(
                    senderName = publisher()?.vonagePublisher?.name.orEmpty(),
                    message = data
                ).let {
                    session.sendSignal(it.type, it.data)
                }
            }
    }

    /**
     * Enables or disables tracking of unread chat messages.
     *
     * @param enable True to start tracking unread messages, false to stop
     */
    override fun listenUnreadChatMessages(enable: Boolean) {
        signalPlugins
            .filterIsInstance<ChatSignalPlugin>()
            .forEach { chatPlugin -> chatPlugin.listenUnread(enable) }
    }

    /**
     * Initializes signal listening by mapping plugin outputs to signal type flows.
     */
    private fun startListeningSignals() {
        signalsJob?.cancel()
        signalsJob = coroutineScope.launch {
            signalPlugins.forEach {
                if (it.canHandle(SignalType.CHAT.signal)) {
                    signalState[SignalType.CHAT] = it.output
                }
                if (it.canHandle(SignalType.REACTION.signal)) {
                    signalState[SignalType.REACTION] = it.output
                }
            }
        }
    }
    //endregion

    //region Publisher

    /**
     * Helper to get the current publisher from participants map.
     */
    private fun publisher(): PublisherState? = (participants[PUBLISHER_ID] as? PublisherState)

    /**
     * Toggles the local video on/off.
     */
    override fun toggleLocalVideo() {
        publisher()?.toggleVideo()
    }

    /**
     * Switches between front and back camera.
     */
    override fun toggleLocalCamera() {
        publisher()?.cycleCamera()
    }

    /**
     * Toggles the local audio (microphone) on/off.
     */
    override fun toggleLocalAudio() {
        publisher()?.toggleAudio()
    }

    /**
     * Applies a video effect to the local publisher.
     */
    override fun applyLocalVideoEffect(effect: VideoEffect) {
        publisher()?.applyVideoEffect(effect)
    }

    /**
     * Applies a video bitrate configuration to the publisher at runtime.
     */
    override fun setVideoBitrate(config: VideoBitrateConfig) {
        publisher()?.applyVideoBitrate(config)
    }

    /**
     * Applies a degradation preference to the publisher at runtime.
     */
    override fun setDegradationPreference(preference: DegradationPreference) {
        publisher()?.applyDegradationPreference(preference)
    }

    /**
     * Refreshes the publisher by unpublishing, destroying, recreating, and republishing.
     * The publisher factory must be reconfigured with the new settings before calling.
     */
    override fun refreshPublisher(context: Context) {
        vonageLogger.i("PublisherFactory", "Refresh publisher")
        coroutineScope.launch(Dispatchers.Default) {
            // Clean up hidden self-subscriber before refreshing publisher
            cleanupHiddenSelfSubscriber()
            
            val old = publisher() ?: return@launch
            val wasVideoOn = old.isCameraEnabled.value
            val wasAudioOn = old.isMicEnabled.value
            val videoEffect = old.videoEffect.value
            val cameraIndex = old.camera.value.index
            val name = old.name

            old.clean()

            publisherFactory.destroyPublisher()
            participants.remove(PUBLISHER_ID)

            vonageLogger.i("PublisherFactory", "Publisher name = $name")

            publisherFactory.init(
                publisherFactory.currentConfig!!.copy(
                    name = name,
                    publishVideo = wasVideoOn,
                    publishAudio = wasAudioOn,
                    initialVideoEffect = videoEffect,
                    cameraIndex = cameraIndex,
                ),
            )
            val newPublisher = withContext(Dispatchers.Main) {
                val publisherState = publisherFactory.createPublisherState(context)
                session.publish(publisherState.vonagePublisher)
                
                // Attach listener to detect when publisher stream becomes available
                publisherState.vonagePublisher.setPublisherListener(object : VonagePublisherKitListener {
                    override fun onStreamCreated(stream: VonageStream) {
                        // Register publisher stream in session's streamMap for self-subscription
                        session.registerPublisherStream(publisherState.vonagePublisher)
                        
                        if (pendingSelfCaptionsSubscription && captionsEnabled) {
                            createHiddenSelfSubscriber(stream)
                            pendingSelfCaptionsSubscription = false
                        }
                    }
                    
                    override fun onStreamDestroyed(stream: VonageStream) {
                        // Unregister publisher stream from session's streamMap
                        session.unregisterPublisherStream(publisherState.vonagePublisher)
                    }
                    
                    override fun onError(error: VonageError) {
                        vonageLogger.e(TAG, "Publisher error: ${error.message}")
                    }
                })
                
                publisherState
            }
            participants[PUBLISHER_ID] = newPublisher
            coroutineScope.launch { newPublisher.setup() }
            updateParticipants()
        }
    }

    /**
     * Publishes the local camera stream to the session.
     * Called automatically when connecting to the session.
     */
    private fun publishToSession() {
        coroutineScope.launch(Dispatchers.Default) {
            val newPublisher = withContext(Dispatchers.Main) {
                val publisherState = publisherFactory.createPublisherState(context)
                session.publish(publisherState.vonagePublisher)
                
                // Attach listener to detect when publisher stream becomes available
                publisherState.vonagePublisher.setPublisherListener(object : VonagePublisherKitListener {
                    override fun onStreamCreated(stream: VonageStream) {
                        // Register publisher stream in session's streamMap for self-subscription
                        session.registerPublisherStream(publisherState.vonagePublisher)
                        
                        if (pendingSelfCaptionsSubscription && captionsEnabled) {
                            createHiddenSelfSubscriber(stream)
                            pendingSelfCaptionsSubscription = false
                        }
                    }
                    
                    override fun onStreamDestroyed(stream: VonageStream) {
                        // Unregister publisher stream from session's streamMap
                        session.unregisterPublisherStream(publisherState.vonagePublisher)
                    }
                    
                    override fun onError(error: VonageError) {
                        vonageLogger.e(TAG, "Publisher error: ${error.message}")
                    }
                })
                
                publisherState
            }
            participants[PUBLISHER_ID] = newPublisher
            coroutineScope.launch { newPublisher.setup() }
            updateParticipants()
        }
    }
    //endregion

    //region Screen sharing

    /**
     * Starts screen sharing using the provided MediaProjection.
     *
     * Creates a separate publisher for the screen stream with video only (no audio).
     *
     * @param mediaProjection Android MediaProjection for capturing screen content
     */
    override fun startCapturingScreen(mediaProjection: MediaProjection) {
        coroutineScope.launch(Dispatchers.Default) {
            val name = "${publisher()?.vonagePublisher?.name}'s Screen"
            val publisher = withContext(Dispatchers.Main) {
                val screenPublisherState = publisherFactory.createScreenSharePublisherState(
                    context = context,
                    mediaProjection = mediaProjection,
                    name = name,
                )
                session.publish(screenPublisherState.vonagePublisher)
                screenPublisherState
            }
            participants[PUBLISHER_SCREEN_ID] = publisher
            updateParticipants()
        }
    }

    /**
     * Stops screen sharing and removes the screen publisher.
     */
    override fun stopCapturingScreen() {
        (participants[PUBLISHER_SCREEN_ID] as PublisherState).vonagePublisher.let {
            session.unpublish(it)
        }
        participants.remove(PUBLISHER_SCREEN_ID)
        updateParticipants()
    }
    //endregion

    //region Captions

    /**
     * Creates a [VonageCaptionsListener] that upserts a [CaptionLine] into [_captionsStateFlow]
     * and drives the hide-cooldown scheduler.
     *
     * @param isMe `true` for the local publisher's hidden self-subscriber so the UI can
     *              render the "You" label instead of the stream name.
     */
    private fun captionsListenerFor(isMe: Boolean): VonageCaptionsListener =
        VonageCaptionsListener { name, streamId, text, isFinal ->
            // Cancel any pending hide — the stream is either still active or
            // about to schedule a fresh cooldown.
            captionsHideScheduler.cancel(streamId)
            _captionsStateFlow.update { lines ->
                val line = CaptionLine(streamId = streamId, subscriberName = name, isMe = isMe, text = text)
                lines.filter { it.streamId != streamId }.plus(line).toImmutableList()
            }
            if (isFinal) {
                captionsHideScheduler.schedule(streamId)
            }
        }

    /** Captions listener for remote participants ([isMe] = `false`). */
    private val captionsDelegate: VonageCaptionsListener = captionsListenerFor(isMe = false)

    /** Captions listener for the local publisher's hidden self-subscriber ([isMe] = `true`). */
    private val selfCaptionsDelegate: VonageCaptionsListener = captionsListenerFor(isMe = true)

    /**
     * Enables captions.
     */
    override fun enableCaptions() {
        setCaptions(true)
    }

    /**
     * Disables captions.
     */
    override fun disableCaptions() {
        captionsHideScheduler.cancelAll()
        _captionsStateFlow.update { persistentListOf() }
        setCaptions(false)
    }

    private fun setCaptions(enable: Boolean) {
        coroutineScope.launch {
            captionsEnabled = enable
            
            if (enable) {
                // Enable captions on all existing remote subscribers
                participants.values.filterIsInstance<ParticipantState>()
                    .forEach { participant -> 
                        participant.vonageSubscriber.subscribeToCaptions = true 
                    }
                
                // Create hidden self-subscriber
                val publisherVonagePublisher = publisher()?.vonagePublisher
                val publisherStream = publisherVonagePublisher?.stream
                if (publisherStream != null) {
                    // Ensure publisher stream is registered before subscribing
                    session.registerPublisherStream(publisherVonagePublisher)
                    createHiddenSelfSubscriber(publisherStream)
                } else {
                    pendingSelfCaptionsSubscription = true
                }
            } else {
                // Disable captions on all remote subscribers
                participants.values.filterIsInstance<ParticipantState>()
                    .forEach { participant -> 
                        participant.vonageSubscriber.subscribeToCaptions = false 
                    }
                
                // Clean up hidden self-subscriber
                cleanupHiddenSelfSubscriber()
                pendingSelfCaptionsSubscription = false
            }
        }
    }

    /**
     * Creates a hidden subscriber for the publisher's own stream to receive self-captions.
     * Configures the subscriber with audio and video disabled to avoid echo and rendering overhead.
     */
    @Suppress("TooGenericExceptionCaught")
    private fun createHiddenSelfSubscriber(stream: VonageStream) {
        coroutineScope.launch {
            try {
                val subscriber = withContext(Dispatchers.Main) {
                    session.subscribe(context, stream)
                }
                subscriber.subscribeToAudio = false
                subscriber.subscribeToVideo = false
                subscriber.subscribeToCaptions = true
                subscriber.setCaptionsListener(selfCaptionsDelegate)
                selfCaptionsSubscriber = subscriber
                vonageLogger.d(TAG, "Created hidden self-captions subscriber for stream ${stream.streamId}")
            } catch (e: Exception) {
                vonageLogger.e(TAG, "Failed to create hidden self-captions subscriber", e)
            }
        }
    }

    /**
     * Cleans up the hidden self-captions subscriber.
     * Removes listeners and unsubscribes from the session.
     */
    private fun cleanupHiddenSelfSubscriber() {
        selfCaptionsSubscriber?.let { subscriber ->
            subscriber.setCaptionsListener(null)
            session.unsubscribe(subscriber)
            selfCaptionsSubscriber = null
            vonageLogger.d(TAG, "Cleaned up hidden self-captions subscriber")
        }
    }
    //endregion

    //region Subscribers

    /**
     * Creates and subscribes to a remote participant's stream.
     * Adds the subscriber to the participants map and starts audio level monitoring.
     */
    @Suppress("TooGenericExceptionCaught")
    private fun addSubscriber(stream: VonageStream) {
        vonageLogger.d(TAG, "Add subscriber ${stream.name} [${stream.streamId}]")
        coroutineScope.launch {
            try {
                val participant = withContext(Dispatchers.Main) {
                    val subscriber = session.subscribe(context, stream)
                    subscriber.setCaptionsListener(captionsDelegate)
                    subscriber.subscribeToCaptions = captionsEnabled
                    ParticipantState(vonageSubscriber = subscriber)
                }
                launch { participant.setup() }
                participants[stream.streamId] = participant
                updateParticipants()
                observeSubscriberAudioLevel(participant)
            } catch (e: Exception) {
                vonageLogger.e(TAG, "Failed to add subscriber ${stream.streamId}", e)
            }
        }
    }

    /**
     * Observes audio levels for a participant and feeds them to the active speaker tracker.
     */
    private fun observeSubscriberAudioLevel(participant: Participant) {
        coroutineScope.launch {
            participant.audioLevel.collect { movingAvg ->
                activeSpeakerTracker.onSubscriberAudioLevelUpdated(
                    streamId = participant.id,
                    movingAvg = movingAvg
                )
            }
        }
    }

    /**
     * Removes a subscriber when their stream is dropped.
     * Cleans up resources and updates the active speaker if needed.
     */
    private fun removeSubscriber(stream: VonageStream) {
        val subscriber = participants[stream.streamId] ?: return
        coroutineScope.launch {
            activeSpeakerTracker.onSubscriberDestroyed(stream.streamId)
            participants.remove(stream.streamId)
            updateParticipants()
        }
        subscriber.clean(session)
    }
    //endregion

    /**
     * Updates participant visibility based on UI snapshot state.
     *
     * Optimizes bandwidth by disabling video for off-screen participants while
     * ensuring the active speaker always has video enabled.
     *
     * @param snapshotFlow Flow emitting lists of currently visible participant IDs
     */
    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {
        if (!VISIBILITY_MONITOR_ENABLED) return
        participantsOnScreenJob?.cancel()
        participantsOnScreenJob = coroutineScope.launch(Dispatchers.Default) {
            snapshotFlow
                .distinctUntilChanged()
                .collectLatest { visibleParticipants ->
                    if (visibleParticipants.isEmpty()) return@collectLatest
                    val activeSpeakerId = activeSpeaker.value?.id
                    participants.forEach { (key, participantState) ->
                        if (participantState.isPublisher) return@forEach
                        val isVisible =
                            visibleParticipants.contains(key) || (key == activeSpeakerId)
                        participantState.changeVisibility(isVisible)
                    }
                }
        }
    }

    /**
     * Starts monitoring active speaker changes and updates visibility accordingly.
     * Ensures the active speaker is always visible even if scrolled off-screen.
     * Screen sharing participants are automatically set as the active speaker.
     * Camera-off participants are not promoted to the spotlight.
     */
    private fun startActiveSpeakerTracker() {
        activeSpeakerTrackerJob?.cancel()
        activeSpeakerTrackerJob = activeSpeakerTracker.activeSpeakerChanges
            .onEach { payload ->
                participants[payload.newActiveSpeaker.streamId]?.let { mainSpeaker ->
                    // Screen share always wins regardless of camera state.
                    val screenSharingParticipant = participants.values.firstScreenSharing()
                    when {
                        screenSharingParticipant != null -> {
                            _activeSpeaker.update { screenSharingParticipant }
                        }
                        // Only promote participants with their camera on.
                        // A camera-off participant talking does not deserve the spotlight.
//                        mainSpeaker.isCameraEnabled.value -> {
//                            mainSpeaker.changeVisibility(true)
//                            _activeSpeaker.update { mainSpeaker }
//                        }
                    }
                }
            }
            .launchIn(coroutineScope)
    }

    /**
     * Updates the participants flow and count whenever the participants map changes.
     * Also updates active speaker if a screen sharing participant is present.
     * If the current active speaker left, clears it or sets to screen sharing participant.
     */
    private fun updateParticipants() {
        _participantsInternalFlow.update { participants.values.toImmutableList() }
        _participantsCount.update { participants.size }

        // Set screen sharing participant as active speaker
        val screenSharingParticipant = participants.values.firstScreenSharing()
        // Update active speaker: prioritize screen sharing, or clear if current speaker left
        _activeSpeaker.update { currentSpeaker ->
            when {
                screenSharingParticipant != null -> screenSharingParticipant
                currentSpeaker != null && participants.containsKey(currentSpeaker.id) -> currentSpeaker
                else -> null
            }
        }
    }

    companion object {
        private const val TAG: String = "CallFacade"
        private const val SUBSCRIBE_TIMEOUT_MILLIS = 10000L
        const val PUBLISHER_ID: String = "publisher"
        const val PUBLISHER_SCREEN_ID: String = "publisher-screen"

        private const val PARTICIPANTS_DEBOUNCE_MILLIS = 100L
        private const val ACTIVE_SPEAKER_DEBOUNCE_MILLIS = 250L
        private const val VISIBILITY_MONITOR_ENABLED = false
    }
}
