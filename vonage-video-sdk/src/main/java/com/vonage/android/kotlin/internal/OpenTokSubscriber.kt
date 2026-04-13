package com.vonage.android.kotlin.internal

import android.view.View
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import com.vonage.android.kotlin.VonageAudioLevelListener
import com.vonage.android.kotlin.VonageCaptionsListener
import com.vonage.android.kotlin.VonageStream
import com.vonage.android.kotlin.VonageSubscriber
import com.vonage.android.kotlin.VonageSubscriberAudioStatsEntry
import com.vonage.android.kotlin.VonageSubscriberAudioStatsListener
import com.vonage.android.kotlin.VonageSubscriberStreamListener
import com.vonage.android.kotlin.VonageSubscriberVideoListener
import com.vonage.android.kotlin.VonageSubscriberVideoStatsEntry
import com.vonage.android.kotlin.VonageSubscriberVideoStatsListener

/**
 * OpenTok-backed implementation of [VonageSubscriber].
 *
 * Wraps a real [Subscriber] and translates all listener callbacks
 * into SDK-agnostic types.
 */
internal class OpenTokSubscriber(
    internal val raw: Subscriber,
) : VonageSubscriber {

    override val stream: VonageStream = raw.stream.toVonage()
    override val view: View get() = raw.view

    override var subscribeToVideo: Boolean
        get() = raw.subscribeToVideo
        set(value) { raw.subscribeToVideo = value }

    override var subscribeToCaptions: Boolean
        get() = raw.subscribeToCaptions
        set(value) { raw.subscribeToCaptions = value }

    override fun setStreamListener(listener: VonageSubscriberStreamListener?) {
        if (listener == null) { raw.setStreamListener(null); return }
        raw.setStreamListener(object : SubscriberKit.StreamListener {
            override fun onReconnected(s: SubscriberKit) = listener.onReconnected()
            override fun onDisconnected(s: SubscriberKit) = listener.onDisconnected()
        })
    }

    override fun setVideoListener(listener: VonageSubscriberVideoListener?) {
        if (listener == null) { raw.setVideoListener(null); return }
        raw.setVideoListener(object : SubscriberKit.VideoListener {
            override fun onVideoEnabled(s: SubscriberKit, reason: String) =
                listener.onVideoEnabled(reason)

            override fun onVideoDisabled(s: SubscriberKit, reason: String) =
                listener.onVideoDisabled(reason)

            override fun onVideoDataReceived(s: SubscriberKit) =
                listener.onVideoDataReceived()

            override fun onVideoDisableWarning(s: SubscriberKit) =
                listener.onVideoDisableWarning()

            override fun onVideoDisableWarningLifted(s: SubscriberKit) =
                listener.onVideoDisableWarningLifted()
        })
    }

    override fun setAudioLevelListener(listener: VonageAudioLevelListener?) {
        if (listener == null) { raw.setAudioLevelListener(null); return }
        raw.setAudioLevelListener { _, level -> listener.onAudioLevelUpdated(level) }
    }

    override fun setVideoStatsListener(listener: VonageSubscriberVideoStatsListener?) {
        if (listener == null) { raw.setVideoStatsListener(null); return }
        raw.setVideoStatsListener { _, stats ->
            listener.onVideoStats(
                VonageSubscriberVideoStatsEntry(
                    videoPacketsReceived = stats.videoPacketsReceived,
                    videoPacketsLost = stats.videoPacketsLost,
                    videoBytesReceived = stats.videoBytesReceived,
                    width = stats.width,
                    height = stats.height,
                    codec = stats.codec.orEmpty(),
                    decodedFrameRate = stats.decodedFrameRate,
                    bitrate = stats.bitrate,
                    freezeCount = stats.freezeCount,
                    totalFreezesDuration = stats.totalFreezesDuration,
                    estimatedBandwidthInBps = stats.senderStats?.connectionEstimatedBandwidth,
                ),
            )
        }
    }

    override fun setAudioStatsListener(listener: VonageSubscriberAudioStatsListener?) {
        if (listener == null) { raw.setAudioStatsListener(null); return }
        raw.setAudioStatsListener { _, stats ->
            listener.onAudioStats(
                VonageSubscriberAudioStatsEntry(
                    audioPacketsReceived = stats.audioPacketsReceived,
                    audioPacketsLost = stats.audioPacketsLost,
                    audioBytesReceived = stats.audioBytesReceived,
                    estimatedBandwidthInBps = stats.senderStats?.connectionEstimatedBandwidth,
                ),
            )
        }
    }

    override fun setCaptionsListener(listener: VonageCaptionsListener?) {
        if (listener == null) { raw.setCaptionsListener(null); return }
        raw.setCaptionsListener { subscriber, text, isFinal ->
            listener.onCaption(
                name = subscriber.stream.name,
                streamId = subscriber.stream.streamId,
                text = text,
                isFinal = isFinal,
            )
        }
    }
}
