package com.vonage.android.kotlin

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import com.opentok.android.OpentokError
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VeraSubscriber
import com.vonage.android.kotlin.model.toParticipant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

@Stable
class Call internal constructor(
    private val context: Context,
    private val token: String,
    private val session: Session,
    private val publisherHolder: VeraPublisherHolder,
) {

    private val _participantsStateFlow = MutableStateFlow<ImmutableList<Participant>>(persistentListOf())
    val participantsStateFlow: StateFlow<ImmutableList<Participant>> = _participantsStateFlow

    private val subscriberStreams = HashMap<String, Subscriber>()
    private val participantStreams = HashMap<String, Participant>()

    fun connect(): Flow<SessionEvent> = callbackFlow {
        val sessionListener = object : Session.SessionListener {
            override fun onConnected(session: Session) {
                publishToSession()
                trySend(SessionEvent.Connected)
            }

            override fun onDisconnected(session: Session) {
                trySend(SessionEvent.Disconnected)
            }

            override fun onStreamReceived(session: Session, stream: Stream) {
                trySend(SessionEvent.StreamReceived(stream.streamId))
                addSubscriber(stream)
            }

            override fun onStreamDropped(session: Session, stream: Stream) {
                trySend(SessionEvent.StreamDropped(stream.streamId))
                removeSubscriber(stream)
            }

            override fun onError(session: Session, error: OpentokError) {
                trySend(SessionEvent.Error(error))
            }
        }
        session.setSessionListener(sessionListener)
        session.connect(token)
        awaitClose { session.setSessionListener(null) }
    }

    private fun publishToSession() {
        publisherHolder.let { holder ->
            val id = holder.publisher.stream?.streamId ?: PUBLISHER_ID
            participantStreams[id] = holder.participant
            _participantsStateFlow.value = participantStreams.values.toImmutableList()
            session.publish(holder.publisher)
        }
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
        subscriber.setAudioLevelListener { subscriber, audioLevel ->
            Log.d("AudioLevelListener", "Subscriber audio level changed - audioLevel $audioLevel")
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
                Log.d(TAG, "Subscriber video disabled - reason $reason")
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
        subscriberStreams.remove(stream.streamId)

        participantStreams.remove(stream.streamId)
        _participantsStateFlow.value = participantStreams.values.toImmutableList()

        session.unsubscribe(subscriber)
    }

    fun end() {
        publisherHolder.publisher.session?.disconnect()
        session.unpublish(publisherHolder.publisher)
        session.disconnect()
    }

    fun togglePublisherVideo() {
        publisherHolder.publisher.publishVideo = !publisherHolder.publisher.publishVideo
        participantStreams[PUBLISHER_ID] = publisherHolder.publisher.toParticipant()
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
    }

    fun togglePublisherAudio() {
        publisherHolder.publisher.publishAudio = !publisherHolder.publisher.publishAudio
        participantStreams[PUBLISHER_ID] = publisherHolder.publisher.toParticipant()
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
    }

    fun pause() {
        Log.d(TAG, "Session paused")
        session.onPause()
    }

    fun resume() {
        Log.d(TAG, "Session resumed")
        session.onResume()
    }

    companion object {
        const val TAG: String = "Call"
        const val PUBLISHER_ID: String = "publisher"
    }
}
