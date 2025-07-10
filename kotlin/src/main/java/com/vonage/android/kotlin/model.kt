package com.vonage.android.kotlin

import androidx.compose.runtime.Immutable
import com.opentok.android.Publisher

@Immutable
sealed interface PublisherState {
    data object Offline : PublisherState
    data class Online(
        val participant: Publisher,
        val publishVideo: Boolean = true,
        val publishAudio: Boolean = true,
    ) : PublisherState
}
