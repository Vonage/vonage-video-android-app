package com.vonage.android.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.opentok.android.Session
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.shared.ChatMessage
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class EnabledChatSignalPlugin(
    private val context: Context,
) : ChatSignalPlugin {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val chatMessages: MutableList<ChatMessage> = CopyOnWriteArrayList()
    private var chatMessagesUnreadCount: Int = 0
    private var listenUnread: Boolean = true

    override fun canHandle(signalType: String): Boolean = signalType == SignalType.CHAT.signal

    override fun handleSignal(
        type: String,
        data: String,
        senderName: String,
        isYou: Boolean,
        callback: (SignalStateContent) -> Unit,
    ): SignalStateContent? {
        if (!canHandle(type)) return null

        val chatSignal = try {
            Json.decodeFromString<ChatSignal>(data)
        } catch (_: SerializationException) {
            return null
        }

        val message = ChatMessage(
            id = UUID.randomUUID(),
            date = Date(),
            participantName = chatSignal.participantName,
            text = chatSignal.text,
        )
        chatMessages.add(0, message)
        val unreadCount = if (listenUnread) {
            ++chatMessagesUnreadCount
        } else 0

        handleNotification()

        return ChatState(
            unreadCount = unreadCount,
            messages = chatMessages.toImmutableList(),
        )
    }

    private fun handleNotification() {
        val isInForeground = ActivityManager.RunningAppProcessInfo()
            .let { appProcessInfo ->
                ActivityManager.getMyMemoryState(appProcessInfo)
                appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        if (!isInForeground) {
            showChatNotification(chatMessages.take(chatMessagesUnreadCount))
        }
    }

    @SuppressLint("MissingPermission")
    private fun showChatNotification(messages: List<ChatMessage>) {
        if (checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val summary = NotificationCompat.MessagingStyle("Vonage")
        messages.forEach { message ->
            val notificationMessage = NotificationCompat.MessagingStyle.Message(
                message.text,
                message.date.time,
                message.participantName
            )
            summary.addMessage(notificationMessage)
        }
        val chatNotification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_action_chat)
            .setStyle(summary)
            .build()
        notificationManager.notify(NOTIFICATION_ID, chatNotification)
    }

    override fun sendSignal(session: Session, message: String, payload: Map<String, String>) {
        val signal = Json.encodeToString(
            ChatSignal(
                participantName = payload[PAYLOAD_PARTICIPANT_NAME_KEY].orEmpty(),
                text = message,
            )
        )
        session.sendSignal(SignalType.CHAT.signal, signal)
    }

    override fun listenUnread(enable: Boolean): ChatState? {
        listenUnread = enable
        if (!enable) {
            chatMessagesUnreadCount = 0
            notificationManager.cancel(NOTIFICATION_ID)
            return ChatState(
                unreadCount = 0,
                messages = chatMessages.toImmutableList(),
            )
        }
        return null
    }

    companion object Companion {
        const val PAYLOAD_PARTICIPANT_NAME_KEY = "participantName"
        const val NOTIFICATION_CHANNEL_ID = "VeraNotificationManagerChat"
        const val NOTIFICATION_CHANNEL_NAME = "Vonage Chat"
        const val NOTIFICATION_ID = 123
    }
}

@Serializable
internal data class ChatSignal(
    val participantName: String,
    val text: String,
)
