package com.vonage.android.kotlin

import android.content.Context
import android.util.Log
import com.opentok.android.AudioDeviceManager
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.opentok.android.Session
import com.opentok.android.Session.SessionOptions
import com.opentok.android.VeraCameraCapturer
import com.vonage.android.kotlin.ext.applyVideoBlur
import com.vonage.android.kotlin.internal.VeraAudioDevice
import com.vonage.android.kotlin.internal.VeraPublisherHolder
import com.vonage.android.kotlin.internal.toParticipant
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.kotlin.signal.ReactionSignalPlugin
import com.vonage.android.kotlin.signal.SignalPlugin

class VonageVideoClient(
    private val context: Context,
    baseAudioDevice: VeraAudioDevice,
    private val signalPlugins: List<SignalPlugin> = listOf(
        ChatSignalPlugin(context),
        ReactionSignalPlugin(),
    ),
    // configure log level of the entire SDK
) {

    init {
        AudioDeviceManager.setAudioDevice(baseAudioDevice)
    }

    private var session: Session? = null
    private var publisherConfig: PublisherConfig? = null
    private var publisherHolder: VeraPublisherHolder? = null

    fun configurePublisher(publisherConfig: PublisherConfig) {
        this.publisherConfig = publisherConfig
    }

    fun buildPublisher(): VeraPublisher {
        Log.d(TAG, "build publisher with $publisherConfig")
        val resolvedName = publisherConfig?.name ?: Default.PUBLISHER_NAME
        val publisher = Publisher.Builder(context)
            .name(resolvedName)
            .videoTrack(true)
            .audioTrack(true)
            .capturer(
                VeraCameraCapturer(
                    context = context,
                    resolution = Default.PUBLISHER_RESOLUTION,
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
            }
        val participant = publisher.toParticipant(
            name = resolvedName,
            camera = publisherConfig?.cameraIndex ?: 0,
        )
        this.publisherHolder = VeraPublisherHolder(
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
            .sessionOptions(
                object : SessionOptions() {
                    override fun useTextureViews(): Boolean = true
                }
            ).build()

        return Call(
            context = context,
            token = token,
            session = session!!,
            publisherHolder = publisherHolder!!,
            signalPlugins = signalPlugins,
        )
    }

    private companion object {
        const val TAG: String = "VonageVideoClient"
    }

    object Default {
        val PUBLISHER_RESOLUTION = Publisher.CameraCaptureResolution.MEDIUM
        val PUBLISHER_FRAME_RATE = Publisher.CameraCaptureFrameRate.FPS_30
        const val PUBLISHER_CAMERA_INDEX = 0
        const val PUBLISHER_NAME = ""
    }
}
