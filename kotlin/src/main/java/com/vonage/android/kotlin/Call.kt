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

@Suppress("TooManyFunctions")
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

    private val activeSpeakerTracker = ActiveSpeakerTracker(coroutineScope = coroutineScope)
    private val participants = ConcurrentHashMap<String, Participant>()

    private val _participantsInternalFlow = MutableStateFlow<ImmutableList<Participant>>(persistentListOf())
    override val participantsStateFlow: StateFlow<ImmutableList<Participant>> = _participantsInternalFlow
        .sample(PARTICIPANTS_DEBOUNCE_MILLIS)
        .distinctUntilChanged()
        .map { participants -> participants.sortedByDescending { it.creationTime }.toImmutableList() }
        .stateIn(
            scope = coroutineScope,
            started = WhileSubscribed(SUBSCRIBE_TIMEOUT_MILLIS),
            initialValue = persistentListOf(),
        )

    override val publisher: StateFlow<PublisherState?> = _participantsInternalFlow
        .map { participants -> participants.firstOrNull { it.id == PUBLISHER_ID }?.let { it as PublisherState } }
        .stateIn(
            scope = coroutineScope,
            started = WhileSubscribed(SUBSCRIBE_TIMEOUT_MILLIS),
            initialValue = null,
        )

    private val _activeSpeaker = MutableStateFlow<Participant?>(null)
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

    //region Session lifecycle
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
    override fun toggleLocalVideo() {
        (participants[PUBLISHER_ID] as PublisherState).toggleVideo()
    }

    override fun toggleLocalCamera() {
        publisherHolder.publisher.cycleCamera()
    }

    override fun toggleLocalAudio() {
        (participants[PUBLISHER_ID] as PublisherState).toggleAudio()
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
                    updateParticipants()
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

    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {
        if (!VISIBILITY_MONITOR_ENABLED) return
        participantsOnScreenJob?.cancel()
        participantsOnScreenJob = coroutineScope.launch(Dispatchers.Default) {
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

    private fun observeSubscriberAudioLevel(participant: Participant) {
        coroutineScope.launch {
            participant.audioLevel.collect { movingAvg ->
                activeSpeakerTracker.onSubscriberAudioLevelUpdated(streamId = participant.id, movingAvg = movingAvg)
            }
        }
    }

    private fun updateParticipants() {
        _participantsInternalFlow.update { participants.values.toImmutableList() }
        _participantsCount.update { participants.size }
    }

    private fun removeSubscriber(stream: Stream) {
        val subscriber = participants[stream.streamId] ?: return
        coroutineScope.launch {
            activeSpeakerTracker.onSubscriberDestroyed(stream.streamId)
            participants.remove(stream.streamId)
            updateParticipants()
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

    private fun startActiveSpeakerTracker() {
        activeSpeakerTrackerJob?.cancel()
        activeSpeakerTrackerJob = activeSpeakerTracker.activeSpeakerChanges
            .onEach { payload ->
                participants[payload.newActiveSpeaker.streamId]?.let { mainSpeaker ->
                    mainSpeaker.changeVisibility(true)
                    _activeSpeaker.update { mainSpeaker }
                }
            }
            .launchIn(coroutineScope)
    }

    companion object {
        private const val TAG: String = "Call"
        private const val SUBSCRIBE_TIMEOUT_MILLIS = 10000L
        const val PUBLISHER_ID: String = "publisher"
        const val PUBLISHER_SCREEN_ID: String = "publisher-screen"

        // Add the following parameters to a Config-kind object
        private const val PARTICIPANTS_DEBOUNCE_MILLIS = 100L
        private const val ACTIVE_SPEAKER_DEBOUNCE_MILLIS = 100L
        private const val VISIBILITY_MONITOR_ENABLED = true
    }
}
