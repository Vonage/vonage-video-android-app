package com.vonage.android.kotlin

import android.content.Context
import android.media.projection.MediaProjection
import com.vonage.android.kotlin.internal.OpenTokSdkFactory

/**
 * Configuration for creating a [VonagePublisher].
 *
 * Contains all build-time and runtime settings for a publisher instance.
 */
data class VonagePublisherConfig(
    val name: String = "",
    val hasVideoTrack: Boolean = true,
    val hasAudioTrack: Boolean = true,
    val publishVideo: Boolean = true,
    val publishAudio: Boolean = true,
    val blurLevel: VonageBlurLevel = VonageBlurLevel.NONE,
    val cameraIndex: Int = 1,
    val captureResolution: VonageCaptureResolution? = null,
    val captureFrameRate: VonageCaptureFrameRate = VonageCaptureFrameRate.FPS_15,
    val preferredVideoCodecOrder: List<VonageVideoCodec>? = null,
    val senderStatsTrack: Boolean = false,
    val opusDtxEnabled: Boolean = true,
    val publisherAudioFallback: Boolean = true,
    val subscriberAudioFallback: Boolean = true,
    val videoBitratePreset: VonageBitratePreset = VonageBitratePreset.DEFAULT,
    val maxVideoBitrate: Int? = null,
    val degradationPreference: VonageDegradationPref = VonageDegradationPref.NOT_SET,
    val audioBitrate: Int? = null,
    val allowAudioCaptureWhileMuted: Boolean = true,
)

/**
 * Configuration for creating a screen share publisher.
 */
data class VonageScreenShareConfig(
    val name: String,
    val mediaProjection: MediaProjection,
)

/**
 * Capture resolution tiers for publisher creation.
 */
enum class VonageCaptureResolution {
    LOW,
    MEDIUM,
    HIGH,
    HIGH_1080P,
}

/**
 * Capture frame rate options for publisher creation.
 */
@Suppress("MagicNumber")
enum class VonageCaptureFrameRate(val fps: Int) {
    FPS_1(1),
    FPS_7(7),
    FPS_15(15),
    FPS_30(30),
}

/**
 * Video codec options for publisher creation.
 */
enum class VonageVideoCodec {
    VP8,
    H264,
    VP9,
}

/**
 * Factory for creating Vonage Video SDK objects.
 *
 * Abstracts the creation of sessions, publishers, and subscribers.
 * External modules depend only on this interface; the underlying SDK
 * implementation is hidden. This makes it straightforward to substitute
 * a fake factory in tests.
 */
interface VonageSdkFactory {

    fun createSession(context: Context, apiKey: String, sessionId: String): VonageSession

    fun createPublisher(context: Context, config: VonagePublisherConfig): VonagePublisher

    fun createScreenSharePublisher(
        context: Context,
        config: VonageScreenShareConfig,
    ): VonagePublisher

    /**
     * Returns the optimal capture resolution for the current device.
     */
    fun getOptimalResolution(context: Context): VonageCaptureResolution

    companion object {
        /**
         * Creates the default OpenTok-backed factory.
         */
        fun create(): VonageSdkFactory = OpenTokSdkFactory()
    }
}
