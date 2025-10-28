package com.vonage.android.chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.vonage.android.chat.EnabledChatNotifications.Companion.NOTIFICATION_CHANNEL_ID
import com.vonage.android.chat.EnabledChatNotifications.Companion.NOTIFICATION_CHANNEL_NAME

class EnabledChatFeature : ChatFeature {

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
