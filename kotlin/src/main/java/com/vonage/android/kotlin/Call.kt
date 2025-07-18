package com.vonage.android.kotlin

import android.content.Context
import androidx.compose.runtime.Stable
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VeraPublisher
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
class Call(
    private val context: Context,
    private val token: String,
    private val session: Session,
    private val publisher: Publisher,
) {

    private val _participantsStateFlow = MutableStateFlow<ImmutableList<Participant>>(persistentListOf())
    val participantsStateFlow: StateFlow<ImmutableList<Participant>> = _participantsStateFlow

    private val subscribers = ArrayList<Subscriber>()
    private val subscriberStreams = HashMap<String, Subscriber>()

    private val participants = ArrayList<Participant>()
    private val participantStreams = HashMap<String, Participant>()

    fun connect(): Flow<SessionEvent> = callbackFlow {
        val sessionListener = object : Session.SessionListener {
            override fun onConnected(session: Session) {
                val publisher = defaultPublisher()
                session.publish(publisher)
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

    private fun defaultPublisher(): Publisher {
        val participant = VeraPublisher(publisher)
        val id = publisher.stream?.streamId ?: "publisher-generated-uuid"
        participantStreams[id] = participant
        participants.add(participant)

        _participantsStateFlow.value = participants.toImmutableList()
        return publisher
    }

    private fun addSubscriber(stream: Stream) {
        val subscriber = Subscriber.Builder(context, stream).build()
        session.subscribe(subscriber)
        subscribers.add(subscriber)
        subscriberStreams[stream.streamId] = subscriber

        participants.add(subscriber.toParticipant())
        participantStreams[stream.streamId] = subscriber.toParticipant()
        _participantsStateFlow.value = participantStreams.values.toImmutableList()
    }

    private fun removeSubscriber(stream: Stream) {
        val subscriber = subscriberStreams[stream.streamId] ?: return
        subscribers.remove(subscriber)
        subscriberStreams.remove(stream.streamId)

        val p = participantStreams[stream.streamId] ?: return
        participants.remove(p)
        participantStreams.remove(stream.streamId)
        _participantsStateFlow.value = participantStreams.values.toImmutableList()

        session.unsubscribe(subscriber)
    }

    fun end() {
        publisher.session?.disconnect()
        session.disconnect()
    }

    fun togglePublisherVideo() {
//        publisher.publishVideo = !publisher.publishVideo
//        val p = participantStreams["publisher-generated-uuid"]
//        p?.toggleVideo()
//        _participantsStateFlow.value = participantStreams.values.toImmutableList()
    }

    fun togglePublisherAudio() {
//        publisher.publishAudio = !publisher.publishAudio
    }

    private companion object {
        const val TAG: String = "Call"
    }
}
