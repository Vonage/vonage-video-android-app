package com.vonage.android.chat

import android.app.NotificationManager
import com.vonage.android.kotlin.model.ChatMessage

interface ChatFeature {
    fun createNotificationChannel(manager: NotificationManager)
}

interface ChatNotifications {
    fun showChatNotification(messages: List<ChatMessage>)
    fun cancelNotification()
}
