package com.vonage.android.kotlin.ext

import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.isActive

/**
 * Creates a flow that emits the subscriber's audio level continuously.
 *
 * Registers an audio level listener and emits normalized audio levels (0.0 to 1.0)
 * with conflation and sampling to prevent overwhelming downstream collectors.
 * Sampled every 100ms to reduce update frequency.
 *
 * @return Flow emitting audio level values rounded to 2 decimal places
 */
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
    .sample(DEBOUNCE_SUBSCRIBER_AUDIO_LEVEL_MILLIS)

/** Sampling interval for subscriber audio level updates in milliseconds */
const val DEBOUNCE_SUBSCRIBER_AUDIO_LEVEL_MILLIS = 100L

/**
 * Gets the display name from the subscriber's stream.
 *
 * @return The stream's name
 */
internal fun Subscriber.name(): String = stream.name

/**
 * Gets the display name from the subscriber kit's stream.
 *
 * @return The stream's name
 */
internal fun SubscriberKit.name(): String = stream.name

/**
 * Gets the unique stream ID from the subscriber kit.
 *
 * @return The stream ID
 */
internal fun SubscriberKit.id() = stream.streamId
