package com.vonage.android.chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.vonage.android.chat.ChatNotifications.Companion.NOTIFICATION_CHANNEL_ID
import com.vonage.android.chat.ChatNotifications.Companion.NOTIFICATION_CHANNEL_NAME
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.shared.ForegroundChecker

class EnabledChatFeature : ChatFeature {

    override fun getPlugin(
        foregroundChecker: ForegroundChecker,
        notifications: ChatNotifications,
    ): ChatSignalPlugin =
        EnabledChatSignalPlugin(
            chatNotifications = notifications,
            foregroundChecker = foregroundChecker,
        )

    override fun createNotificationChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chatChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            manager.createNotificationChannel(chatChannel)
        }
    }
}
