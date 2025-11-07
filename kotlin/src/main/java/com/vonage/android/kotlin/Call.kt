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
import com.vonage.android.kotlin.ext.id
import com.vonage.android.kotlin.ext.name
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.ext.round2
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.internal.ActiveSpeakerTracker
import com.vonage.android.kotlin.internal.AudioLevelProcessor
import com.vonage.android.kotlin.internal.ScreenSharingCapturer
import com.vonage.android.kotlin.internal.VeraPublisherHolder
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.ParticipantState
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

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

    // todo: expose a shortcut to the publisher to improve audio/video enabling/disabling

    private val coroutineScope = CoroutineScope(coroutineDispatcher)
    private val actualActiveSpeakerTracker = activeSpeakerTracker ?: ActiveSpeakerTracker(coroutineScope = coroutineScope)
    private val audioLevelProcessor = AudioLevelProcessor(coroutineScope)

    private val _participantsStateFlow = MutableStateFlow<ImmutableList<ParticipantState>>(persistentListOf())
    override val participantsStateFlow: StateFlow<ImmutableList<ParticipantState>> = _participantsStateFlow
        .mapState { it }

    private val _participantsCount = MutableStateFlow(_participantsStateFlow.value.size)
    override val participantsCount: StateFlow<Int> = _participantsCount

    private val _signalStateFlow = MutableStateFlow<SignalState?>(null)
    override val signalStateFlow: StateFlow<SignalState?> = _signalStateFlow

    private val _captionsStateFlow = MutableStateFlow<String?>(null)
    override val captionsStateFlow: StateFlow<String?> = _captionsStateFlow

    private val _mainSpeaker = MutableStateFlow<Participant?>(null)
    override val activeSpeaker: StateFlow<Participant?> = _mainSpeaker

    private val _localAudioStateFlow = MutableStateFlow(0F)
    override val localAudioLevel: StateFlow<Float> = _localAudioStateFlow

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

    private val participants = ConcurrentHashMap<String, ParticipantState>(25)

    private lateinit var context: Context

    init {
        signalPlugins.forEach {
            if (it.canHandle(SignalType.CHAT.signal)) {
                signalState[SignalType.CHAT] = it.output
            }
            if (it.canHandle(SignalType.REACTION.signal)) {
                signalState[SignalType.REACTION] = it.output
            }
        }

        actualActiveSpeakerTracker.activeSpeakerChanges
            .onEach { payload ->
                Log.d(
                    "activeSpeakerTracker",
                    "active speaker = ${payload.newActiveSpeaker.streamId} (${Thread.currentThread()})"
                )
                participants[payload.newActiveSpeaker.streamId]?.let {
                    it.subscribeToVideo = true
                    _mainSpeaker.value = it
                }
            }
            .launchIn(coroutineScope)
    }

    override fun connect(context: Context): Flow<SessionEvent> = callbackFlow {
        this@Call.context = context
        val sessionListener = object : Session.SessionListener {
            override fun onConnected(session: Session) {
                publishToSession()
                trySend(SessionEvent.Connected)
            }

            override fun onDisconnected(session: Session) {
                trySend(SessionEvent.Disconnected)
            }

            override fun onStreamReceived(session: Session, stream: Stream) {
                Log.d(
                    "onStreamReceived",
                    "Stream received ${stream.name}-${stream.streamId} - ${Thread.currentThread()}"
                )
                addSubscriber(stream)
                trySend(SessionEvent.StreamReceived(stream.streamId))
            }

            override fun onStreamDropped(session: Session, stream: Stream) {
                Log.d("subscriber.streamDropped", "stream dropped = ${stream.streamId}")
                removeSubscriber(stream)
                trySend(SessionEvent.StreamDropped(stream.streamId))
            }

            override fun onError(session: Session, error: OpentokError) {
                Log.d("subscriber.onError", "error = ${error.message}")
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

    override fun endSession() {
        // wait for PublisherListener#streamDestroyed before returning : VIDSOL-104
        session.unpublish(publisherHolder.publisher)

        session.setSessionListener(null)
        session.setSignalListener(null)
        session.disconnect()
    }

    override fun toggleLocalVideo() {
        publisherHolder.publisher.let { publisher ->
            publisher.publishVideo = publisher.publishVideo.toggle()
        }
    }

    override fun toggleLocalCamera() {
        publisherHolder.publisher.cycleCamera()
    }

    override fun toggleLocalAudio() {
        publisherHolder.publisher.let { publisher ->
            publisher.publishAudio = publisher.publishAudio.toggle()
        }
    }

    override fun pauseSession() {
        Log.d(TAG, "Session paused")
        session.onPause()
    }

    override fun resumeSession() {
        Log.d(TAG, "Session resumed")
        session.onResume()
    }

    private fun publishToSession() {
        publisherHolder.let { holder ->
            val id = PUBLISHER_ID
            session.publish(holder.publisher)
        }
        publisherAudioLevel()
    }

    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {
        coroutineScope.launch(Dispatchers.Default) {
            snapshotFlow
                .debounce(100)
                .distinctUntilChanged()
                .collectLatest { visibleParticipants ->
                    if (visibleParticipants.isEmpty()) return@collectLatest
                    participants.forEach { (key, participantState) ->
                        val shouldSubscribe = visibleParticipants.contains(key) || (key == activeSpeaker.value?.id)
                        if (participantState.subscribeToVideo != shouldSubscribe) {
                            participantState.subscribeToVideo = shouldSubscribe
                        }
                    }
                }
        }
    }

    private fun publisherAudioLevel() {
        coroutineScope.launch {
            publisherHolder.publisher.observeAudioLevel()
                .filter { publisherHolder.publisher.publishAudio }
                .distinctUntilChanged()
                .debounce(PUBLISHER_AUDIO_LEVEL_DEBOUNCE)
                .collect { audioLevel ->
                    _localAudioStateFlow.value = audioLevel
                }
        }
    }

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

    override fun enableCaptions(enable: Boolean) {
        publisherHolder.publisher.publishCaptions = enable
        //subscriberStreams.values.forEach { it.subscribeToCaptions = enable }
    }

    private fun addSubscriber(stream: Stream) {
        val subscriber = Subscriber.Builder(context, stream).build()

        subscriber.setStreamListener(streamDelegate) // listen audio enabled/disabled
//        subscriber.setCaptionsListener(captionsDelegate)
        subscriber.setVideoListener(videoDelegate) // listen video enabled/disabled
        subscriber.setAudioLevelListener(audioLevelDelegate)

        participants[stream.streamId] = ParticipantState(subscriber = subscriber)

//        coroutineScope.launch {
//            subscriber.observeAudioLevel().collect { audioLevel ->
////                Log.d("audioLevelDelegate", "${subscriber.name()} -> $audioLevel [ ${Thread.currentThread()} ]")
//                audioLevelProcessor.onAudioLevelUpdate(subscriber.stream.streamId, audioLevel.round2())
//            }
//        }

        /**
         * crash when:
         * enter a room with 12 participants
         * enable/disable audio publish in a subscriber many times -> audio level listener stop publishing
         */

        coroutineScope.launch {
            audioLevelProcessor.getAudioLevelFlow(subscriber.stream.streamId)
                .collect { value ->
                    participants[subscriber.stream.streamId]?.let { p ->
                        p._isSpeaking.value = value > 0.1
                        //updatedParticipant?.isSpeaking?.value = isTalking
                    }
                    //actualActiveSpeakerTracker.onSubscriberAudioLevelUpdated(subscriber.stream.streamId, value)
                }
        }

        _participantsStateFlow.update { participants.values.toImmutableList() }
        _participantsCount.update { participants.size }

        session.subscribe(subscriber)
    }

    private fun removeSubscriber(stream: Stream) {
        val subscriber = participants[stream.streamId] ?: return
        coroutineScope.launch {
            actualActiveSpeakerTracker.onSubscriberDestroyed(stream.streamId)
            audioLevelProcessor.removeSubscriber(stream.streamId)
            subscriber.clean(session)

            participants.remove(stream.streamId)
            _participantsCount.value = participants.size
        }
    }

    private val streamDelegate: SubscriberKit.StreamListener = object : SubscriberKit.StreamListener {
        override fun onReconnected(subscriber: SubscriberKit) {
            // not implemented yet
        }

        override fun onDisconnected(subscriber: SubscriberKit) {
            // not implemented yet
        }

        override fun onAudioDisabled(subscriber: SubscriberKit) {
            coroutineScope.launch(Dispatchers.Default) {
                Log.d(TAG, "Subscriber audio disabled - ${Thread.currentThread()}")
                participants[subscriber.id()]?.let { subscriber ->
                    Log.d(TAG, "Subscriber audio disabled - ${subscriber.id}")
                    subscriber._isMicEnabled.value = false
                }
            }
        }

        override fun onAudioEnabled(subscriber: SubscriberKit) {
            coroutineScope.launch(Dispatchers.Default) {
                Log.d(TAG, "Subscriber - audio enabled - ${Thread.currentThread()}")
                participants[subscriber.id()]?.let { subscriber ->
                    Log.d(TAG, "Subscriber audio enabled to ${subscriber.id}")
                    subscriber._isMicEnabled.value = true
                }
            }
        }
    }

    private val captionsDelegate: SubscriberKit.CaptionsListener =
        SubscriberKit.CaptionsListener { subscriber, text, isFinal ->
            _captionsStateFlow.update { _ -> "${subscriber.name()}: $text" }
            if (isFinal) {
                _captionsStateFlow.update { _ -> null }
            }
        }

    private val audioLevelDelegate: SubscriberKit.AudioLevelListener =
        SubscriberKit.AudioLevelListener { subscriber, audioLevel ->
//            coroutineScope.launch(Dispatchers.Default) {
            //Log.d("audioLevelDelegate", "${subscriber.name()} -> $audioLevel [ ${Thread.currentThread()} ]")
            //audioLevelProcessor.onAudioLevelUpdate(subscriber.stream.streamId, audioLevel.round2())
//            }
        }

    private val videoDelegate: SubscriberKit.VideoListener = object : SubscriberKit.VideoListener {
        override fun onVideoDataReceived(subscriber: SubscriberKit) {
            // not implemented yet
        }

        override fun onVideoDisabled(subscriber: SubscriberKit, reason: String) {
            coroutineScope.launch(Dispatchers.Default) {
                Log.d(TAG, "Subscriber video disabled - reason $reason - ${Thread.currentThread()}")
                participants[subscriber.id()]?.let { subscriber ->
                    Log.d(TAG, "Subscriber video disabled - ${subscriber.id}")
                    subscriber._isCameraEnabled.value = false
                }
            }
        }

        override fun onVideoEnabled(subscriber: SubscriberKit, reason: String) {
            coroutineScope.launch(Dispatchers.Default) {
                Log.d(TAG, "Subscriber video enabled - reason $reason - ${Thread.currentThread()}")
                participants[subscriber.id()]?.let { subscriber ->
                    Log.d(TAG, "Subscriber video enabled - ${subscriber.id}")
                    subscriber._isCameraEnabled.value = true
                }
            }
        }

        override fun onVideoDisableWarning(subscriber: SubscriberKit) {
            // not implemented yet
            Log.d(TAG, "Subscriber video disabled warning - ${Thread.currentThread()}")
        }

        override fun onVideoDisableWarningLifted(subscriber: SubscriberKit) {
            // not implemented yet
            Log.d(TAG, "Subscriber video disabled warning lifted - ${Thread.currentThread()}")
        }
    }

    companion object {
        const val TAG: String = "Call"
        const val PUBLISHER_ID: String = "publisher"
        const val PUBLISHER_SCREEN_ID: String = "publisher-screen"
        private const val PUBLISHER_AUDIO_LEVEL_DEBOUNCE = 60L
    }
}
