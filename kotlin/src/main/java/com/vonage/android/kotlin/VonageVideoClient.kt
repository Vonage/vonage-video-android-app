package com.vonage.android.kotlin

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.opentok.android.AudioDeviceManager
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit.PublisherKitVideoType
import com.opentok.android.Session
import com.opentok.android.VeraCameraCapturer
import com.vonage.android.kotlin.ext.applyVideoBlur
import com.vonage.android.kotlin.internal.VeraAudioDevice
import com.vonage.android.kotlin.internal.VeraPublisherHolder
import com.vonage.android.kotlin.internal.toParticipant
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.signal.SignalPlugin

class VonageVideoClient(
    private val context: Context,
    baseAudioDevice: VeraAudioDevice,
    private val signalPlugins: List<SignalPlugin>,
    // configure log level of the entire SDK
) {

    init {
        AudioDeviceManager.setAudioDevice(baseAudioDevice)
    }

    private var session: Session? = null
    private var publisherConfig: PublisherConfig? = null
    private var publisherHolder: VeraPublisherHolder? = null

    fun debugDump(): String {
        val dump = """
            Android Version: ${Build.VERSION.RELEASE} SDK-${Build.VERSION.SDK_INT}
            Device: ${Build.MANUFACTURER} ${Build.MODEL}
            ===
            Session: ${session?.sessionId}
            Connection: ${session?.connection?.connectionId}
            Connection creation time: ${session?.connection?.creationTime}
            ===
        """.trimIndent()
        return dump
    }

    fun configurePublisher(publisherConfig: PublisherConfig) {
        this.publisherConfig = publisherConfig
    }

    fun buildPublisher(context: Context): VeraPublisher {
        Log.d(TAG, "build publisher with $publisherConfig")
        val resolvedName = publisherConfig?.name ?: Default.PUBLISHER_NAME
        val publisher = Publisher.Builder(context)
            .name(resolvedName)
            .videoTrack(true)
            .audioTrack(true)
            .capturer(
                VeraCameraCapturer(
                    context = context,
                    resolution = getOptimalResolution(),
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
                    publishAudio = false//config.publishAudio
                    applyVideoBlur(config.blurLevel)
                }
                publisherVideoType = PublisherKitVideoType.PublisherKitVideoTypeCamera
            }
        val participant = publisher.toParticipant(
            name = resolvedName,
            camera = publisherConfig?.cameraIndex ?: 0,
        )
        publisherHolder = VeraPublisherHolder(
            participant = participant,
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

    fun initializeSession(apiKey: String, sessionId: String, token: String): CallFacade {
        Log.i(TAG, "apiKey: $apiKey")
        Log.i(TAG, "sessionId: $sessionId")
        Log.i(TAG, "token: $token")

        session = Session.Builder(context, apiKey, sessionId)
            .setSinglePeerConnection(true)
            .build()

        session?.capabilities?.let { capabilities ->
            Log.i(TAG, "Session capabilities:")
            Log.i(TAG, capabilities.toString())
        }

        return Call(
            token = token,
            session = session!!,
            publisherHolder = publisherHolder!!,
            signalPlugins = signalPlugins,
        )
    }

    @Suppress("MagicNumber")
    private fun getOptimalResolution(): Publisher.CameraCaptureResolution {
        val memoryClass = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
        return when {
            memoryClass >= 512 -> Publisher.CameraCaptureResolution.HIGH
            memoryClass >= 256 -> Publisher.CameraCaptureResolution.MEDIUM
            else -> Publisher.CameraCaptureResolution.LOW
        }
    }

    private companion object {
        const val TAG: String = "VonageVideoClient"
    }

    object Default {
        val PUBLISHER_FRAME_RATE = Publisher.CameraCaptureFrameRate.FPS_15 // implement adaptative frame rate
        const val PUBLISHER_CAMERA_INDEX = 0
        const val PUBLISHER_NAME = ""
    }
}
