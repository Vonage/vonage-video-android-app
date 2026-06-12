package com.vonage.android.kotlin.internal

import android.content.Context
import android.media.projection.MediaProjection
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_ID
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_SCREEN_ID
import com.vonage.android.kotlin.sdk.VonageBlurLevel
import com.vonage.android.kotlin.sdk.VonageCaptureFrameRate
import com.vonage.android.kotlin.sdk.VonageCaptureResolution
import com.vonage.android.kotlin.sdk.VonagePublisherConfig
import com.vonage.android.kotlin.sdk.VonageScreenShareConfig
import com.vonage.android.kotlin.sdk.VonageSdkFactory
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference.NOT_SET
import com.vonage.android.kotlin.model.PreviewPublisherState
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.VideoBitratePreset.DEFAULT
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.kotlin.model.toVonageBitratePreset
import com.vonage.android.kotlin.model.toVonageDegradationPref
import com.vonage.android.kotlin.model.toVonageVideoCodec
import com.vonage.logger.vonageLogger

/**
 * Factory for creating and managing Publisher instances.
 *
 * Uses [VonageSdkFactory] to create wrapped publishers,
 * keeping all OpenTok SDK details behind the abstraction.
 */
class PublisherFactory(
    private val sdkFactory: VonageSdkFactory,
) {

    var publisherHolder: VeraPublisherHolder? = null
    private var publisherConfig: PublisherConfig? = null

    val currentConfig: PublisherConfig?
        get() = publisherConfig

    fun init(config: PublisherConfig) {
        publisherConfig = config
    }

    fun createPreviewPublisher(context: Context): PreviewPublisherState {
        val vonagePublisher = sdkFactory.createPublisher(context, buildVonageConfig())
        publisherHolder = VeraPublisherHolder(publisher = vonagePublisher)
        val effect = currentConfig?.initialVideoEffect ?: VideoEffect.None
        val state = PreviewPublisherState(
            vonagePublisher,
            captureInfoLabel = buildCaptureInfoLabel(context),
            initialVideoEffect = effect,
        )
        if (effect is VideoEffect.BackgroundImage) {
            vonagePublisher.applyBackgroundImage(effect.imagePath)
        }
        return state
    }

    fun createPublisherState(context: Context): PublisherState {
        val vonagePublisher = sdkFactory.createPublisher(context, buildVonageConfig())
        vonagePublisher.publishCaptions = currentConfig?.publishCaptions ?: false
        val effect = currentConfig?.initialVideoEffect ?: VideoEffect.None
        val participant = PublisherState(
            publisherId = PUBLISHER_ID,
            vonagePublisher = vonagePublisher,
            captureInfoLabel = buildCaptureInfoLabel(context),
            initialVideoEffect = effect,
        )
        publisherHolder = VeraPublisherHolder(publisher = vonagePublisher)
        if (effect is VideoEffect.BackgroundImage) {
            vonagePublisher.applyBackgroundImage(effect.imagePath)
        }
        return participant
    }

    fun createScreenSharePublisherState(
        context: Context,
        mediaProjection: MediaProjection,
        name: String,
    ): PublisherState {
        val vonagePublisher = sdkFactory.createScreenSharePublisher(
            context,
            VonageScreenShareConfig(name = name, mediaProjection = mediaProjection),
        )
        publisherHolder?.screenPublisher = vonagePublisher
        return PublisherState(
            publisherId = PUBLISHER_SCREEN_ID,
            vonagePublisher = vonagePublisher,
        )
    }

    fun destroyPublisher() {
        publisherHolder?.publisher?.let {
            it.destroy()
            it.stop()
        }
        publisherHolder = null
        vonageLogger.i(TAG, "Destroy publisher")
    }

    private fun buildVonageConfig(): VonagePublisherConfig {
        val config = currentConfig
        return VonagePublisherConfig(
            name = config?.name.orEmpty(),
            hasVideoTrack = true,
            hasAudioTrack = true,
            publishVideo = config?.publishVideo ?: true,
            publishAudio = config?.publishAudio ?: true,
            blurLevel = videoEffectToVonageBlurLevel(config?.initialVideoEffect),
            cameraIndex = config?.cameraIndex ?: DEFAULT_CAMERA_INDEX,
            captureResolution = config?.captureResolution?.toVonageCaptureResolution(),
            captureFrameRate = (config?.captureFrameRate ?: CaptureFrameRate.FPS_15).toVonageCaptureFrameRate(),
            preferredVideoCodecOrder = config?.preferredVideoCodecOrder?.map { it.toVonageVideoCodec() },
            senderStatsTrack = config?.senderStatsTrack ?: false,
            opusDtxEnabled = config?.opusDtxEnabled ?: true,
            publisherAudioFallback = config?.publisherAudioFallback ?: true,
            subscriberAudioFallback = config?.subscriberAudioFallback ?: true,
            videoBitratePreset = (config?.videoBitrateConfig?.preset ?: DEFAULT).toVonageBitratePreset(),
            maxVideoBitrate = config?.videoBitrateConfig?.maxBitrate,
            degradationPreference = (config?.degradationPreference ?: NOT_SET).toVonageDegradationPref(),
            audioBitrate = config?.audioBitrate,
            allowAudioCaptureWhileMuted = true,
        )
    }

    @Suppress("MagicNumber")
    private fun buildCaptureInfoLabel(context: Context): String {
        val config = currentConfig
        val resolution = config?.captureResolution ?: resolveOptimalResolution(context)
        val frameRate = config?.captureFrameRate ?: CaptureFrameRate.FPS_15
        val resolutionLabel = when (resolution) {
            CaptureResolution.LOW -> "288p"
            CaptureResolution.MEDIUM -> "480p"
            CaptureResolution.HIGH -> "720p"
            CaptureResolution.HIGH_1080P -> "1080p"
        }
        return "$resolutionLabel / ${frameRate.fps}fps"
    }

    private fun resolveOptimalResolution(context: Context): CaptureResolution =
        when (sdkFactory.getOptimalResolution(context)) {
            VonageCaptureResolution.LOW -> CaptureResolution.LOW
            VonageCaptureResolution.MEDIUM -> CaptureResolution.MEDIUM
            VonageCaptureResolution.HIGH -> CaptureResolution.HIGH
            VonageCaptureResolution.HIGH_1080P -> CaptureResolution.HIGH_1080P
        }

    companion object {
        private const val TAG = "PublisherFactory"
        private const val DEFAULT_CAMERA_INDEX = 1
    }
}

// region Domain → SDK config mapping

private fun videoEffectToVonageBlurLevel(effect: VideoEffect?): VonageBlurLevel = when (effect) {
    VideoEffect.BlurLow -> VonageBlurLevel.LOW
    VideoEffect.BlurHigh -> VonageBlurLevel.HIGH
    else -> VonageBlurLevel.NONE // None and BackgroundImage both start with no blur in SDK config
}

private fun CaptureResolution.toVonageCaptureResolution(): VonageCaptureResolution = when (this) {
    CaptureResolution.LOW -> VonageCaptureResolution.LOW
    CaptureResolution.MEDIUM -> VonageCaptureResolution.MEDIUM
    CaptureResolution.HIGH -> VonageCaptureResolution.HIGH
    CaptureResolution.HIGH_1080P -> VonageCaptureResolution.HIGH_1080P
}

private fun CaptureFrameRate.toVonageCaptureFrameRate(): VonageCaptureFrameRate = when (this) {
    CaptureFrameRate.FPS_1 -> VonageCaptureFrameRate.FPS_1
    CaptureFrameRate.FPS_7 -> VonageCaptureFrameRate.FPS_7
    CaptureFrameRate.FPS_15 -> VonageCaptureFrameRate.FPS_15
    CaptureFrameRate.FPS_30 -> VonageCaptureFrameRate.FPS_30
}

// endregion
