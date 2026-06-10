package com.vonage.android.kotlin.sdk.internal

import android.app.ActivityManager
import android.content.Context
import com.opentok.android.AudioDeviceManager
import com.opentok.android.BaseAudioDevice
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.PublisherKit.PublisherKitVideoType
import com.opentok.android.Session
import com.opentok.android.VeraCameraCapturer
import com.vonage.android.kotlin.sdk.VonageCaptureFrameRate
import com.vonage.android.kotlin.sdk.VonageCaptureResolution
import com.vonage.android.kotlin.sdk.VonagePublisher
import com.vonage.android.kotlin.sdk.VonagePublisherConfig
import com.vonage.android.kotlin.sdk.VonageScreenShareConfig
import com.vonage.android.kotlin.sdk.VonageSdkFactory
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.sdk.VonageVideoCodec
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Default [VonageSdkFactory] implementation backed by the OpenTok Android SDK.
 *
 * All raw OpenTok builder calls and SDK enum mappings are encapsulated here
 * so that the rest of the codebase only works with the Vonage wrapper types.
 */
internal class OpenTokSdkFactory(
    baseAudioDevice: BaseAudioDevice,
) : VonageSdkFactory {

    init {
        // AudioDeviceManager.setAudioDevice must be called before any session or publisher
        // is created and cannot be called again once the SDK is initialized. Guard with an
        // AtomicBoolean so that creating multiple factory instances (e.g. from MeetingRoomContainer
        // and from the Hilt graph) does not trigger a second call and throw IllegalStateException.
        if (audioDeviceRegistered.compareAndSet(false, true)) {
            AudioDeviceManager.setAudioDevice(baseAudioDevice)
        }
    }

    companion object {
        private val audioDeviceRegistered = AtomicBoolean(false)
    }

    override fun createSession(
        context: Context,
        apiKey: String,
        sessionId: String,
    ): VonageSession {
        val session = Session.Builder(context, apiKey, sessionId)
            .setSinglePeerConnection(true)
            .setSessionMigration(true)
            .sessionOptions(object : Session.SessionOptions() {
                override fun useTextureViews(): Boolean {
                    return true
                }
            })
            .build()
        return OpenTokSession(session)
    }

    override fun createPublisher(
        context: Context,
        config: VonagePublisherConfig,
    ): VonagePublisher {
        val resolution = config.captureResolution?.toSdk()
            ?: context.getOptimalSdkResolution()
        val frameRate = config.captureFrameRate.toSdk()

        val publisher = Publisher.Builder(context)
            .name(config.name)
            .videoTrack(config.hasVideoTrack)
            .audioTrack(config.hasAudioTrack)
            .senderStatsTrack(config.senderStatsTrack)
            .enableOpusDtx(config.opusDtxEnabled)
            .publisherAudioFallbackEnabled(config.publisherAudioFallback)
            .subscriberAudioFallbackEnabled(config.subscriberAudioFallback)
            .preferredVideoCodecs(resolvePreferredVideoCodecs(config.preferredVideoCodecOrder))
            .frameRate(frameRate)
            .resolution(resolution)
            .let { builder ->
                config.audioBitrate?.let { builder.audioBitrate(it) } ?: builder
            }
            .capturer(
                VeraCameraCapturer(
                    context = context,
                    resolution = resolution,
                    frameRate = frameRate,
                    initialCameraIndex = config.cameraIndex,
                ),
            )
            .allowAudioCaptureWhileMuted(config.allowAudioCaptureWhileMuted)
            .build()
            .apply {
                renderer?.setStyle(
                    BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FIT,
                )
                publishVideo = config.publishVideo
                publishAudio = config.publishAudio
                publisherVideoType = PublisherKitVideoType.PublisherKitVideoTypeCamera
            }

        val wrapper = OpenTokPublisher(publisher)
        wrapper.applyBlur(config.blurLevel)
        wrapper.applyVideoBitrate(config.videoBitratePreset, config.maxVideoBitrate)
        wrapper.applyDegradationPreference(config.degradationPreference)
        return wrapper
    }

    override fun createScreenSharePublisher(
        context: Context,
        config: VonageScreenShareConfig,
    ): VonagePublisher {
        val publisher = Publisher.Builder(context)
            .name(config.name)
            .capturer(ScreenSharingCapturer(context, config.mediaProjection))
            .videoTrack(true)
            .audioTrack(false)
            .build()
            .apply {
                renderer?.setStyle(
                    BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FIT,
                )
                publishVideo = true
                publishAudio = false
                publisherVideoType = PublisherKitVideoType.PublisherKitVideoTypeScreen
            }
        return OpenTokPublisher(publisher)
    }

    @Suppress("MagicNumber")
    override fun getOptimalResolution(context: Context): VonageCaptureResolution {
        val memoryClass =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
        return when {
            memoryClass >= 512 -> VonageCaptureResolution.HIGH
            memoryClass >= 256 -> VonageCaptureResolution.MEDIUM
            else -> VonageCaptureResolution.LOW
        }
    }

    // region Private helpers

    private fun resolvePreferredVideoCodecs(
        order: List<VonageVideoCodec>?,
    ): PublisherKit.PreferredVideoCodecs {
        if (order == null) return PublisherKit.PreferredVideoCodecs.automatic()
        val sdkCodecs = ArrayList(order.map { it.toSdk() })
        return PublisherKit.PreferredVideoCodecs.manual(sdkCodecs)
    }

    @Suppress("MagicNumber")
    private fun Context.getOptimalSdkResolution(): Publisher.CameraCaptureResolution {
        val memoryClass =
            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
        return when {
            memoryClass >= 512 -> Publisher.CameraCaptureResolution.HIGH
            memoryClass >= 256 -> Publisher.CameraCaptureResolution.MEDIUM
            else -> Publisher.CameraCaptureResolution.LOW
        }
    }

    // endregion
}

// region SDK enum mappings

private fun VonageCaptureResolution.toSdk(): Publisher.CameraCaptureResolution = when (this) {
    VonageCaptureResolution.LOW -> Publisher.CameraCaptureResolution.LOW
    VonageCaptureResolution.MEDIUM -> Publisher.CameraCaptureResolution.MEDIUM
    VonageCaptureResolution.HIGH -> Publisher.CameraCaptureResolution.HIGH
    VonageCaptureResolution.HIGH_1080P -> Publisher.CameraCaptureResolution.HIGH_1080P
}

private fun VonageCaptureFrameRate.toSdk(): Publisher.CameraCaptureFrameRate = when (this) {
    VonageCaptureFrameRate.FPS_1 -> Publisher.CameraCaptureFrameRate.FPS_1
    VonageCaptureFrameRate.FPS_7 -> Publisher.CameraCaptureFrameRate.FPS_7
    VonageCaptureFrameRate.FPS_15 -> Publisher.CameraCaptureFrameRate.FPS_15
    VonageCaptureFrameRate.FPS_30 -> Publisher.CameraCaptureFrameRate.FPS_30
}

private fun VonageVideoCodec.toSdk(): PublisherKit.PreferredVideoCodecs.Codec = when (this) {
    VonageVideoCodec.VP8 -> PublisherKit.PreferredVideoCodecs.Codec.VP8
    VonageVideoCodec.H264 -> PublisherKit.PreferredVideoCodecs.Codec.H264
    VonageVideoCodec.VP9 -> PublisherKit.PreferredVideoCodecs.Codec.VP9
}

// endregion
