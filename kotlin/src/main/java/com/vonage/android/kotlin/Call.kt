package com.vonage.android.kotlin

import android.content.Context
import android.util.Log
import com.opentok.android.OpentokError
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.internal.VeraPublisherHolder
import com.vonage.android.kotlin.internal.toParticipant
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VeraSubscriber
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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

@Suppress("TooManyFunctions")
@OptIn(FlowPreview::class)
class Call internal constructor(
    private val context: Context,
    private val token: String,
    private val session: Session,
    private val publisherHolder: VeraPublisherHolder,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : CallFacade {

    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    private val _participantsStateFlow = MutableStateFlow<ImmutableList<Participant>>(persistentListOf())
    override val participantsStateFlow: StateFlow<ImmutableList<Participant>> = _participantsStateFlow

    private val subscriberStreams = HashMap<String, Subscriber>()
    private val subscriberJobs = ConcurrentHashMap<String, Job>()
    private val participantStreams = HashMap<String, Participant>()

    override fun connect(): Flow<SessionEvent> = callbackFlow {
        val sessionListener = object : Session.SessionListener {
            override fun onConnected(session: Session) {
                publishToSession()
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
        session.connect(token)
        awaitClose { session.setSessionListener(null) }
    }

    override fun endSession() {
        // wait for PublisherListener#streamDestroyed before returning : VIDSOL-104
        session.unpublish(publisherHolder.publisher)
        session.disconnect()
    }

    override fun togglePublisherVideo() {
        publisherHolder.publisher.publishVideo = publisherHolder.publisher.publishVideo.toggle()
        participantStreams[PUBLISHER_ID] = publisherHolder.publisher.toParticipant()
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
    }

    override fun togglePublisherCamera() {
        publisherHolder.publisher.cycleCamera()
    }

    override fun togglePublisherAudio() {
        publisherHolder.publisher.publishAudio = publisherHolder.publisher.publishAudio.toggle()
        participantStreams[PUBLISHER_ID] = publisherHolder.publisher.toParticipant()
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
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
    }

    override fun observePublisherAudio(): Flow<Float> = publisherHolder.publisher.observeAudioLevel { audioLevel ->
        Log.d(TAG, "publisher audio level listener changed to $audioLevel")

        participantStreams[PUBLISHER_ID] = publisherHolder.publisher
            .toParticipant(isSpeaking = (audioLevel > SPEAKING_AUDIO_LEVEL_THRESHOLD))
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
    }

    private fun addSubscriber(stream: Stream) {
        val subscriber = Subscriber.Builder(context, stream).build()
        subscriber.setStreamListener(object : SubscriberKit.StreamListener {
            override fun onReconnected(p0: SubscriberKit?) {
                // not implemented yet
            }

            override fun onDisconnected(p0: SubscriberKit?) {
                // not implemented yet
            }

            override fun onAudioDisabled(subscriber: SubscriberKit) {
                Log.d(TAG, "Subscriber audio level changed - audio disabled")
                subscriberStreams[subscriber.stream.streamId]?.let { subscriber ->
                    val updatedParticipant = (participantStreams[subscriber.stream.streamId] as VeraSubscriber)
                        .copy(isMicEnabled = false)
                    participantStreams[subscriber.stream.streamId] = updatedParticipant
                    _participantsStateFlow.value = participantStreams.values.toImmutableList()
                }
            }

            override fun onAudioEnabled(subscriber: SubscriberKit) {
                Log.d(TAG, "Subscriber audio level changed - audio enabled")
                subscriberStreams[subscriber.stream.streamId]?.let { subs ->
                    val updatedParticipant = (participantStreams[subs.stream.streamId] as VeraSubscriber)
                        .copy(isMicEnabled = true)
                    participantStreams[subs.stream.streamId] = updatedParticipant
                    _participantsStateFlow.value = participantStreams.values.toImmutableList()
                }
            }
        })

        coroutineScope.launch {
            subscriber.observeAudioLevel()
                .distinctUntilChanged()
                .debounce(SUBSCRIBER_AUDIO_LEVEL_SAMPLING_MS)
                .onEach { audioLevel ->
                    subscriberStreams[subscriber.stream.streamId]?.let { subs ->
                        val updatedParticipant = (participantStreams[subs.stream.streamId] as VeraSubscriber)
                            .copy(isSpeaking = (audioLevel > SPEAKING_AUDIO_LEVEL_THRESHOLD))
                        participantStreams[subs.stream.streamId] = updatedParticipant
                        _participantsStateFlow.value = participantStreams.values.toImmutableList()
                    }
                }
                .collect()
        }.also {
            subscriberJobs[subscriber.stream.streamId] = it
        }

        subscriber.setVideoListener(object : SubscriberKit.VideoListener {
            override fun onVideoDataReceived(subscriber: SubscriberKit) {
                // not implemented yet
            }

            override fun onVideoDisabled(subscriber: SubscriberKit, reason: String) {
                Log.d(TAG, "Subscriber video disabled - reason $reason")
                subscriberStreams[subscriber.stream.streamId]?.let { subs ->
                    val updatedParticipant = (participantStreams[subs.stream.streamId] as VeraSubscriber)
                        .copy(isCameraEnabled = false)
                    participantStreams[subs.stream.streamId] = updatedParticipant
                    _participantsStateFlow.value = participantStreams.values.toImmutableList()
                }
            }

            override fun onVideoEnabled(subscriber: SubscriberKit, reason: String) {
                Log.d(TAG, "Subscriber video enabled - reason $reason")
                subscriberStreams[subscriber.stream.streamId]?.let { subs ->
                    val updatedParticipant = (participantStreams[subs.stream.streamId] as VeraSubscriber)
                        .copy(isCameraEnabled = true)
                    participantStreams[subs.stream.streamId] = updatedParticipant
                    _participantsStateFlow.value = participantStreams.values.toImmutableList()
                }
            }

            override fun onVideoDisableWarning(subscriber: SubscriberKit) {
                // not implemented yet
            }

            override fun onVideoDisableWarningLifted(subscriber: SubscriberKit) {
                // not implemented yet
            }
        })
        session.subscribe(subscriber)
        subscriberStreams[stream.streamId] = subscriber

        participantStreams[stream.streamId] = subscriber.toParticipant()
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
    }

    private fun removeSubscriber(stream: Stream) {
        val subscriber = subscriberStreams[stream.streamId] ?: return
        subscriber.setVideoListener(null)
        subscriber.setStreamListener(null)
        subscriber.setAudioLevelListener(null)
        subscriberJobs[stream.streamId]?.cancel()
        subscriberJobs.remove(stream.streamId)
        subscriberStreams.remove(stream.streamId)
        participantStreams.remove(stream.streamId)
        _participantsStateFlow.value = participantStreams.values.toImmutableList()

        session.unsubscribe(subscriber)
    }

    companion object {
        const val TAG: String = "Call"
        const val PUBLISHER_ID: String = "publisher"

        const val SUBSCRIBER_AUDIO_LEVEL_SAMPLING_MS = 50L
        const val SPEAKING_AUDIO_LEVEL_THRESHOLD = 0.2F // add as a configuration parameter
    }
}
