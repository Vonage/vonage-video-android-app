package com.vonage.android.kotlin.signal

import com.vonage.android.kotlin.model.SignalStateContent
import kotlinx.coroutines.flow.StateFlow

interface SignalPlugin {
    val output: StateFlow<SignalStateContent?>
    fun canHandle(signalType: String): Boolean
    fun handleSignal(type: String, data: String, senderName: String, isYou: Boolean)
    fun sendSignal(senderName: String, message: String): RawSignal
}

data class RawSignal(
    val type: String,
    val data: String,
)

interface ChatSignalPlugin : SignalPlugin {
    fun listenUnread(enable: Boolean)
}
