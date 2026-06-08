package com.vonage.android.kotlin.sdk.internal

import android.view.View
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.Stream
import com.vonage.android.kotlin.sdk.VonageAudioLevelListener
import com.vonage.android.kotlin.sdk.VonageBitratePreset
import com.vonage.android.kotlin.sdk.VonageBlurLevel
import com.vonage.android.kotlin.sdk.VonageCameraListener
import com.vonage.android.kotlin.sdk.VonageDegradationPref
import com.vonage.android.kotlin.sdk.VonageMuteListener
import com.vonage.android.kotlin.sdk.VonageNoiseSuppression
import com.vonage.android.kotlin.sdk.VonagePublisher
import com.vonage.android.kotlin.sdk.VonagePublisherAudioStatsEntry
import com.vonage.android.kotlin.sdk.VonagePublisherAudioStatsListener
import com.vonage.android.kotlin.sdk.VonagePublisherKitListener
import com.vonage.android.kotlin.sdk.VonagePublisherVideoListener
import com.vonage.android.kotlin.sdk.VonagePublisherVideoStatsEntry
import com.vonage.android.kotlin.sdk.VonagePublisherVideoStatsListener
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageVideoLayer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * OpenTok-backed implementation of [VonagePublisher].
 *
 * Wraps a real [Publisher] and translates all listener callbacks
 * into SDK-agnostic types.
 */
internal class OpenTokPublisher(
    internal val raw: Publisher,
) : VonagePublisher {

    override val name: String get() = raw.name
    override val view: View get() = raw.view
    override val stream: VonageStream? get() = raw.stream?.toVonage()

    override var publishVideo: Boolean
        get() = raw.publishVideo
        set(value) {
            raw.publishVideo = value
        }

    override var publishAudio: Boolean
        get() = raw.publishAudio
        set(value) {
            raw.publishAudio = value
        }

    override var publishCaptions: Boolean
        get() = raw.publishCaptions
        set(value) {
            raw.publishCaptions = value
        }

    override fun cycleCamera() = raw.cycleCamera()

    override fun destroy() = raw.destroy()

    override fun stop() = raw.onStop()

    override fun reinitializeRenderer() {
        // Reinitialize the renderer by setting the style again
        // This forces the OpenTok SDK to remeasure and apply correct scaling/cropping
        raw.renderer?.setStyle(
            BaseVideoRenderer.STYLE_VIDEO_SCALE,
            BaseVideoRenderer.STYLE_VIDEO_FIT,
        )
    }

    // region Noise suppression

    override fun toggleNoiseSuppression(noiseSuppression: VonageNoiseSuppression): Result<VonageNoiseSuppression> =
        when (noiseSuppression) {
            VonageNoiseSuppression.ENABLED -> removeNoiseSuppression()
            VonageNoiseSuppression.DISABLED -> applyNoiseSuppression()
        }

    internal fun applyNoiseSuppression(): Result<VonageNoiseSuppression> =
        runCatching {
            raw.setAudioTransformers(
                arrayListOf(raw.AudioTransformer("NoiseSuppression", ""))
            )
            VonageNoiseSuppression.ENABLED
        }

    internal fun removeNoiseSuppression(): Result<VonageNoiseSuppression> =
        runCatching {
            raw.setAudioTransformers(arrayListOf())
            VonageNoiseSuppression.DISABLED
        }

    // endregion

    // region Blur

    override fun applyBlur(level: VonageBlurLevel) {
        val transformers: ArrayList<PublisherKit.VideoTransformer> = when (level) {
            VonageBlurLevel.NONE -> arrayListOf()
            VonageBlurLevel.LOW,
            VonageBlurLevel.HIGH -> {
                val params = Json.encodeToString(BlurRadius(level.toSdkBlurLevel()))
                arrayListOf(raw.VideoTransformer(BLUR_KEY, params))
            }
        }
        raw.setVideoTransformers(transformers)
    }

    override fun applyBackgroundImage(imageFilePath: String) {
        val params = Json.encodeToString(BackgroundImageParams(imageFilePath))
        val transformers = arrayListOf(raw.VideoTransformer(BACKGROUND_REPLACEMENT_KEY, params))
        raw.setVideoTransformers(transformers)
    }

    // endregion

    // region Bitrate / Degradation

    override fun applyVideoBitrate(preset: VonageBitratePreset, maxBitrate: Int?) {
        raw.videoBitratePreset = preset.toSdk()
        if (preset == VonageBitratePreset.CUSTOM && maxBitrate != null) {
            raw.maxVideoBitrate = maxBitrate
        }
    }

    override fun applyDegradationPreference(preference: VonageDegradationPref) {
        raw.degradationPreference = preference.toSdk()
    }

    // endregion

    // region Listeners

    override fun setVideoListener(listener: VonagePublisherVideoListener?) {
        if (listener == null) {
            raw.setVideoListener(null); return
        }
        raw.setVideoListener(object : PublisherKit.VideoListener {
            override fun onVideoEnabled(p: PublisherKit, reason: String) =
                listener.onVideoEnabled(reason)

            override fun onVideoDisabled(p: PublisherKit, reason: String) =
                listener.onVideoDisabled(reason)

            override fun onVideoDisableWarning(p: PublisherKit) =
                listener.onVideoDisableWarning()

            override fun onVideoDisableWarningLifted(p: PublisherKit) =
                listener.onVideoDisableWarningLifted()
        })
    }

    override fun setPublisherListener(listener: VonagePublisherKitListener?) {
        if (listener == null) {
            raw.setPublisherListener(null); return
        }
        raw.setPublisherListener(object : PublisherKit.PublisherListener {
            override fun onStreamCreated(p: PublisherKit, stream: Stream) =
                listener.onStreamCreated(stream.toVonage())

            override fun onStreamDestroyed(p: PublisherKit, stream: Stream) =
                listener.onStreamDestroyed(stream.toVonage())

            override fun onError(p: PublisherKit, error: OpentokError) =
                listener.onError(error.toVonage())
        })
    }

    override fun setMuteListener(listener: VonageMuteListener?) {
        if (listener == null) {
            raw.setMuteListener(null); return
        }
        raw.setMuteListener { _ -> listener.onMuteForced() }
    }

    override fun setCameraListener(listener: VonageCameraListener?) {
        if (listener == null) {
            raw.setCameraListener(null); return
        }
        raw.setCameraListener(object : Publisher.CameraListener {
            override fun onCameraChanged(p: Publisher, cameraIndex: Int) =
                listener.onCameraChanged(cameraIndex)

            override fun onCameraError(p: Publisher, error: OpentokError) =
                listener.onCameraError(error.toVonage())
        })
    }

    override fun setAudioLevelListener(listener: VonageAudioLevelListener?) {
        if (listener == null) {
            raw.setAudioLevelListener(null); return
        }
        raw.setAudioLevelListener { _, level -> listener.onAudioLevelUpdated(level) }
    }

    override fun setVideoStatsListener(listener: VonagePublisherVideoStatsListener?) {
        if (listener == null) {
            raw.setVideoStatsListener(null); return
        }
        raw.setVideoStatsListener { _, stats ->
            val entries = stats.map { s ->
                VonagePublisherVideoStatsEntry(
                    startTime = s.startTime,
                    timeStamp = s.timeStamp,
                    videoPacketsSent = s.videoPacketsSent,
                    videoPacketsLost = s.videoPacketsLost,
                    videoBytesSent = s.videoBytesSent,
                    connectionEstimatedBandwidth = s.transport.connectionEstimatedBandwidth,
                    videoLayers = s.videoLayers?.map { layer ->
                        VonageVideoLayer(
                            height = layer.height,
                            width = layer.width,
                            codec = layer.codec,
                            encodedFrameRate = layer.encodedFrameRate,
                            qualityLimitationReason = layer.qualityLimitationReason,
                            scalabilityMode = layer.scalabilityMode,
                            bitrate = layer.bitrate,
                            totalBitrate = layer.totalBitrate,
                        )
                    }.orEmpty(),
                )
            }
            listener.onVideoStats(entries)
        }
    }

    override fun setAudioStatsListener(listener: VonagePublisherAudioStatsListener?) {
        if (listener == null) {
            raw.setAudioStatsListener(null); return
        }
        raw.setAudioStatsListener { _, stats ->
            val entries = stats.map { s ->
                VonagePublisherAudioStatsEntry(
                    startTime = s.startTime,
                    timeStamp = s.timeStamp,
                    audioPacketsSent = s.audioPacketsSent,
                    audioPacketsLost = s.audioPacketsLost,
                    audioBytesSent = s.audioBytesSent,
                    connectionEstimatedBandwidth = s.transport.connectionEstimatedBandwidth,
                )
            }
            listener.onAudioStats(entries)
        }
    }

    // endregion

    companion object {
        private const val BLUR_KEY = "BackgroundBlur"
        private const val BACKGROUND_REPLACEMENT_KEY = "BackgroundReplacement"
    }
}

// region SDK mapping helpers

private fun VonageBitratePreset.toSdk(): PublisherKit.VideoBitratePreset = when (this) {
    VonageBitratePreset.DEFAULT -> PublisherKit.VideoBitratePreset.VideoBitratePresetDefault
    VonageBitratePreset.BW_SAVER -> PublisherKit.VideoBitratePreset.VideoBitratePresetBwSaver
    VonageBitratePreset.EXTRA_BW_SAVER -> PublisherKit.VideoBitratePreset.VideoBitratePresetExtraBwSaver
    VonageBitratePreset.CUSTOM -> PublisherKit.VideoBitratePreset.VideoBitratePresetCustom
}

private fun VonageDegradationPref.toSdk(): PublisherKit.DegradationPreference = when (this) {
    VonageDegradationPref.NOT_SET ->
        PublisherKit.DegradationPreference.DegradationPreferenceNotSet

    VonageDegradationPref.MAINTAIN_FRAME_RATE_AND_RESOLUTION ->
        PublisherKit.DegradationPreference.DegradationPreferenceMaintainFrameRateAndResolution

    VonageDegradationPref.MAINTAIN_FRAME_RATE ->
        PublisherKit.DegradationPreference.DegradationPreferenceMaintainFrameRate

    VonageDegradationPref.MAINTAIN_RESOLUTION ->
        PublisherKit.DegradationPreference.DegradationPreferenceMaintainResolution

    VonageDegradationPref.BALANCED ->
        PublisherKit.DegradationPreference.DegradationPreferenceBalanced
}

private fun VonageBlurLevel.toSdkBlurLevel(): SdkBlurName = when (this) {
    VonageBlurLevel.LOW -> SdkBlurName.Low
    VonageBlurLevel.HIGH -> SdkBlurName.High
    VonageBlurLevel.NONE -> SdkBlurName.None
}

@Serializable
private enum class SdkBlurName { Low, High, None }

@Serializable
private data class BlurRadius(val radius: SdkBlurName)

@Serializable
private data class BackgroundImageParams(@kotlinx.serialization.SerialName("image_file_path") val imageFilePath: String)

// endregion
