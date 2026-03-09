package com.vonage.android.kotlin.internal

import android.app.ActivityManager
import android.content.Context
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.VeraCameraCapturer
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_ID
import com.vonage.android.kotlin.ext.applyDegradationPreference
import com.vonage.android.kotlin.ext.applyVideoBitrate
import com.vonage.android.kotlin.ext.applyVideoBlur
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.PreviewPublisherState
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherState
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
     * @param name Display name for the publisher
     * @return PreviewPublisherState wrapping the publisher
     */
    fun createPreviewPublisher(context: Context, name: String): PreviewPublisherState {
        val publisher = createPublisher(context, name)
        return PreviewPublisherState(publisher)
    }

    /**
     * Creates a full publisher for the video call.
     *
     * @param context Android context
     * @return PublisherState ready to be published to the session
     */
    fun createPublisher(context: Context): PublisherState {
        vonageLogger.d(TAG, "build publisher with $publisherConfig")
        val name = publisherConfig?.name ?: Default.PUBLISHER_NAME
        val publisher = createPublisher(context, name)
        val participant = PublisherState(PUBLISHER_ID, publisher)
        publisherHolder = VeraPublisherHolder(
            publisher = publisher,
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
     * Internal helper to create a configured Publisher instance.
     */
    private fun createPublisher(context: Context, name: String): Publisher =
        Publisher.Builder(context)
            .name(name)
            .videoTrack(true)
            .audioTrack(true)
            .senderStatsTrack(publisherConfig?.senderStatsTrack ?: false)
            .enableOpusDtx(publisherConfig?.opusDtxEnabled ?: true)
            .publisherAudioFallbackEnabled(publisherConfig?.publisherAudioFallback ?: true)
            .subscriberAudioFallbackEnabled(publisherConfig?.subscriberAudioFallback ?: true)
            .capturer(
                VeraCameraCapturer(
                    context = context,
                    resolution = context.resolveResolution(),
                    frameRate = resolveFrameRate(),
                    initialCameraIndex = publisherConfig?.cameraIndex
                        ?: Default.PUBLISHER_CAMERA_INDEX,
                )
            )
            .build()
            .apply {
                renderer?.setStyle(
                    BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FIT,
                )
                publisherConfig?.let { config ->
                    publishVideo = config.publishVideo
                    publishAudio = config.publishAudio
                    applyVideoBlur(config.blurLevel)
                }
                publisherVideoType = PublisherKit.PublisherKitVideoType.PublisherKitVideoTypeCamera
                applyVideoBitrate(publisherConfig?.videoBitrateConfig)
                applyDegradationPreference(publisherConfig?.degradationPreference)
            }

    /**
     * Default configuration values for publisher creation.
     */
    object Default {
        /** Default frame rate for video capture (15 FPS for better performance) - TODO: Implement adaptive frame rate */
        val PUBLISHER_FRAME_RATE = Publisher.CameraCaptureFrameRate.FPS_15
        
        /** Default camera index (1 = front camera) */
        const val PUBLISHER_CAMERA_INDEX = 1
        
        /** Default publisher name (empty string) */
        const val PUBLISHER_NAME = ""
    }

    private companion object {
        const val TAG: String = "PublisherFactory"
    }
}