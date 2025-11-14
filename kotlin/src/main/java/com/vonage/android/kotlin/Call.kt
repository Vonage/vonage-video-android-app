package com.vonage.android.kotlin

import android.content.Context
import android.media.projection.MediaProjection
import android.util.Log
import androidx.compose.runtime.Stable
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.NoOpVideoRenderer
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit.PublisherKitVideoType
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import com.opentok.android.VeraVideoRenderer
import com.vonage.android.kotlin.ext.extractSenderName
import com.vonage.android.kotlin.ext.name
import com.vonage.android.kotlin.ext.toggle
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
import com.vonage.android.kotlin.model.mapState
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
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.emptyList

@Suppress("TooManyFunctions")
@OptIn(FlowPreview::class)
@Stable
class Call internal constructor(
    private val token: String,
    private val session: Session,
    private val publisherHolder: VeraPublisherHolder,
    private val signalPlugins: List<SignalPlugin>,
    activeSpeakerTracker: ActiveSpeakerTracker? = null,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : CallFacade {

    private val VISIBILITY_MONITOR_ENABLED = true


    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val actualActiveSpeakerTracker = activeSpeakerTracker ?: ActiveSpeakerTracker(coroutineScope = coroutineScope)
    private val participants = ConcurrentHashMap<String, Participant>()

    private val _participantsRawFlow = MutableStateFlow<ImmutableList<Participant>>(persistentListOf())
    override val participantsStateFlow: StateFlow<ImmutableList<Participant>> = _participantsRawFlow
//        .debounce(PARTICIPANTS_UPDATE_DEBOUNCE)
//        .distinctUntilChanged()
        .mapState { participants -> participants.sortedByDescending { it.creationTime }.toImmutableList() }
        .onEach { Log.d("SessionTracing", "emit participants to UI") }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(10000),
            initialValue = persistentListOf()
        )

    private val _mainSpeaker = MutableStateFlow<Participant?>(null)
    override val activeSpeaker: StateFlow<Participant?> = _mainSpeaker
        .debounce(PARTICIPANTS_UPDATE_DEBOUNCE)
        .distinctUntilChanged()
        .onEach { Log.d("SessionTracing", "emit main speaker to UI") }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(10000),
            initialValue = null
        )

    private val _participantsCount = MutableStateFlow(0)
    override val participantsCount: StateFlow<Int> = _participantsCount

    private val _signalStateFlow = MutableStateFlow<SignalState?>(null)
    override val signalStateFlow: StateFlow<SignalState?> = _signalStateFlow

    private val _captionsStateFlow = MutableStateFlow<String?>(null)
    override val captionsStateFlow: StateFlow<String?> = _captionsStateFlow

    val signalState: SignalFlows = mutableMapOf()
    override fun signalState(signalType: SignalType): StateFlow<SignalStateContent?> =
        signalPlugins
            .filter { it.canHandle(signalType.signal) }
            .map { it.output }
            .firstOrNull() ?: MutableStateFlow(null)

    override fun chatSignalState(): StateFlow<ChatState?> =
        signalState(SignalType.CHAT)
            .map { it as? ChatState }
            .stateIn(scope = coroutineScope, started = SharingStarted.Lazily, initialValue = null)

    override fun emojiSignalState(): StateFlow<EmojiState?> =
        signalState(SignalType.REACTION)
            .map { it as? EmojiState }
            .stateIn(scope = coroutineScope, started = SharingStarted.Lazily, initialValue = null)

    override val publisher: StateFlow<PublisherState?> = participantsStateFlow
        .mapState { participants -> participants.firstOrNull { it.id == PUBLISHER_ID }?.let { it as PublisherState } }

    private lateinit var context: Context

    init {
        coroutineScope.launch {
            signalPlugins.forEach {
                if (it.canHandle(SignalType.CHAT.signal)) {
                    signalState[SignalType.CHAT] = it.output
                }
                if (it.canHandle(SignalType.REACTION.signal)) {
                    signalState[SignalType.REACTION] = it.output
                }
            }
        }

        actualActiveSpeakerTracker.activeSpeakerChanges
            .onEach { payload ->
                participants[payload.newActiveSpeaker.streamId]?.let {
                    it.changeVisibility(true)
                    _mainSpeaker.value = it
                    Log.d("ACTIVE SPEAKER CHANGE", "active speaker ${it.id}")
                }
            }
            .launchIn(coroutineScope)
    }

    private var currentTime = System.currentTimeMillis()

    //region Session lifecycle
    override fun connect(context: Context): Flow<SessionEvent> = callbackFlow {
        this@Call.context = context
        val sessionListener = object : Session.SessionListener {
            override fun onConnected(session: Session) {
                currentTime = System.currentTimeMillis()
                Log.d("SessionTracing", "onConnected")
                publishToSession()
                trySend(SessionEvent.Connected)
            }

            override fun onDisconnected(session: Session) {
                trySend(SessionEvent.Disconnected)
            }

            override fun onStreamReceived(session: Session, stream: Stream) {
                val now = System.currentTimeMillis()
                Log.d("SessionTracing", "onStreamReceived - ${now - currentTime}")
                Log.d("Session", "onStreamReceived ${stream.streamId}")
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
                val isYou = publisherHolder.publisher.stream.connection == conn
                val senderName = if (!isYou) {
                    conn.extractSenderName(participants.values)
                } else {
                    ""
                }
                plugin.handleSignal(type, data, senderName, isYou)
            }
        }
        session.connect(token)
        awaitClose { session.setSessionListener(null) }
    }

    override fun pauseSession() {
        Log.d(TAG, "Session paused")
        session.onPause()
    }

    override fun resumeSession() {
        Log.d(TAG, "Session resumed")
        session.onResume()
    }

    override fun endSession() {
        // wait for PublisherListener#streamDestroyed before returning : VIDSOL-104
        session.unpublish(publisherHolder.publisher)

        session.setSessionListener(null)
        session.setSignalListener(null)
        session.disconnect()
    }
    //endregion

    //region Signals

    override fun sendEmoji(emoji: String) {
        sendSignal(SignalType.REACTION, emoji)
    }

    override fun sendChatMessage(message: String) {
        sendSignal(SignalType.CHAT, message)
    }

    private fun sendSignal(signalType: SignalType, data: String) {
        signalPlugins
            .filter { it.canHandle(signalType.signal) }
            .forEach { plugin ->
                plugin.sendSignal(senderName = publisherHolder.publisher.name, message = data).let {
                    session.sendSignal(it.type, it.data)
                }
            }
    }

    override fun listenUnreadChatMessages(enable: Boolean) {
        signalPlugins
            .filterIsInstance<ChatSignalPlugin>()
            .forEach { chatPlugin -> chatPlugin.listenUnread(enable) }
    }

    //endregion

    //region Publisher
    override fun toggleLocalVideo() {
        (participants[PUBLISHER_ID] as PublisherState)._isCameraEnabled.value = true
//        publisherHolder.publisher.let { publisher ->
//            publisher.publishVideo = publisher.publishVideo.toggle()
//        }
    }

    override fun toggleLocalCamera() {
        publisherHolder.publisher.cycleCamera()
    }

    override fun toggleLocalAudio() {
        (participants[PUBLISHER_ID] as PublisherState)._isMicEnabled.value = true
        publisherHolder.publisher.let { publisher ->
            publisher.publishAudio = publisher.publishAudio.toggle()
        }
    }

    private fun publishToSession() {
        coroutineScope.launch(Dispatchers.Default) {
            publisherHolder.let { holder ->
                val publisher = withContext(Dispatchers.Main) {
                    session.publish(holder.publisher)
                    PublisherState(holder.publisher)
                }
                participants[PUBLISHER_ID] = publisher
                coroutineScope.launch {
                    async { publisher.setup() }.await()
                    updateParticipants(publisher)
                }
            }
        }
    }
    //endregion

    //region Screen sharing
    override fun startCapturingScreen(mediaProjection: MediaProjection) {
        val name = "${publisherHolder.publisher.name}'s Screen" // translate this!
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
        publisherHolder.screenPublisher = screenPublisher
        //participants[PUBLISHER_SCREEN_ID] = screenPublisher.toScreenParticipant()
        //updateParticipantsState()

        session.publish(screenPublisher)
    }

    override fun stopCapturingScreen() {
        publisherHolder.screenPublisher?.let {
            session.unpublish(it)
        }
        //participantStreams.remove(PUBLISHER_SCREEN_ID)
        //updateParticipantsState()
    }
    //endregion

    override fun enableCaptions(enable: Boolean) {
        publisherHolder.publisher.publishCaptions = enable
        //subscriberStreams.values.forEach { it.subscribeToCaptions = enable }
    }

    private var participantsVisibilityMonitor: Job? = null

    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {
        if (!VISIBILITY_MONITOR_ENABLED) return

        participantsVisibilityMonitor?.cancel()

        participantsVisibilityMonitor = coroutineScope.launch(Dispatchers.Default) {
            snapshotFlow
                .distinctUntilChanged()
                .collectLatest { visibleParticipants ->
                    if (visibleParticipants.isEmpty()) return@collectLatest
                    participants.forEach { (key, participantState) ->
                        val isVisible = visibleParticipants.contains(key) || (key == activeSpeaker.value?.id)
                        participantState.changeVisibility(isVisible)
                    }
                }
        }
    }

    private fun addSubscriber(stream: Stream) {
        Log.d(TAG, "Adding subscriber ${stream.streamId}")
        coroutineScope.launch(Dispatchers.Default) {
            try {
                // Build subscriber off main thread (renderer creation can be slow)
                val participant = withContext(Dispatchers.Main) {
                    val subscriber = Subscriber.Builder(context, stream)
                        .renderer(VeraVideoRenderer(context))
//                        .renderer(NoOpVideoRenderer(context))
                        .build()
                    session.subscribe(subscriber)
                    ParticipantState(subscriber = subscriber)
                }

                //subscriber.setCaptionsListener(captionsDelegate)
                launch { participant.setup() }

                // Add to participants map and update UI
                participants[stream.streamId] = participant
                updateParticipants(participant)
                
                Log.d(TAG, "Subscriber added successfully: ${stream.streamId}")
                val now = System.currentTimeMillis()
                Log.d("SessionTracing", "Subscriber added - ${now - currentTime}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add subscriber ${stream.streamId}", e)
            }
        }
    }

    private fun observeSubscriber(participant: Participant) {
        coroutineScope.launch {
            participant.audioLevel.collect { audioLevel ->
                Log.d(TAG, "audio level collected ${Thread.currentThread()}")
                actualActiveSpeakerTracker.onSubscriberAudioLevelUpdated(
                    participant.id,
                    audioLevel,
                )
            }
        }
    }

    private fun updateParticipants(participant: Participant) {
        _participantsRawFlow.update { participants.values.toImmutableList() }
        _participantsCount.update { participants.size }

        observeSubscriber(participant)
    }

    private fun removeSubscriber(stream: Stream) {
        val subscriber = participants[stream.streamId] ?: return
        coroutineScope.launch {
            actualActiveSpeakerTracker.onSubscriberDestroyed(stream.streamId)

            participants.remove(stream.streamId)
            updateParticipants(subscriber)
        }

        subscriber.clean(session)
    }

    private val captionsDelegate: SubscriberKit.CaptionsListener =
        SubscriberKit.CaptionsListener { subscriber, text, isFinal ->
            _captionsStateFlow.update { _ -> "${subscriber.name()}: $text" }
            if (isFinal) {
                _captionsStateFlow.update { _ -> null }
            }
        }

    companion object {
        private const val TAG: String = "Call"
        const val PUBLISHER_ID: String = "publisher"
        const val PUBLISHER_SCREEN_ID: String = "publisher-screen"
        private const val PARTICIPANTS_UPDATE_DEBOUNCE = 60L // ms - batch subscriber additions
    }
}
