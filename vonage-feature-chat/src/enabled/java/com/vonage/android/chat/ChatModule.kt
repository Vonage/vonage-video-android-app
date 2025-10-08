package com.vonage.android.chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.vonage.android.kotlin.signal.ChatSignalPlugin

object ChatModule {

    fun getChatFeature(): ChatFeature {
        return EnabledChatFeature()
    }

    fun getPlugin(context: Context): ChatSignalPlugin {
        return EnabledChatSignalPlugin(context)
    }

    fun createNotificationChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chatChannel = NotificationChannel(
                EnabledChatSignalPlugin.Companion.NOTIFICATION_CHANNEL_ID,
                EnabledChatSignalPlugin.Companion.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            manager.createNotificationChannel(chatChannel)
        }
    }
}
