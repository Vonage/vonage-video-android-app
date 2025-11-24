package com.vonage.android.kotlin.internal

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.VeraCameraCapturer
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_ID
import com.vonage.android.kotlin.ext.applyVideoBlur
import com.vonage.android.kotlin.model.PreviewPublisherState
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherState

class PublisherFactory {

    var publisherHolder: VeraPublisherHolder? = null
    private var publisherConfig: PublisherConfig? = null

    fun init(config: PublisherConfig) {
        publisherConfig = config
    }

    fun createPreviewPublisher(context: Context, name: String): PreviewPublisherState {
        val publisher = createPublisher(context, name)
        return PreviewPublisherState(publisher)
    }

    fun createPublisher(context: Context): PublisherState {
        Log.d(TAG, "build publisher with $publisherConfig")
        val name = publisherConfig?.name ?: Default.PUBLISHER_NAME
        val publisher = createPublisher(context, name)
        val participant = PublisherState(PUBLISHER_ID, publisher)
        publisherHolder = VeraPublisherHolder(
            publisher = publisher,
        )
        return participant
    }

    fun destroyPublisher() {
        publisherHolder?.publisher?.let {
            it.destroy()
            it.onStop()
        }
        publisherHolder = null
        Log.i(TAG, "Destroy publisher")
    }

    @Suppress("MagicNumber")
    private fun Context.getOptimalResolution(): Publisher.CameraCaptureResolution {
        val memoryClass = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
        return when {
            memoryClass >= 512 -> Publisher.CameraCaptureResolution.HIGH
            memoryClass >= 256 -> Publisher.CameraCaptureResolution.MEDIUM
            else -> Publisher.CameraCaptureResolution.LOW
        }
    }

    private fun createPublisher(context: Context, name: String): Publisher =
        Publisher.Builder(context)
            .name(name)
            .videoTrack(true)
            .audioTrack(true)
            .capturer(
                VeraCameraCapturer(
                    context = context,
                    resolution = context.getOptimalResolution(),
                    frameRate = Default.PUBLISHER_FRAME_RATE,
                    initialCameraIndex = publisherConfig?.cameraIndex ?: Default.PUBLISHER_CAMERA_INDEX,
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
            }

    object Default {
        val PUBLISHER_FRAME_RATE = Publisher.CameraCaptureFrameRate.FPS_15 // implement adaptative frame rate
        const val PUBLISHER_CAMERA_INDEX = 0
        const val PUBLISHER_NAME = ""
    }

    private companion object {
        const val TAG: String = "PublisherFactory"
    }
}