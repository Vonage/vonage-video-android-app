package com.vonage.android.kotlin.ext

import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.vonage.android.kotlin.model.BackgroundBlur.KEY
import com.vonage.android.kotlin.model.BackgroundBlur.params
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.NoiseSuppression
import com.vonage.android.kotlin.model.PublisherState.AudioStats
import com.vonage.android.kotlin.model.PublisherState.VideoLayerStats
import com.vonage.android.kotlin.model.PublisherState.VideoStats
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Applies a background blur video transformer to the publisher.
 *
 * Uses OpenTok's video transformer API to apply blur effects to the camera stream.
 * BlurLevel.NONE removes all transformers, while LOW and HIGH apply corresponding blur.
 *
 * @param blurLevel The desired blur level (NONE, LOW, or HIGH)
 */
internal fun Publisher.applyVideoBlur(blurLevel: BlurLevel) {
    when (blurLevel) {
        BlurLevel.NONE -> arrayListOf()
        BlurLevel.LOW,
        BlurLevel.HIGH -> {
            arrayListOf(VideoTransformer(KEY, params(blurLevel)))
        }
    }.let {
        setVideoTransformers(it)
    }
}

internal fun Publisher.toggleNoiseSuppression(current: NoiseSuppression): Result<NoiseSuppression> =
    when (current) {
        NoiseSuppression.ENABLED -> removeNoiseSuppression()
        NoiseSuppression.DISABLED -> applyNoiseSuppression()
    }

internal fun Publisher.applyNoiseSuppression(): Result<NoiseSuppression> =
    runCatching {
        setAudioTransformers(
            arrayListOf(AudioTransformer("NoiseSuppression", ""))
        )
        NoiseSuppression.ENABLED
    }

internal fun Publisher.removeNoiseSuppression(): Result<NoiseSuppression> =
    runCatching {
        setAudioTransformers(arrayListOf())
        NoiseSuppression.DISABLED
    }

/**
 * Cycles through blur levels: NONE -> LOW -> HIGH -> NONE.
 *
 * Increments the current blur level, applies it to the publisher, and invokes
 * the callback with the new level.
 *
 * @param currentBlur The current blur level
 * @param callback Function invoked with the new blur level after applying it
 */
internal fun Publisher.cycleBlur(currentBlur: BlurLevel, callback: (BlurLevel) -> Unit) {
    var index = BlurLevel.entries.first { it == currentBlur }.ordinal
    (BlurLevel by ++index).also { blurLevel ->
        applyVideoBlur(blurLevel)
        callback(blurLevel)
    }
}

/**
 * Creates a flow that emits the publisher's audio level continuously.
 *
 * Registers an audio level listener and emits normalized audio levels (0.0 to 1.0).
 * The flow completes when the listener is removed.
 *
 * @return Flow emitting audio level values rounded to 2 decimal places
 */
internal fun Publisher.observeAudioLevel(): Flow<Float> = callbackFlow {
    val audioLevelListener = PublisherKit.AudioLevelListener { _, audioLevelRaw ->
        val audioLevel = audioLevelRaw.round2()
        trySend(audioLevel)
    }
    setAudioLevelListener(audioLevelListener)
    awaitClose {
        setAudioLevelListener(null)
    }
}

internal fun Publisher.applyVideoBitrate(config: VideoBitrateConfig?) {
    val sdkPreset = config.toSdkValue()
    videoBitratePreset = sdkPreset
    if (config?.preset == VideoBitratePreset.CUSTOM) {
        config.maxBitrate?.let { safeMaxBitrate ->
            maxVideoBitrate = safeMaxBitrate
        }
    }
}

internal fun VideoBitrateConfig?.toSdkValue(): PublisherKit.VideoBitratePreset =
    when (this?.preset) {
        VideoBitratePreset.DEFAULT -> PublisherKit.VideoBitratePreset.VideoBitratePresetDefault
        VideoBitratePreset.BW_SAVER -> PublisherKit.VideoBitratePreset.VideoBitratePresetBwSaver
        VideoBitratePreset.EXTRA_BW_SAVER -> PublisherKit.VideoBitratePreset.VideoBitratePresetExtraBwSaver
        VideoBitratePreset.CUSTOM -> PublisherKit.VideoBitratePreset.VideoBitratePresetCustom
        null -> PublisherKit.VideoBitratePreset.VideoBitratePresetDefault
    }

internal fun Publisher.applyDegradationPreference(preference: DegradationPreference?) {
    val sdkPref = when (preference) {
        DegradationPreference.NOT_SET ->
            PublisherKit.DegradationPreference.DegradationPreferenceNotSet
        DegradationPreference.MAINTAIN_FRAME_RATE_AND_RESOLUTION ->
            PublisherKit.DegradationPreference.DegradationPreferenceMaintainFrameRateAndResolution
        DegradationPreference.MAINTAIN_FRAME_RATE ->
            PublisherKit.DegradationPreference.DegradationPreferenceMaintainFrameRate
        DegradationPreference.MAINTAIN_RESOLUTION ->
            PublisherKit.DegradationPreference.DegradationPreferenceMaintainResolution
        DegradationPreference.BALANCED ->
            PublisherKit.DegradationPreference.DegradationPreferenceBalanced
        null -> PublisherKit.DegradationPreference.DegradationPreferenceNotSet
    }
    degradationPreference = sdkPref
}

internal fun Publisher.observeVideoStats(): Flow<VideoStats> = callbackFlow {
    setVideoStatsListener { _, stats ->
        if (stats.isNotEmpty()) {
            val s = stats[0]
            trySend(
                VideoStats(
                    duration = (s.timeStamp - s.startTime) / 1000,
                    videoPacketsSent = s.videoPacketsSent,
                    videoPacketsLost = s.videoPacketsLost,
                    videoBytesSent = s.videoBytesSent,
                    estimatedBandwidthInBps = s.transport.connectionEstimatedBandwidth,
                    videoLayerStats = s.videoLayers?.map { layer ->
                        VideoLayerStats(
                            height = layer.height,
                            width = layer.width,
                            codec = layer.codec,
                            encodedFrameRate = layer.encodedFrameRate,
                            qualityLimitationReason = layer.qualityLimitationReason,
                            scalabilityMode = layer.scalabilityMode,
                            bitrate = layer.bitrate,
                            totalBitrate = layer.totalBitrate,
                        )
                    }?.toImmutableList() ?: persistentListOf(),
                ),
            )
        }
    }
    awaitClose { setVideoStatsListener(null) }
}

internal fun Publisher.observeAudioStats(): Flow<AudioStats> = callbackFlow {
    setAudioStatsListener { _, stats ->
        if (stats.isNotEmpty()) {
            val s = stats[0]
            trySend(
                AudioStats(
                    duration = (s.timeStamp - s.startTime) / 1000,
                    audioPacketsSent = s.audioPacketsSent,
                    audioPacketsLost = s.audioPacketsLost,
                    audioBytesSent = s.audioBytesSent,
                    estimatedBandwidthInBps = s.transport.connectionEstimatedBandwidth,
                ),
            )
        }
    }
    awaitClose { setAudioStatsListener(null) }
}
