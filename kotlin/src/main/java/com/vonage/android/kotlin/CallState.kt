package com.vonage.android.kotlin

import androidx.compose.runtime.Immutable
import com.opentok.android.Subscriber
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class CallState(
    val participants: ImmutableList<ParticipantState>,
    val publisher: PublisherState,
    val subscribers: SubscriberState,
    val activeSubscriber: Subscriber,
)

data class ParticipantState(
    val name: String,
)
