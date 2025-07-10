package com.vonage.android.kotlin

import androidx.compose.runtime.Immutable

@Immutable
data class CallState(
    val publisher: PublisherState,
)
