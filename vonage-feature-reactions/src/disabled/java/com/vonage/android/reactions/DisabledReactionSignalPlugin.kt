package com.vonage.android.reactions

import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.signal.RawSignal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("EmptyFunctionBlock")
class DisabledReactionSignalPlugin : ReactionSignalPlugin {

    override val output: StateFlow<SignalStateContent?> = MutableStateFlow(null)

    override fun canHandle(signalType: String): Boolean = false

    override fun handleSignal(type: String, data: String, senderName: String, isYou: Boolean) {}

    override fun sendSignal(senderName: String, message: String): RawSignal = RawSignal("", "")

}
