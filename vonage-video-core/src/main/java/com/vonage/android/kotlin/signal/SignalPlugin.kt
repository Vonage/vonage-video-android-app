package com.vonage.android.kotlin.signal

import com.vonage.android.kotlin.model.SignalStateContent
import kotlinx.coroutines.flow.StateFlow

/**
 * Plugin interface for handling custom signal types in a video call.
 *
 * Plugins process incoming signals, maintain state, and format outgoing signals.
 * Multiple plugins can be registered to handle different signal types (chat, reactions, etc.).
 */
interface SignalPlugin {
    /** StateFlow emitting the current signal state managed by this plugin */
    val output: StateFlow<SignalStateContent?>
    
    /**
     * Determines if this plugin can handle a specific signal type.
     *
     * @param signalType The signal type identifier (e.g., "chat", "emoji")
     * @return True if this plugin handles the signal type
     */
    fun canHandle(signalType: String): Boolean
    
    /**
     * Processes an incoming signal from the session.
     *
     * @param type Signal type identifier
     * @param data Signal payload data
     * @param senderName Display name of the sender
     * @param isYou True if the signal was sent by the local user
     */
    fun handleSignal(type: String, data: String, senderName: String, isYou: Boolean)
    
    /**
     * Formats a message into a raw signal for sending.
     *
     * @param senderName Display name of the sender
     * @param message The message content to send
     * @return RawSignal containing type and formatted data
     */
    fun sendSignal(senderName: String, message: String): RawSignal
}

/**
 * Raw signal data ready to be sent through the session.
 *
 * @property type Signal type identifier
 * @property data Serialized signal payload
 */
data class RawSignal(
    val type: String,
    val data: String,
)

/**
 * Extended signal plugin interface for chat functionality.
 *
 * Adds unread message tracking capability to the base SignalPlugin.
 */
interface ChatSignalPlugin : SignalPlugin {
    /**
     * Enables or disables tracking of unread messages.
     *
     * @param enable True to start tracking, false to stop
     */
    fun listenUnread(enable: Boolean)
}
