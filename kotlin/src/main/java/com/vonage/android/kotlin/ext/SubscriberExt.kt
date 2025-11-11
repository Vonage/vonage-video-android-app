package com.vonage.android.kotlin.ext

import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive

@OptIn(FlowPreview::class)
internal fun Subscriber.observeAudioLevel(): Flow<Float> = callbackFlow {
    val audioLevelListener = SubscriberKit.AudioLevelListener { _, audioLevel ->
        if (isActive) {
            trySend(audioLevel.round2())
        }
    }
    setAudioLevelListener(audioLevelListener)
    awaitClose {
        setAudioLevelListener(null)
    }
}
    .conflate()
    .debounce(100)
//    .flowOn(Dispatchers.Default)

internal fun Subscriber.name(): String = stream.name
internal fun SubscriberKit.name(): String = stream.name
internal fun SubscriberKit.id() = stream.streamId
