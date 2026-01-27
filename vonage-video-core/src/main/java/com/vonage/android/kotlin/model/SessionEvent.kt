package com.vonage.android.kotlin.model

import com.opentok.android.OpentokError

/**
 * Sealed interface representing all possible session events during a video call.
 */
sealed interface SessionEvent {
    /** Session successfully connected */
    data object Connected : SessionEvent
    
    /** Session disconnected */
    data object Disconnected : SessionEvent
    
    /**
     * A new stream was received from a remote participant.
     *
     * @property streamId The unique identifier of the received stream
     */
    data class StreamReceived(val streamId: String) : SessionEvent
    
    /**
     * A remote participant's stream was dropped.
     *
     * @property streamId The unique identifier of the dropped stream
     */
    data class StreamDropped(val streamId: String) : SessionEvent
    
    /**
     * An error occurred during the session.
     *
     * @property error The OpenTok error details
     */
    data class Error(val error: OpentokError) : SessionEvent
}
