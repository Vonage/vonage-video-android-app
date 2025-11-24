package com.vonage.android.kotlin.ext

import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.vonage.android.kotlin.model.BackgroundBlur.KEY
import com.vonage.android.kotlin.model.BackgroundBlur.params
import com.vonage.android.kotlin.model.BlurLevel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
