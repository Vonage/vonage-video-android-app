package com.vonage.android.kotlin.ext

import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
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
    .debounce(DEBOUNCE_SUBSCRIBER_AUDIO_LEVEL_MILLIS)

const val DEBOUNCE_SUBSCRIBER_AUDIO_LEVEL_MILLIS = 100L

internal fun Subscriber.name(): String = stream.name
internal fun SubscriberKit.name(): String = stream.name
internal fun SubscriberKit.id() = stream.streamId
