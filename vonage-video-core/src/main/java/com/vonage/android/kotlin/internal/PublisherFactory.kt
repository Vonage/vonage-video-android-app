package com.vonage.android.kotlin.internal

import android.app.ActivityManager
import android.content.Context
import android.media.projection.MediaProjection
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.PublisherKit.PublisherKitVideoType
import com.opentok.android.VeraCameraCapturer
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_ID
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_SCREEN_ID
import com.vonage.android.kotlin.ext.applyDegradationPreference
import com.vonage.android.kotlin.ext.applyVideoBitrate
import com.vonage.android.kotlin.ext.applyVideoBlur
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.PreviewPublisherState
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.toSdkValue
import com.vonage.logger.vonageLogger

/**
 * Factory for creating and managing Publisher instances.
 *
 * Handles publisher lifecycle including creation, configuration, and cleanup.
 * Automatically selects optimal video resolution based on device memory.
 */
class PublisherFactory {

    var publisherHolder: VeraPublisherHolder? = null
    private var publisherConfig: PublisherConfig? = null

    val currentConfig: PublisherConfig?
        get() = publisherConfig

    /**
     * Initializes the factory with publisher configuration.
     *
     * @param config Configuration for publisher creation
     */
    fun init(config: PublisherConfig) {
        publisherConfig = config
    }

    /**
     * Creates a preview-only publisher for camera preview.
     *
     * @param context Android context
     * @return PreviewPublisherState wrapping the publisher
     */
    fun createPreviewPublisher(context: Context): PreviewPublisherState {
        val publisher = createPublisher(context)
        publisherHolder = VeraPublisherHolder(publisher = publisher)
        return PreviewPublisherState(publisher, captureInfoLabel = buildCaptureInfoLabel(context))
    }

    /**
     * Creates a full publisher for the video call.
     *
     * @param context Android context
     * @return PublisherState ready to be published to the session
     */
    fun createPublisherState(context: Context): PublisherState {
        val publisher = createPublisher(context)
        val participant = PublisherState(
            publisherId = PUBLISHER_ID,
            publisher = publisher,
            captureInfoLabel = buildCaptureInfoLabel(context),
        )
        publisherHolder = VeraPublisherHolder(
            publisher = publisher,
        )
        return participant
    }

    fun createScreenSharePublisherState(
        context: Context,
        mediaProjection: MediaProjection,
        name: String
    ): PublisherState {
        val screenPublisher = Publisher.Builder(context)
            .name(name)
            .capturer(ScreenSharingCapturer(context, mediaProjection))
            .build()
            .apply {
                renderer?.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FIT)
                publishVideo = true
                publishAudio = false
                publisherVideoType = PublisherKitVideoType.PublisherKitVideoTypeScreen
            }
        val participant = PublisherState(
            publisherId = PUBLISHER_SCREEN_ID,
            publisher = screenPublisher,
        )
        return participant
    }

    /**
     * Destroys the current publisher and releases resources.
     */
    fun destroyPublisher() {
        publisherHolder?.publisher?.let {
            it.destroy()
            it.onStop()
        }
        publisherHolder = null
        vonageLogger.i(TAG, "Destroy publisher")
    }

    /**
     * Determines optimal video resolution based on device memory.
     *
     * @return HIGH for 512MB+, MEDIUM for 256MB+, LOW otherwise
     */
    @Suppress("MagicNumber")
    private fun Context.getOptimalResolution(): Publisher.CameraCaptureResolution {
        val memoryClass =
            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
        return when {
            memoryClass >= 512 -> Publisher.CameraCaptureResolution.HIGH
            memoryClass >= 256 -> Publisher.CameraCaptureResolution.MEDIUM
            else -> Publisher.CameraCaptureResolution.LOW
        }
    }

    /**
     * Maps the app-level [CaptureFrameRate] to the SDK enum.
     */
    private fun resolveFrameRate(): Publisher.CameraCaptureFrameRate =
        when (publisherConfig?.captureFrameRate) {
            CaptureFrameRate.FPS_1 -> Publisher.CameraCaptureFrameRate.FPS_1
            CaptureFrameRate.FPS_7 -> Publisher.CameraCaptureFrameRate.FPS_7
            CaptureFrameRate.FPS_15 -> Publisher.CameraCaptureFrameRate.FPS_15
            CaptureFrameRate.FPS_30 -> Publisher.CameraCaptureFrameRate.FPS_30
            null -> Default.PUBLISHER_FRAME_RATE
        }

    /**
     * Resolves the capture resolution from config, falling back to device-optimal.
     */
    private fun Context.resolveResolution(): Publisher.CameraCaptureResolution =
        when (publisherConfig?.captureResolution) {
            CaptureResolution.LOW -> Publisher.CameraCaptureResolution.LOW
            CaptureResolution.MEDIUM -> Publisher.CameraCaptureResolution.MEDIUM
            CaptureResolution.HIGH -> Publisher.CameraCaptureResolution.HIGH
            CaptureResolution.HIGH_1080P -> Publisher.CameraCaptureResolution.HIGH_1080P
            null -> getOptimalResolution()
        }

    /**
     * Resolves the preferred video codecs from config.
     */
    private fun resolvePreferredVideoCodecs(): PublisherKit.PreferredVideoCodecs {
        val order = publisherConfig?.preferredVideoCodecOrder
            ?: return PublisherKit.PreferredVideoCodecs.automatic()
        val sdkCodecs = ArrayList(order.map { it.toSdkValue() })
        return PublisherKit.PreferredVideoCodecs.manual(sdkCodecs)
    }

    /**
     * Builds a human-readable label with the resolved capture resolution and frame rate.
     * E.g. "720p / 15 FPS".
     */
    @Suppress("MagicNumber")
    private fun buildCaptureInfoLabel(context: Context): String {
        val resolutionLabel = when (context.resolveResolution()) {
            Publisher.CameraCaptureResolution.LOW -> "288p"
            Publisher.CameraCaptureResolution.MEDIUM -> "480p"
            Publisher.CameraCaptureResolution.HIGH -> "720p"
            Publisher.CameraCaptureResolution.HIGH_1080P -> "1080p"
        }
        val fpsValue = when (resolveFrameRate()) {
            Publisher.CameraCaptureFrameRate.FPS_1 -> 1
            Publisher.CameraCaptureFrameRate.FPS_7 -> 7
            Publisher.CameraCaptureFrameRate.FPS_15 -> 15
            Publisher.CameraCaptureFrameRate.FPS_30 -> 30
        }
        return "$resolutionLabel / ${fpsValue}fps"
    }

    /**
     * Internal helper to create a configured Publisher instance.
     */
    private fun createPublisher(context: Context): Publisher =
        Publisher.Builder(context)
            .name(currentConfig?.name.orEmpty())
            .videoTrack(true)
            .audioTrack(true)
            .senderStatsTrack(currentConfig?.senderStatsTrack ?: false)
            .enableOpusDtx(currentConfig?.opusDtxEnabled ?: true)
            .publisherAudioFallbackEnabled(currentConfig?.publisherAudioFallback ?: true)
            .subscriberAudioFallbackEnabled(currentConfig?.subscriberAudioFallback ?: true)
            .preferredVideoCodecs(resolvePreferredVideoCodecs())
            .frameRate(resolveFrameRate())
            .resolution(context.resolveResolution())
            .let { builder ->
                currentConfig?.audioBitrate?.let { builder.audioBitrate(it) } ?: builder
            }
            .capturer(
                VeraCameraCapturer(
                    context = context,
                    resolution = context.resolveResolution(),
                    frameRate = resolveFrameRate(),
                    initialCameraIndex = currentConfig?.cameraIndex
                        ?: Default.PUBLISHER_CAMERA_INDEX,
                )
            )
            .build()
            .apply {
                renderer?.setStyle(
                    BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FIT,
                )
                currentConfig?.let { config ->
                    publishVideo = config.publishVideo
                    publishAudio = config.publishAudio
                    applyVideoBlur(config.blurLevel)
                }
                publisherVideoType = PublisherKitVideoType.PublisherKitVideoTypeCamera
                applyVideoBitrate(currentConfig?.videoBitrateConfig)
                applyDegradationPreference(currentConfig?.degradationPreference)
            }
            .also {
                vonageLogger.d("PublisherFactory", "publisher created with config $currentConfig")
            }

    /**
     * Default configuration values for publisher creation.
     */
    object Default {
        /** Default frame rate for video capture (15 FPS for better performance) - TODO: Implement adaptive frame rate */
        val PUBLISHER_FRAME_RATE = Publisher.CameraCaptureFrameRate.FPS_15

        /** Default camera index (1 = front camera) */
        const val PUBLISHER_CAMERA_INDEX = 1
    }

    private companion object {
        const val TAG: String = "PublisherFactory"
    }
}