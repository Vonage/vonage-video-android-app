package com.vonage.android.kotlin.model

/**
 * Configuration for creating a publisher instance.
 *
 * @property name Display name for the publisher
 * @property publishVideo Initial video enabled state
 * @property publishAudio Initial audio enabled state
 * @property blurLevel Initial background blur level
 * @property cameraIndex Initial camera (0 = back, 1 = front)
 */
data class PublisherConfig(
    val name: String,
    val publishVideo: Boolean,
    val publishAudio: Boolean,
    val blurLevel: BlurLevel,
    val cameraIndex: Int,
    val senderStatsTrack: Boolean = false,
    val opusDtxEnabled: Boolean = true,
    val publisherAudioFallback: Boolean = true,
    val subscriberAudioFallback: Boolean = true,
    val videoBitrateConfig: VideoBitrateConfig = VideoBitrateConfig(
        preset = VideoBitratePreset.DEFAULT,
        maxBitrate = VideoBitratePreset.DEFAULT.defaultMaxBitrate,
    ),
    val degradationPreference: DegradationPreference = DegradationPreference.NOT_SET,
    val captureFrameRate: CaptureFrameRate = CaptureFrameRate.FPS_15,
    val captureResolution: CaptureResolution? = null,
    val preferredVideoCodecOrder: List<VideoCodec>? = null,
    val audioBitrate: Int? = null,
)
