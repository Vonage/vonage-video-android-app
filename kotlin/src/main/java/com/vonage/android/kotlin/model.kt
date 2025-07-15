package com.vonage.android.kotlin

import androidx.compose.runtime.Immutable
import com.opentok.android.Publisher
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface SubscriberState {
    data object Offline : SubscriberState
    data class Online(val participants: List<Participant>) : SubscriberState
    data object Disconnected : SubscriberState
}

@Immutable
sealed interface PublisherState {
    data object Offline : PublisherState
    data class Online(
        val participant: Publisher,
        val publishVideo: Boolean = true,
        val publishAudio: Boolean = true,
    ) : PublisherState
}
