package com.vonage.android.chat

import android.content.Context
import com.vonage.android.kotlin.signal.ChatSignalPlugin

object ChatModule {

    fun provideChatFeature(): ChatFeature =
        DisabledChatFeature()

    @Suppress("UnusedParameter")
    fun provideChatSignalPlugin(context: Context): ChatSignalPlugin =
        DisabledChatSignalPlugin()
}
