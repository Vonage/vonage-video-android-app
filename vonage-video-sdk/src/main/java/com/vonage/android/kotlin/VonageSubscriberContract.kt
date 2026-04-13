package com.vonage.android.kotlin

import android.view.View

/**
 * Abstraction over a Vonage Video subscriber (remote participant's stream).
 *
 * Encapsulates all subscriber operations and listener management without
 * exposing the underlying SDK types. Designed to be easily mockable for testing.
 */
interface VonageSubscriber {

    val stream: VonageStream
    val view: View

    var subscribeToVideo: Boolean
    var subscribeToCaptions: Boolean

    fun setStreamListener(listener: VonageSubscriberStreamListener?)
    fun setVideoListener(listener: VonageSubscriberVideoListener?)
    fun setAudioLevelListener(listener: VonageAudioLevelListener?)
    fun setVideoStatsListener(listener: VonageSubscriberVideoStatsListener?)
    fun setAudioStatsListener(listener: VonageSubscriberAudioStatsListener?)
    fun setCaptionsListener(listener: VonageCaptionsListener?)
}

// region Subscriber listener interfaces

interface VonageSubscriberStreamListener {
    fun onReconnected()
    fun onDisconnected()
}

interface VonageSubscriberVideoListener {
    fun onVideoEnabled(reason: String)
    fun onVideoDisabled(reason: String)
    fun onVideoDataReceived()
    fun onVideoDisableWarning()
    fun onVideoDisableWarningLifted()
}

fun interface VonageCaptionsListener {
    fun onCaption(name: String, streamId: String, text: String, isFinal: Boolean)
}

// endregion

// region Subscriber stats

fun interface VonageSubscriberVideoStatsListener {
    fun onVideoStats(stats: VonageSubscriberVideoStatsEntry)
}

data class VonageSubscriberVideoStatsEntry(
    val videoPacketsReceived: Int,
    val videoPacketsLost: Int,
    val videoBytesReceived: Int,
    val width: Int,
    val height: Int,
    val codec: String,
    val decodedFrameRate: Double,
    val bitrate: Long,
    val freezeCount: Long,
    val totalFreezesDuration: Long,
    val estimatedBandwidthInBps: Long?,
)

fun interface VonageSubscriberAudioStatsListener {
    fun onAudioStats(stats: VonageSubscriberAudioStatsEntry)
}

data class VonageSubscriberAudioStatsEntry(
    val audioPacketsReceived: Int,
    val audioPacketsLost: Int,
    val audioBytesReceived: Int,
    val estimatedBandwidthInBps: Long?,
)

// endregion
