package com.vonage.android.chat

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.vonage.android.shared.ChatMessage

class EnabledChatNotifications(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
): ChatNotifications {

    @SuppressLint("MissingPermission")
    override fun showChatNotification(messages: List<ChatMessage>) {
        val summary = NotificationCompat.MessagingStyle(Person.Builder().setName("Vonage").build())
        messages.forEach { message ->
            val notificationMessage = NotificationCompat.MessagingStyle.Message(
                message.text,
                message.date.time,
                Person.Builder().setName(message.participantName).build(),
            )
            summary.addMessage(notificationMessage)
        }
        val chatNotification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_action_chat)
            .setStyle(summary)
            .build()
        notificationManager.notify(NOTIFICATION_ID, chatNotification)
    }

    override fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object Companion {
        const val NOTIFICATION_CHANNEL_ID = "VeraNotificationManagerChat"
        const val NOTIFICATION_CHANNEL_NAME = "Vonage Chat"
        const val NOTIFICATION_ID = 123
    }
}
