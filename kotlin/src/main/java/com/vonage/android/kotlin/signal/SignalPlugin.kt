package com.vonage.android.kotlin.signal

import com.opentok.android.Session
import com.vonage.android.kotlin.model.SignalStateContent

interface SignalPlugin {
    fun canHandle(signalType: String): Boolean
    fun handleSignal(
        type: String,
        data: String,
        senderName: String,
        isYou: Boolean,
        callback: (SignalStateContent) -> Unit = {},
    ): SignalStateContent?
    fun sendSignal(session: Session, message: String, payload: Map<String, String> = emptyMap())
}
