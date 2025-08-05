package com.vonage.android.kotlin.ext

import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun Subscriber.observeAudioLevel(): Flow<Float> = callbackFlow {
    val audioLevelListener = SubscriberKit.AudioLevelListener { subscriber, audioLevel ->
        trySend(audioLevel.round4())
    }
    setAudioLevelListener(audioLevelListener)
    awaitClose {
        setAudioLevelListener(null)
    }
}
