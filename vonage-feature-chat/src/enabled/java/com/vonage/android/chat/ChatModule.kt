package com.vonage.android.chat

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.vonage.android.kotlin.signal.ChatSignalPlugin

object ChatModule {

    fun provideChatFeature(): ChatFeature =
        EnabledChatFeature()

    fun provideChatSignalPlugin(context: Context): ChatSignalPlugin {
        val applicationContext = context.applicationContext
        val notifications = EnabledChatNotifications(
            context = applicationContext,
            notificationManager = NotificationManagerCompat.from(applicationContext),
        )
        return EnabledChatSignalPlugin(
            chatNotifications = notifications,
        )
    }
}
