package com.vonage.android.kotlin

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.PublisherKit.PublisherListener
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Subscriber
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
) {

    private val _participantsStateFlow = MutableStateFlow<ImmutableList<Participant>>(persistentListOf())
    val participantsStateFlow: StateFlow<ImmutableList<Participant>> = _participantsStateFlow

    private var publisher: Publisher? = null

    private val subscribers = ArrayList<Subscriber>()
    private val subscriberStreams = HashMap<String, Subscriber>()

    private val participants = ArrayList<Participant>()
    private val participantStreams = HashMap<String, Participant>()

    private val sessionListener: Session.SessionListener = object : Session.SessionListener {
        override fun onConnected(session: Session) {
            // use publisher factory instead with publisher preconfigured in waiting room
            Log.d(TAG, "onConnected: Connected to session: " + session.sessionId)

            publisher = Publisher.Builder(context)
                .name("Vera Native")
                .videoTrack(true)
                .build().apply {
                    setPublisherListener(publisherListener)
                    renderer?.setStyle(
                        BaseVideoRenderer.STYLE_VIDEO_SCALE,
                        BaseVideoRenderer.STYLE_VIDEO_FIT
                    )
                }
                .also {
                    val pa = VeraPublisher(it)
                    val id = it?.stream?.streamId ?: "publisher"
                    participantStreams[id] = pa
                    participants.add(pa)
                    _participantsStateFlow.value = participants.toImmutableList()
                    session.publish(it)
                }
        }

        override fun onDisconnected(session: Session) {
            Log.d(TAG, "onDisconnected: Disconnected from session: " + session.sessionId)
        }

        override fun onStreamReceived(session: Session, stream: Stream) {
            Log.d(TAG, "onStreamReceived: New Stream Received " + stream.streamId + " in session: " + session.sessionId)
            val subscriber = Subscriber.Builder(context, stream).build()
            session.subscribe(subscriber)
            subscribers.add(subscriber)
            subscriberStreams[stream.streamId] = subscriber

            participants.add(subscriber.toParticipant())
            participantStreams[stream.streamId] = subscriber.toParticipant()
            _participantsStateFlow.value = participantStreams.values.toImmutableList()
        }

        override fun onStreamDropped(session: Session, stream: Stream) {
            Log.d(TAG, "onStreamDropped: Stream Dropped: " + stream.streamId + " in session: " + session.sessionId)
            Log.d("XXX", "DROP - " + participants.joinToString { it.id })

            val subscriber = subscriberStreams[stream.streamId] ?: return
            subscribers.remove(subscriber)
            subscriberStreams.remove(stream.streamId)

            val p = participantStreams[stream.streamId] ?: return
            participants.remove(p)
            participantStreams.remove(stream.streamId)
            _participantsStateFlow.value = participantStreams.values.toImmutableList()

            Log.d("XXX", "CALL - " + participantStreams.values.joinToString { it.id })
            session.unsubscribe(subscriber)
        }

        override fun onError(session: Session, opentokError: OpentokError) {
            Log.e(TAG, "Session error: " + opentokError.message)
        }
    }

    private val publisherListener: PublisherListener = object : PublisherListener {
        override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {
            Log.d(TAG, "onStreamCreated: Publisher Stream Created. Own stream " + stream.streamId)
        }

        override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {
            Log.d(TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream " + stream.streamId)
        }

        override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {
            Log.e(TAG, "PublisherKit onError: " + opentokError.message)
        }
    }

    fun connect() {
        session.setSessionListener(sessionListener)
        session.connect(token)
    }

    fun observeConnect(): Flow<String> = callbackFlow {
        val sessionListener = object : Session.SessionListener {
            override fun onConnected(p0: Session?) {
                trySend("Connected")
            }

            override fun onDisconnected(p0: Session?) {
                trySend("Disconnected")
            }

            override fun onStreamReceived(p0: Session?, p1: Stream?) {
                trySend("Stream received")
            }

            override fun onStreamDropped(p0: Session?, p1: Stream?) {
                trySend("Stream dropped")
            }

            override fun onError(p0: Session?, p1: OpentokError?) {
                trySend("Error $p1")
            }
        }
        session.setSessionListener(sessionListener)
        session.connect(token)
        awaitClose { session.setSessionListener(null) }
    }

    fun end() {
        publisher?.session?.disconnect()
        session.disconnect()
    }

    private companion object {
        const val TAG: String = "Call"
    }
}
