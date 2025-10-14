package com.vonage.android.chat

import android.app.NotificationManager
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.shared.ForegroundChecker

interface ChatFeature {
    fun getPlugin(
        foregroundChecker: ForegroundChecker,
        notifications: ChatNotifications,
    ): ChatSignalPlugin?

    fun createNotificationChannel(manager: NotificationManager)
}
