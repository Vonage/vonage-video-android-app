package com.vonage.android.kotlin

import android.content.Context
import android.media.projection.MediaProjection
import android.util.Log
import androidx.compose.runtime.Stable
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit.PublisherKitVideoType
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import com.vonage.android.kotlin.ext.extractSenderName
import com.vonage.android.kotlin.ext.firstScreenSharing
import com.vonage.android.kotlin.ext.mapSorted
import com.vonage.android.kotlin.ext.name
import com.vonage.android.kotlin.internal.ActiveSpeakerTracker
import com.vonage.android.kotlin.internal.ScreenSharingCapturer
import com.vonage.android.kotlin.internal.VeraPublisherHolder
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalFlows
import com.vonage.android.kotlin.model.SignalState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.kotlin.signal.SignalPlugin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
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
 * @param session Vonage session instance
 * @param publisherHolder Container for publisher instances
 * @param signalPlugins List of plugins for handling custom signals
 * @param coroutineDispatcher Dispatcher for coroutine operations (defaults to IO)
 */
@OptIn(FlowPreview::class)
@Stable
class Call internal constructor(
    private val token: String,
    private val session: Session,
    private val publisherHolder: VeraPublisherHolder,
    private val signalPlugins: List<SignalPlugin>,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CallFacade {

    private lateinit var context: Context

    private val coroutineScope = CoroutineScope(SupervisorJob() + coroutineDispatcher)
    private var participantsOnScreenJob: Job? = null
    private var activeSpeakerTrackerJob: Job? = null
    private var signalsJob: Job? = null

    /** Tracks active speaker based on audio levels across all participants */
    private val activeSpeakerTracker = ActiveSpeakerTracker(coroutineScope = coroutineScope)
    
    /** Thread-safe map of all participants (publishers and subscribers) keyed by stream ID */
    private val participants = ConcurrentHashMap<String, Participant>()

    /** Internal flow that emits on every participant change, throttled before exposing */
    private val _participantsInternalFlow = MutableStateFlow<ImmutableList<Participant>>(persistentListOf())
    
    /**
     * StateFlow of all participants sorted with screen sharing first, then by creation time (newest first).
     * Throttled to reduce UI updates and improve performance.
     */
    override val participantsStateFlow: StateFlow<ImmutableList<Participant>> = _participantsInternalFlow
        .sample(PARTICIPANTS_DEBOUNCE_MILLIS)
        .distinctUntilChanged()
        .mapSorted()
        .stateIn(
            scope = coroutineScope,
            started = WhileSubscribed(SUBSCRIBE_TIMEOUT_MILLIS),
            initialValue = persistentListOf(),
        )

    /**
     * StateFlow of the local publisher (the current user's camera/screen).
     * Extracted from the participants list for convenient access.
     */
    override val publisher: StateFlow<PublisherState?> = _participantsInternalFlow
        .map { participants -> participants.firstOrNull { it.id == PUBLISHER_ID }?.let { it as PublisherState } }
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
            started = WhileSubscribed(SUBSCRIBE_TIMEOUT_MILLIS),
            initialValue = null,
        )

    private val _participantsCount = MutableStateFlow(0)
    override val participantsCount: StateFlow<Int> = _participantsCount

    private val _signalStateFlow = MutableStateFlow<SignalState?>(null)
    override val signalStateFlow: StateFlow<SignalState?> = _signalStateFlow

    private val _captionsStateFlow = MutableStateFlow<String?>(null)
    override val captionsStateFlow: StateFlow<String?> = _captionsStateFlow

    private val _archivingStateFlow = MutableStateFlow<String?>(null)
    override val archivingStateFlow: StateFlow<String?> = _archivingStateFlow

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
        val sessionListener = object : Session.SessionListener {
            override fun onConnected(session: Session) {
                publishToSession()
                startActiveSpeakerTracker()
                startListeningSignals()
                trySend(SessionEvent.Connected)
            }

            override fun onDisconnected(session: Session) {
                trySend(SessionEvent.Disconnected)
            }

            override fun onStreamReceived(session: Session, stream: Stream) {
                addSubscriber(stream)
                trySend(SessionEvent.StreamReceived(stream.streamId))
            }

            override fun onStreamDropped(session: Session, stream: Stream) {
                removeSubscriber(stream)
                trySend(SessionEvent.StreamDropped(stream.streamId))
            }

            override fun onError(session: Session, error: OpentokError) {
                trySend(SessionEvent.Error(error))
            }
        }
        session.setSessionListener(sessionListener)
        session.setSignalListener { _, type, data, conn ->
            signalPlugins.forEach { plugin ->
                val isYou = publisher()?.connectionId == conn.connectionId
                val senderName = if (!isYou) {
                    conn.extractSenderName(participants.values)
                } else {
                    ""
                }
                plugin.handleSignal(type, data, senderName, isYou)
            }
        }
        session.setArchiveListener(object : Session.ArchiveListener {
            override fun onArchiveStarted(session: Session, id: String, name: String?) {
                _archivingStateFlow.update { "started" }
            }

            override fun onArchiveStopped(session: Session, id: String) {
                _archivingStateFlow.update { "stopped" }
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
        Log.d(TAG, "Session paused")
        session.onPause()
    }

    /**
     * Resumes the session when the app returns to foreground.
     * Restores full video and audio functionality.
     */
    override fun resumeSession() {
        Log.d(TAG, "Session resumed")
        session.onResume()
    }

    /**
     * Ends the session and cleans up all resources.
     * Unpublishes the local stream and disconnects from the session.
     */
    override fun endSession() {
        // Wait for PublisherListener#streamDestroyed before returning (VIDSOL-104)
        session.unpublish(publisher()?.publisher)

        session.setSessionListener(null)
        session.setSignalListener(null)
        session.disconnect()
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
                plugin.sendSignal(senderName = publisher()?.publisher?.name.orEmpty(), message = data).let {
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
     * Publishes the local camera stream to the session.
     * Called automatically when connecting to the session.
     */
    private fun publishToSession() {
        coroutineScope.launch(Dispatchers.Default) {
            publisherHolder.let { holder ->
                val publisher = withContext(Dispatchers.Main) {
                    session.publish(holder.publisher)
                    PublisherState(publisherId = PUBLISHER_ID, publisher = holder.publisher)
                }
                participants[PUBLISHER_ID] = publisher
                coroutineScope.launch { publisher.setup() }
                updateParticipants()
            }
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
            val name = "${publisherHolder.publisher.name}'s Screen" // translate this!
            val publisher = withContext(Dispatchers.Main) {
                val screenPublisher = Publisher.Builder(context)
                    .name(name)
                    .capturer(ScreenSharingCapturer(context, mediaProjection))
                    .build()
                    .apply {
                        renderer?.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FIT)
                        publishVideo = true
                        publishAudio = false
                        publisherVideoType = PublisherKitVideoType.PublisherKitVideoTypeScreen
                    }
                session.publish(screenPublisher)
                publisherHolder.screenPublisher = screenPublisher
                PublisherState(
                    publisherId = PUBLISHER_SCREEN_ID,
                    publisher = screenPublisher,
                )
            }
            participants[PUBLISHER_SCREEN_ID] = publisher
            updateParticipants()
        }
    }

    /**
     * Stops screen sharing and removes the screen publisher.
     */
    override fun stopCapturingScreen() {
        publisherHolder.screenPublisher?.let {
            session.unpublish(it)
        }
        participants.remove(PUBLISHER_SCREEN_ID)
        updateParticipants()
    }
    //endregion

    //region Captions
    
    /**
     * Listener for receiving captions from remote participants.
     * Updates the captions state flow with speaker name and text.
     */
    private val captionsDelegate: SubscriberKit.CaptionsListener =
        SubscriberKit.CaptionsListener { subscriber, text, isFinal ->
            _captionsStateFlow.update { _ -> "${subscriber.name()}: $text" }
            if (isFinal) {
                _captionsStateFlow.update { _ -> null }
            }
        }

    /**
     * Enables or disables captions for all participants.
     *
     * @param enable True to enable captions, false to disable
     */
    override fun enableCaptions(enable: Boolean) {
        coroutineScope.launch {
            publisher()?.publisher?.publishCaptions = enable
            participants.values.filterIsInstance<ParticipantState>()
                .forEach { participant -> participant.subscriber.subscribeToCaptions = enable }
        }
    }
    //endregion

    //region Subscribers
    
    /**
     * Creates and subscribes to a remote participant's stream.
     * Adds the subscriber to the participants map and starts audio level monitoring.
     */
    @Suppress("TooGenericExceptionCaught")
    private fun addSubscriber(stream: Stream) {
        coroutineScope.launch {
            try {
                val participant = withContext(Dispatchers.Main) {
                    val subscriber = Subscriber.Builder(context, stream).build()
                    subscriber.setCaptionsListener(captionsDelegate)
                    session.subscribe(subscriber)
                    ParticipantState(subscriber = subscriber)
                }
                launch { participant.setup() }
                participants[stream.streamId] = participant
                updateParticipants()
                observeSubscriberAudioLevel(participant)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add subscriber ${stream.streamId}", e)
            }
        }
    }

    /**
     * Observes audio levels for a participant and feeds them to the active speaker tracker.
     */
    private fun observeSubscriberAudioLevel(participant: Participant) {
        coroutineScope.launch {
            participant.audioLevel.collect { movingAvg ->
                activeSpeakerTracker.onSubscriberAudioLevelUpdated(streamId = participant.id, movingAvg = movingAvg)
            }
        }
    }

    /**
     * Removes a subscriber when their stream is dropped.
     * Cleans up resources and updates the active speaker if needed.
     */
    private fun removeSubscriber(stream: Stream) {
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
                        val isVisible = visibleParticipants.contains(key) || (key == activeSpeakerId)
                        participantState.changeVisibility(isVisible)
                    }
                }
        }
    }

    /**
     * Starts monitoring active speaker changes and updates visibility accordingly.
     * Ensures the active speaker is always visible even if scrolled off screen.
     * Screen sharing participants are automatically set as the active speaker.
     */
    private fun startActiveSpeakerTracker() {
        activeSpeakerTrackerJob?.cancel()
        activeSpeakerTrackerJob = activeSpeakerTracker.activeSpeakerChanges
            .onEach { payload ->
                participants[payload.newActiveSpeaker.streamId]?.let { mainSpeaker ->
                    mainSpeaker.changeVisibility(true)
                    // Override active speaker if someone is screen sharing
                    val screenSharingParticipant = participants.values.firstScreenSharing()
                    _activeSpeaker.update { screenSharingParticipant ?: mainSpeaker }
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
        private const val TAG: String = "Call"
        private const val SUBSCRIBE_TIMEOUT_MILLIS = 10000L
        const val PUBLISHER_ID: String = "publisher"
        const val PUBLISHER_SCREEN_ID: String = "publisher-screen"

        private const val PARTICIPANTS_DEBOUNCE_MILLIS = 100L
        private const val ACTIVE_SPEAKER_DEBOUNCE_MILLIS = 100L
        private const val VISIBILITY_MONITOR_ENABLED = true
    }
}
