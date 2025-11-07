package com.vonage.android.kotlin.ext

import android.util.Log
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.isActive

@OptIn(FlowPreview::class)
internal fun Subscriber.observeAudioLevel(): Flow<Float> = callbackFlow {
    val audioLevelListener = SubscriberKit.AudioLevelListener { _, audioLevel ->
        if (isActive) {
            trySend(audioLevel.round2())
        } else {
            Log.e("XXX", "death")
        }
    }
    setAudioLevelListener(audioLevelListener)
    awaitClose {
        setAudioLevelListener(null)
    }
}
    .conflate() // Drop intermediate values if consumer is slow
    .sample(100) // Sample every 100ms to reduce frequency (adjust as needed)
    .flowOn(Dispatchers.Default) // Move to background thread

internal fun Subscriber.name(): String = stream.name
internal fun SubscriberKit.name(): String = stream.name
internal fun SubscriberKit.id() = stream.streamId
