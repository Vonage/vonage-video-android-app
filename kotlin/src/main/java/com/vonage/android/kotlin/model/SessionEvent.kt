package com.vonage.android.kotlin.model

import com.opentok.android.OpentokError

sealed interface SessionEvent {
    data object Connected : SessionEvent
    data object Disconnected : SessionEvent
    data class StreamReceived(val streamId: String) : SessionEvent
    data class StreamDropped(val streamId: String) : SessionEvent
    data class Error(val error: OpentokError) : SessionEvent
}
