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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
class Call(
    private val context: Context,
    private val token: String,
    private val session: Session,
) {

    private val _participantsStateFlow = MutableStateFlow<List<Participant>>(emptyList())
    val participantsStateFlow: StateFlow<List<Participant>> = _participantsStateFlow

    private val _publisherStateFlow = MutableStateFlow<PublisherState>(PublisherState.Offline)
    val publisherStateFlow: StateFlow<PublisherState> = _publisherStateFlow

    private val _subscriberStateFlow = MutableStateFlow<SubscriberState>(SubscriberState.Offline)
    val subscriberStateFlow: StateFlow<SubscriberState> = _subscriberStateFlow

    private var publisher: Publisher? = null

    private val subscribers = ArrayList<Subscriber>()
    private val subscriberStreams = HashMap<Stream, Subscriber>()

    private val sessionListener: Session.SessionListener = object : Session.SessionListener {
        override fun onConnected(session: Session) {
            // use publisher factory instead with publisher preconfigured in waiting room
            Log.d(TAG, "onConnected: Connected to session: " + session.sessionId)

            publisher = Publisher.Builder(context)
                .name("Testing")
                .videoTrack(true)
                .build().apply {
                    setPublisherListener(publisherListener)
                    renderer?.setStyle(
                        BaseVideoRenderer.STYLE_VIDEO_SCALE,
                        BaseVideoRenderer.STYLE_VIDEO_FIT
                    )
                }
                .also {
                    _publisherStateFlow.value = PublisherState.Online(it)
                    _participantsStateFlow.value += VeraPublisher(it)
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
            subscriberStreams[stream] = subscriber
//            _subscriberStateFlow.value = SubscriberState.Online(subscribers.toImmutableList())
            _subscriberStateFlow.value = SubscriberState.Online(subscribers.toParticipants())
            _participantsStateFlow.value += VeraSubscriber(subscriber)
        }

        override fun onStreamDropped(session: Session, stream: Stream) {
            Log.d(TAG, "onStreamDropped: Stream Dropped: " + stream.streamId + " in session: " + session.sessionId)
            val subscriber = subscriberStreams[stream] ?: return
            subscribers.remove(subscriber)
            subscriberStreams.remove(stream)

            subscriber.subscribeToAudio = true

            session.unsubscribe(subscriber)
//            _subscriberStateFlow.value = SubscriberState.Online(subscribers.toImmutableList())
            _subscriberStateFlow.value = SubscriberState.Online(subscribers.toParticipants())
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

    fun end() {
        publisher?.session?.disconnect()
        session.disconnect()
    }

    private companion object {
        const val TAG: String = "Call"
    }
}
