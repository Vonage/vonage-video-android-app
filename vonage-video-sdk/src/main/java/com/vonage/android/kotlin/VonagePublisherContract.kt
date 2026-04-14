package com.vonage.android.kotlin

import android.view.View

/**
 * Abstraction over a Vonage Video publisher (local camera/screen stream).
 *
 * Encapsulates all publisher operations and listener management without
 * exposing the underlying SDK types. Designed to be easily mockable for testing.
 */
interface VonagePublisher {

    val name: String
    val view: View
    val stream: VonageStream?

    var publishVideo: Boolean
    var publishAudio: Boolean
    var publishCaptions: Boolean

    fun cycleCamera()
    fun destroy()
    fun stop()

    fun applyBlur(level: VonageBlurLevel)
    fun applyVideoBitrate(preset: VonageBitratePreset, maxBitrate: Int? = null)
    fun applyDegradationPreference(preference: VonageDegradationPref)
    fun toggleNoiseSuppression(noiseSuppression: VonageNoiseSuppression): Result<VonageNoiseSuppression>

    fun setVideoListener(listener: VonagePublisherVideoListener?)
    fun setPublisherListener(listener: VonagePublisherKitListener?)
    fun setMuteListener(listener: VonageMuteListener?)
    fun setCameraListener(listener: VonageCameraListener?)
    fun setAudioLevelListener(listener: VonageAudioLevelListener?)
    fun setVideoStatsListener(listener: VonagePublisherVideoStatsListener?)
    fun setAudioStatsListener(listener: VonagePublisherAudioStatsListener?)
}

// region Publisher listener interfaces

interface VonagePublisherVideoListener {
    fun onVideoEnabled(reason: String)
    fun onVideoDisabled(reason: String)
    fun onVideoDisableWarning()
    fun onVideoDisableWarningLifted()
}

interface VonagePublisherKitListener {
    fun onStreamCreated(stream: VonageStream)
    fun onStreamDestroyed(stream: VonageStream)
    fun onError(error: VonageError)
}

fun interface VonageMuteListener {
    fun onMuteForced()
}

interface VonageCameraListener {
    fun onCameraChanged(cameraIndex: Int)
    fun onCameraError(error: VonageError)
}

fun interface VonageAudioLevelListener {
    fun onAudioLevelUpdated(audioLevel: Float)
}

// endregion

// region Publisher stats

fun interface VonagePublisherVideoStatsListener {
    fun onVideoStats(stats: List<VonagePublisherVideoStatsEntry>)
}

data class VonagePublisherVideoStatsEntry(
    val startTime: Double,
    val timeStamp: Double,
    val videoPacketsSent: Long,
    val videoPacketsLost: Long,
    val videoBytesSent: Long,
    val connectionEstimatedBandwidth: Long,
    val videoLayers: List<VonageVideoLayer>,
)

data class VonageVideoLayer(
    val height: Int,
    val width: Int,
    val codec: String,
    val encodedFrameRate: Double,
    val qualityLimitationReason: String,
    val scalabilityMode: String?,
    val bitrate: Long,
    val totalBitrate: Long,
)

fun interface VonagePublisherAudioStatsListener {
    fun onAudioStats(stats: List<VonagePublisherAudioStatsEntry>)
}

data class VonagePublisherAudioStatsEntry(
    val startTime: Double,
    val timeStamp: Double,
    val audioPacketsSent: Long,
    val audioPacketsLost: Long,
    val audioBytesSent: Long,
    val connectionEstimatedBandwidth: Long,
)

// endregion

// region SDK-level enums used by VonagePublisher

enum class VonageBlurLevel {
    NONE,
    LOW,
    HIGH;

    companion object {
        private val map = entries.toTypedArray()
        infix fun by(index: Int): VonageBlurLevel = map[index % entries.size]
    }
}

enum class VonageNoiseSuppression {
    ENABLED,
    DISABLED,
}

enum class VonageBitratePreset {
    DEFAULT,
    BW_SAVER,
    EXTRA_BW_SAVER,
    CUSTOM,
}

enum class VonageDegradationPref {
    NOT_SET,
    MAINTAIN_FRAME_RATE_AND_RESOLUTION,
    MAINTAIN_FRAME_RATE,
    MAINTAIN_RESOLUTION,
    BALANCED,
}

// endregion
