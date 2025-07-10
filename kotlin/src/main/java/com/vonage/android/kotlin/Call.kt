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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
class Call(
    private val context: Context,
    private val token: String,
    private val session: Session,
) {

    private val _publisherStateFlow = MutableStateFlow<PublisherState>(PublisherState.Offline)
    val publisherStateFlow: StateFlow<PublisherState> = _publisherStateFlow

    private var publisher: Publisher? = null

    private val sessionListener: Session.SessionListener = object : Session.SessionListener {
        override fun onConnected(session: Session) {
            Log.d(TAG, "onConnected: Connected to session: " + session.sessionId)

            publisher = Publisher.Builder(context)
                .name("Testing")
                .videoTrack(true)
                .build().apply {
                    setPublisherListener(publisherListener)
                    renderer?.setStyle(
                        BaseVideoRenderer.STYLE_VIDEO_SCALE,
                        BaseVideoRenderer.STYLE_VIDEO_FILL
                    )
                }
                .also {
                    _publisherStateFlow.value = PublisherState.Online(it)
                    session.publish(it)
                }
        }

        override fun onDisconnected(session: Session) {
            Log.d(TAG, "onDisconnected: Disconnected from session: " + session.sessionId)
        }

        override fun onStreamReceived(session: Session, stream: Stream) {
            Log.d(TAG, "onStreamReceived: New Stream Received " + stream.streamId + " in session: " + session.sessionId)
        }

        override fun onStreamDropped(session: Session, stream: Stream) {
            Log.d(TAG, "onStreamDropped: Stream Dropped: " + stream.streamId + " in session: " + session.sessionId)
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

    fun setName(publisherName: String) {

    }

    fun togglePublishVideo() {
        publisher?.let {
            val enableVideo = !it.publishVideo
            it.publishVideo = enableVideo
            _publisherStateFlow.value = PublisherState.Online(it, enableVideo, it.publishAudio)
        }
    }

    fun togglePublishAudio() {
        publisher?.let {
            val enableAudio = !it.publishAudio
            it.publishAudio = enableAudio
            _publisherStateFlow.value = PublisherState.Online(it, it.publishVideo, enableAudio)
        }
    }

    fun end() {
        session.disconnect()
    }

    private companion object {
        const val TAG: String = "Call"
    }
}
