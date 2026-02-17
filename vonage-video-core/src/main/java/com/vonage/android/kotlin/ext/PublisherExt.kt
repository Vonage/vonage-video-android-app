package com.vonage.android.kotlin.ext

import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.vonage.android.kotlin.model.BackgroundBlur.KEY
import com.vonage.android.kotlin.model.BackgroundBlur.params
import com.vonage.android.kotlin.model.BlurLevel
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
