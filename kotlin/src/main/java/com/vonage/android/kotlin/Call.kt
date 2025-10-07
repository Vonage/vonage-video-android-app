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
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.ext.throttleFirst
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.internal.ActiveSpeakerChangedPayload
import com.vonage.android.kotlin.internal.ActiveSpeakerListener
import com.vonage.android.kotlin.internal.ActiveSpeakerTracker
import com.vonage.android.kotlin.internal.ScreenSharingCapturer
import com.vonage.android.kotlin.internal.SubscriberTalkingTracker
import com.vonage.android.kotlin.internal.TalkingStateListener
import com.vonage.android.kotlin.internal.VeraPublisherHolder
import com.vonage.android.kotlin.internal.toParticipant
import com.vonage.android.kotlin.internal.toScreenParticipant
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.model.VeraSubscriber
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.kotlin.signal.ChatSignalPlugin.Companion.PAYLOAD_PARTICIPANT_NAME_KEY
import com.vonage.android.kotlin.signal.SignalPlugin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.SortedMap
import java.util.concurrent.ConcurrentHashMap

@Suppress("TooManyFunctions")
@OptIn(FlowPreview::class)
@Stable
class Call internal constructor(
    private val token: String,
    private val session: Session,
    private val publisherHolder: VeraPublisherHolder,
    private val signalPlugins: ImmutableList<SignalPlugin>,
    private val activeSpeakerTracker: ActiveSpeakerTracker = ActiveSpeakerTracker(),
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : CallFacade {

    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    private val _participantsStateFlow = MutableStateFlow<ImmutableList<Participant>>(persistentListOf())
    override val participantsStateFlow: StateFlow<ImmutableList<Participant>> = _participantsStateFlow

    private val _participantsCount = MutableStateFlow(_participantsStateFlow.value.size)
    override val participantsCount: StateFlow<Int> = _participantsCount

    private val _signalStateFlow = MutableStateFlow<SignalState?>(null)
    override val signalStateFlow: StateFlow<SignalState?> = _signalStateFlow

    private val _captionsStateFlow = MutableStateFlow<String?>(null)
    override val captionsStateFlow: StateFlow<String?> = _captionsStateFlow

    private val _mainSpeaker = MutableStateFlow<Participant?>(null)
    override val mainSpeaker: StateFlow<Participant?> = _mainSpeaker

    private val _localAudioStateFlow = MutableStateFlow(0F)
    override val localAudioLevel: StateFlow<Float> = _localAudioStateFlow

    private val signals = ConcurrentHashMap<String, SignalStateContent>()
    private val subscriberStreams = ConcurrentHashMap<String, Subscriber>()
    private val subscriberJobs = ConcurrentHashMap<String, Job>()
    private var participantStreams: SortedMap<String, Participant> = sortedMapOf()

    private lateinit var context: Context

    init {
        activeSpeakerTracker.setActiveSpeakerListener(object : ActiveSpeakerListener {
            override fun onActiveSpeakerChanged(payload: ActiveSpeakerChangedPayload) {
                coroutineScope.launch {
                    Log.d(
                        "activeSpeakerTracker",
                        "active speaker = ${payload.newActiveSpeaker.streamId} (${Thread.currentThread()})"
                    )
                    subscriberStreams[payload.newActiveSpeaker.streamId]?.subscribeToVideo = true
                    _mainSpeaker.value = participantStreams[payload.newActiveSpeaker.streamId]
                }
            }

            override fun onResetActiveSpeaker() {
                _mainSpeaker.value = participantStreams[publisherHolder.publisher.stream.streamId]
            }
        })
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
                Log.d("subscriber.onError", "error = $error")
                trySend(SessionEvent.Error(error))
            }
        }
        session.setSessionListener(sessionListener)
        session.setSignalListener { _, type, data, conn ->
            signalPlugins.forEach { plugin ->
                val isYou = publisherHolder.publisher.stream.connection == conn
                val senderName = if (!isYou) {
                    conn.extractSenderName(subscriberStreams.values)
                } else {
                    ""
                }

                plugin.handleSignal(type, data, senderName, isYou) { state ->
                    updateSignals(type, state)
                }?.let { state ->
                    updateSignals(type, state)
                }
            }
        }
        session.connect(token)
        awaitClose { session.setSessionListener(null) }
    }

    private fun updateSignals(type: String, state: SignalStateContent) {
        signals[type] = state
        _signalStateFlow.value = SignalState(signals = signals)
    }

    override fun sendEmoji(emoji: String) {
        signalPlugins
            .filter { it.canHandle(SignalType.REACTION.signal) }
            .forEach { plugin ->
                plugin.sendSignal(session, emoji)
            }
    }

    override fun sendChatMessage(message: String) {
        signalPlugins
            .filter { it.canHandle(SignalType.CHAT.signal) }
            .forEach { plugin ->
                plugin.sendSignal(
                    session, message, mapOf(
                        PAYLOAD_PARTICIPANT_NAME_KEY to publisherHolder.publisher.name.orEmpty(),
                    )
                )
            }
    }

    override fun listenUnreadChatMessages(enable: Boolean) {
        signalPlugins
            .filterIsInstance<ChatSignalPlugin>()
            .mapNotNull { it.listenUnread(enable) }
            .forEach { state ->
                signals[SignalType.CHAT.signal] = state
                _signalStateFlow.value = SignalState(signals = signals)
            }
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
            participantStreams[PUBLISHER_ID] = publisher.toParticipant()
            _participantsStateFlow.value = participantStreams.values.toImmutableList()
        }
    }

    override fun toggleLocalCamera() {
        publisherHolder.publisher.cycleCamera()
    }

    override fun toggleLocalAudio() {
        publisherHolder.publisher.let { publisher ->
            publisher.publishAudio = publisher.publishAudio.toggle()
            participantStreams[PUBLISHER_ID] = publisher.toParticipant()
            _participantsStateFlow.value = participantStreams.values.toImmutableList()
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
            participantStreams[id] = holder.participant
            _participantsStateFlow.value = participantStreams.values.toImmutableList()
            session.publish(holder.publisher)
        }
        publisherAudioLevel()
    }

    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {
        coroutineScope.launch {
            snapshotFlow.collectLatest { visibleParticipants ->
                if (visibleParticipants.isEmpty()) return@collectLatest
                subscriberStreams.forEach { (key, value) ->
                    value.subscribeToVideo = visibleParticipants.contains(key) || (key == mainSpeaker.value?.id)
                }
            }
        }
    }

    private fun publisherAudioLevel() {
        coroutineScope.launch {
            publisherHolder.publisher.observeAudioLevel()
                .filter { publisherHolder.participant.isMicEnabled.value }
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
        participantStreams[PUBLISHER_SCREEN_ID] = screenPublisher.toScreenParticipant()
        _participantsStateFlow.value = participantStreams.values.toImmutableList()

        session.publish(screenPublisher)
    }

    override fun stopCapturingScreen() {
        publisherHolder.screenPublisher?.let {
            session.unpublish(it)
        }
        participantStreams.remove(PUBLISHER_SCREEN_ID)
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
    }

    override fun enableCaptions(enable: Boolean) {
        publisherHolder.publisher.publishCaptions = enable
        subscriberStreams.values.forEach { it.subscribeToCaptions = enable }
    }

    private fun addSubscriber(stream: Stream) {
        val subscriber = Subscriber.Builder(context, stream).build()
        session.subscribe(subscriber)

        val talkingTracker = SubscriberTalkingTracker()
        talkingTracker.setTalkingStateListener(object : TalkingStateListener {
            override fun onTalkingStateChanged(isTalking: Boolean) {
                coroutineScope.launch {
                    subscriberStreams[subscriber.stream.streamId]?.let { subs ->
                        participantStreams[subs.stream.streamId]?.let { p ->
                            val updatedParticipant = (p as VeraSubscriber)
                            updatedParticipant.isSpeaking.value = isTalking
                            participantStreams[subs.stream.streamId] = updatedParticipant
                        }
                    }
                }
            }
        })

        subscriber.setStreamListener(object : SubscriberKit.StreamListener {
            override fun onReconnected(p0: SubscriberKit?) {
                // not implemented yet
            }

            override fun onDisconnected(p0: SubscriberKit?) {
                // not implemented yet
            }

            override fun onAudioDisabled(subscriber: SubscriberKit) {
                coroutineScope.launch {
                    Log.d(TAG, "Subscriber - audio disabled - ${Thread.currentThread()}")
                    subscriberStreams[subscriber.stream.streamId]?.let { subscriber ->
                        val updatedParticipant = (participantStreams[subscriber.stream.streamId] as VeraSubscriber)
                        updatedParticipant.isMicEnabled.value = false
                        participantStreams[subscriber.stream.streamId] = updatedParticipant
                    }
                }
            }

            override fun onAudioEnabled(subscriber: SubscriberKit) {
                coroutineScope.launch {
                    Log.d(TAG, "Subscriber - audio enabled - ${Thread.currentThread()}")
                    subscriberStreams[subscriber.stream.streamId]?.let { subs ->
                        val updatedParticipant = (participantStreams[subs.stream.streamId] as VeraSubscriber)
                        updatedParticipant.isMicEnabled.value = true
                        participantStreams[subs.stream.streamId] = updatedParticipant
                    }
                }
            }
        })

        subscriber.setCaptionsListener { subscriber, text, isFinal ->
            _captionsStateFlow.update { _ -> "${subscriber.name()}: $text" }
            if (isFinal) {
                _captionsStateFlow.update { _ -> null }
            }
        }

        subscriber.setVideoListener(object : SubscriberKit.VideoListener {
            override fun onVideoDataReceived(subscriber: SubscriberKit) {
                // not implemented yet
            }

            override fun onVideoDisabled(subscriber: SubscriberKit, reason: String) {
                coroutineScope.launch {
                    Log.d(TAG, "Subscriber video disabled - reason $reason - ${Thread.currentThread()}")
                    subscriberStreams[subscriber.stream.streamId]?.let { subs ->
                        val updatedParticipant = (participantStreams[subs.stream.streamId] as VeraSubscriber)
                        updatedParticipant.isCameraEnabled.value = false
                    }
                }
            }

            override fun onVideoEnabled(subscriber: SubscriberKit, reason: String) {
                coroutineScope.launch {
                    Log.d(TAG, "Subscriber video enabled - reason $reason - ${Thread.currentThread()}")
                    subscriberStreams[subscriber.stream.streamId]?.let { subs ->
                        val updatedParticipant = (participantStreams[subs.stream.streamId] as VeraSubscriber)
                        updatedParticipant.isCameraEnabled.value = true
                    }
                }
            }

            override fun onVideoDisableWarning(subscriber: SubscriberKit) {
                // not implemented yet
            }

            override fun onVideoDisableWarningLifted(subscriber: SubscriberKit) {
                // not implemented yet
            }
        })

        coroutineScope.launch {
            subscriber.observeAudioLevel()
                .distinctUntilChanged()
                .throttleFirst(SUBSCRIBER_AUDIO_LEVEL_DEBOUNCE)
                .onEach { audioLevel ->
                    activeSpeakerTracker.onSubscriberAudioLevelUpdated(subscriber.stream.streamId, audioLevel)
                    talkingTracker.onAudioLevelUpdated(audioLevel)
                }
                .collect()
        }.also {
            subscriberJobs[subscriber.stream.streamId] = it
        }

        subscriberStreams[stream.streamId] = subscriber
        participantStreams[stream.streamId] = subscriber.toParticipant()
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
        if (_participantsCount.value == 2) {
            _mainSpeaker.value = subscriber.toParticipant()
        }
        _participantsCount.value = participantStreams.size
    }

    private fun removeSubscriber(stream: Stream) {
        coroutineScope.launch {
            val subscriber = subscriberStreams[stream.streamId] ?: return@launch
            activeSpeakerTracker.onSubscriberDestroyed(stream.streamId)
            subscriber.setVideoListener(null)
            subscriber.setStreamListener(null)
            subscriber.setAudioLevelListener(null)
            subscriberJobs[stream.streamId]?.cancel()
            subscriberJobs.remove(stream.streamId)
            subscriberStreams.remove(stream.streamId)
            participantStreams.remove(stream.streamId)
            _participantsStateFlow.value = participantStreams.values.toImmutableList()
            _participantsCount.value = participantStreams.size
            session.unsubscribe(subscriber)
        }
    }

    companion object {
        const val TAG: String = "Call"
        const val PUBLISHER_ID: String = "publisher"
        const val PUBLISHER_SCREEN_ID: String = "publisher-screen"
        private const val PUBLISHER_AUDIO_LEVEL_DEBOUNCE = 60L
        private const val SUBSCRIBER_AUDIO_LEVEL_DEBOUNCE = 500L
    }
}
