package com.vonage.android.chat

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.shared.DateProvider
import com.vonage.android.shared.ForegroundChecker

object ChatModule {

    fun provideChatFeature(): ChatFeature =
        EnabledChatFeature()

    fun provideChatSignalPlugin(context: Context): ChatSignalPlugin {
        val notifications = EnabledChatNotifications(
            context = context,
            notificationManager = NotificationManagerCompat.from(context),
        )
        return EnabledChatSignalPlugin(
            foregroundChecker = ForegroundChecker(),
            chatNotifications = notifications,
            dateProvider = DateProvider(),
        )
    }
}
