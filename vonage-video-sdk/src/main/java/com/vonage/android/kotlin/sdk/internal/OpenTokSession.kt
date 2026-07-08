package com.vonage.android.kotlin.sdk.internal

import android.content.Context
import com.opentok.android.OpentokError
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.vonage.android.kotlin.sdk.VonageArchiveListener
import com.vonage.android.kotlin.sdk.VonageConnection
import com.vonage.android.kotlin.sdk.VonageError
import com.vonage.android.kotlin.sdk.VonagePublisher
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.sdk.VonageSessionListener
import com.vonage.android.kotlin.sdk.VonageSignalListener
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import com.vonage.android.kotlin.sdk.VonageVideoType
import java.util.concurrent.ConcurrentHashMap

/**
 * OpenTok-backed implementation of [VonageSession].
 *
 * Wraps the real OpenTok [Session], translating all listener callbacks
 * into our SDK-agnostic types. Manages an internal mapping of stream IDs
 * to raw [Stream] objects so that [subscribe] and [forceMuteStream] can
 * work with [VonageStream] data classes.
 */
internal class OpenTokSession(
    private val session: Session,
) : VonageSession {

    /** Stream ID → raw OpenTok Stream, needed for subscribe / forceMute */
    private val streamMap = ConcurrentHashMap<String, Stream>()

    override val sessionId: String get() = session.sessionId
    override val connectionId: String? get() = session.connection?.connectionId
    override val connectionCreationTime: Long?
        get() = session.connection?.creationTime?.time
    override val capabilities: String? get() = session.capabilities?.toString()

    override fun connect(token: String) = session.connect(token)

    override fun disconnect() = session.disconnect()

    override fun pause() = session.onPause()

    override fun resume() = session.onResume()

    override fun publish(publisher: VonagePublisher) {
        val real = (publisher as OpenTokPublisher).raw
        session.publish(real)
    }

    override fun unpublish(publisher: VonagePublisher) {
        val real = (publisher as OpenTokPublisher).raw
        session.unpublish(real)
    }

    override fun subscribe(context: Context, stream: VonageStream): VonageSubscriber {
        val realStream = streamMap[stream.streamId]
            ?: error("Unknown stream ${stream.streamId}")
        val subscriber = Subscriber.Builder(context, realStream).build()
        session.subscribe(subscriber)
        return OpenTokSubscriber(subscriber)
    }

    override fun unsubscribe(subscriber: VonageSubscriber) {
        val real = (subscriber as OpenTokSubscriber).raw
        session.unsubscribe(real)
    }

    override fun sendSignal(type: String, data: String) {
        session.sendSignal(type, data)
    }

    override fun forceMuteStream(stream: VonageStream) {
        val realStream = streamMap[stream.streamId]
            ?: error("Unknown stream ${stream.streamId}")
        session.forceMuteStream(realStream)
    }

    override fun setSessionListener(listener: VonageSessionListener?) {
        if (listener == null) {
            session.setSessionListener(null)
            session.setStreamPropertiesListener(null)
            return
        }
        session.setSessionListener(object : Session.SessionListener {
            override fun onConnected(session: Session) {
                listener.onConnected()
            }

            override fun onDisconnected(session: Session) {
                listener.onDisconnected()
            }

            override fun onStreamReceived(session: Session, stream: Stream) {
                streamMap[stream.streamId] = stream
                listener.onStreamReceived(stream.toVonage())
            }

            override fun onStreamDropped(session: Session, stream: Stream) {
                streamMap.remove(stream.streamId)
                listener.onStreamDropped(stream.toVonage())
            }

            override fun onError(session: Session, error: OpentokError) {
                listener.onError(error.toVonage())
            }
        })
        session.setStreamPropertiesListener(object : Session.StreamPropertiesListener {
            override fun onStreamHasAudioChanged(session: Session, stream: Stream, hasAudio: Boolean) {
                listener.onStreamPropertyChanged(
                    streamId = stream.streamId,
                    hasVideo = stream.hasVideo(),
                    hasAudio = hasAudio,
                )
            }

            override fun onStreamHasVideoChanged(session: Session, stream: Stream, hasVideo: Boolean) {
                listener.onStreamPropertyChanged(
                    streamId = stream.streamId,
                    hasVideo = hasVideo,
                    hasAudio = stream.hasAudio(),
                )
            }

            override fun onStreamVideoDimensionsChanged(session: Session, stream: Stream, width: Int, height: Int) {
                // Intentionally ignored — dimension changes do not affect audio/video state.
            }

            override fun onStreamVideoTypeChanged(session: Session, stream: Stream, videoType: Stream.StreamVideoType) {
                // Intentionally ignored — video type is fixed at subscribe time.
            }
        })
    }

    override fun setSignalListener(listener: VonageSignalListener?) {
        if (listener == null) {
            session.setSignalListener(null)
            return
        }
        session.setSignalListener { _, type, data, connection ->
            val conn = connection?.let {
                VonageConnection(
                    connectionId = it.connectionId,
                    creationTime = it.creationTime?.time ?: 0,
                )
            }
            listener.onSignalReceived(type, data, conn)
        }
    }

    override fun setArchiveListener(listener: VonageArchiveListener?) {
        if (listener == null) {
            session.setArchiveListener(null)
            return
        }
        session.setArchiveListener(object : Session.ArchiveListener {
            override fun onArchiveStarted(session: Session, id: String, name: String?) {
                listener.onArchiveStarted(id, name)
            }

            override fun onArchiveStopped(session: Session, id: String) {
                listener.onArchiveStopped(id)
            }
        })
    }

    /**
     * Registers a publisher's stream in the internal stream map.
     * This is needed to allow subscribing to the publisher's own stream for self-captions.
     */
    override fun registerPublisherStream(publisher: VonagePublisher) {
        val real = (publisher as OpenTokPublisher).raw
        real.stream?.let { stream ->
            streamMap[stream.streamId] = stream
        }
    }

    /**
     * Unregisters a publisher's stream from the internal stream map.
     */
    override fun unregisterPublisherStream(publisher: VonagePublisher) {
        val real = (publisher as OpenTokPublisher).raw
        real.stream?.let { stream ->
            streamMap.remove(stream.streamId)
        }
    }
}

// region Mapping helpers

internal fun Stream.toVonage(): VonageStream = VonageStream(
    streamId = streamId,
    name = name,
    connection = VonageConnection(
        connectionId = connection.connectionId,
        creationTime = connection.creationTime?.time ?: 0,
    ),
    creationTime = creationTime?.time ?: 0,
    videoType = when (streamVideoType) {
        Stream.StreamVideoType.StreamVideoTypeCamera -> VonageVideoType.CAMERA
        Stream.StreamVideoType.StreamVideoTypeScreen -> VonageVideoType.SCREEN
        Stream.StreamVideoType.StreamVideoTypeCustom -> VonageVideoType.CUSTOM
    },
    hasVideo = hasVideo(),
    hasAudio = hasAudio(),
)

internal fun OpentokError.toVonage(): VonageError = VonageError(
    code = errorCode.ordinal,
    message = message,
    domain = errorDomain?.name.orEmpty(),
)

// endregion
