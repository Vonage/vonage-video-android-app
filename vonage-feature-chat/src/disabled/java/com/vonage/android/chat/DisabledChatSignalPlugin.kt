package com.vonage.android.chat

import com.opentok.android.Session
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.signal.ChatSignalPlugin

class DisabledChatSignalPlugin : ChatSignalPlugin {

    override fun listenUnread(enable: Boolean): ChatState? = null

    override fun canHandle(signalType: String): Boolean = false

    override fun handleSignal(
        type: String,
        data: String,
        senderName: String,
        isYou: Boolean,
        callback: (SignalStateContent) -> Unit
    ): SignalStateContent? = null

    override fun sendSignal(session: Session, message: String, payload: Map<String, String>) {

    }
}
