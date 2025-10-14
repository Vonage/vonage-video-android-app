package com.vonage.android.chat

import android.app.NotificationManager
import android.content.Context
import com.vonage.android.kotlin.signal.ChatSignalPlugin

class DisabledChatFeature : ChatFeature {

    override fun getPlugin(context: Context): ChatSignalPlugin? = null

    override fun createNotificationChannel(manager: NotificationManager) {

    }
}
