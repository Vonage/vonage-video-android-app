package com.vonage.android.kotlin.ext

import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.vonage.android.kotlin.model.BackgroundBlur.KEY
import com.vonage.android.kotlin.model.BackgroundBlur.params
import com.vonage.android.kotlin.model.BlurLevel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

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

/**
 * Returns a Flow that emits sequential [size]d chunks of data from the source flow,
 * after transforming them with [transform].
 *
 * The list passed to [transform] is transient and must not be cached.
 */
fun <T, R> Flow<T>.chunked(size: Int, transform: suspend (List<T>) -> R): Flow<R> = flow {
    val cache = ArrayList<T>(size)
    collect {
        cache.add(it)
        if (cache.size == size) {
            emit(transform(cache))
            cache.clear()
        }
    }
}
