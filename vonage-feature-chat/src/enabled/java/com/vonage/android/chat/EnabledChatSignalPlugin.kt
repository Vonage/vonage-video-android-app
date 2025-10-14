package com.vonage.android.chat

import com.opentok.android.Session
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.shared.ChatMessage
import com.vonage.android.shared.ForegroundChecker
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class EnabledChatSignalPlugin(
    private val chatNotifications: ChatNotifications,
    private val foregroundChecker: ForegroundChecker,
) : ChatSignalPlugin {

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

        showNotificationWhenInBackground()

        return ChatState(
            unreadCount = unreadCount,
            messages = chatMessages.toImmutableList(),
        )
    }

    override fun sendSignal(session: Session, senderName: String, message: String) {
        val signal = Json.encodeToString(
            ChatSignal(
                participantName = senderName,
                text = message,
            )
        )
        session.sendSignal(SignalType.CHAT.signal, signal)
    }

    override fun listenUnread(enable: Boolean): ChatState? {
        listenUnread = enable
        if (!enable) {
            chatMessagesUnreadCount = 0
            chatNotifications.cancelNotification()
            return ChatState(
                unreadCount = 0,
                messages = chatMessages.toImmutableList(),
            )
        }
        return null
    }

    private fun showNotificationWhenInBackground() {
        val isInForeground = foregroundChecker.isInForeground()
        if (isInForeground.not()) {
            chatNotifications.showChatNotification(chatMessages.take(chatMessagesUnreadCount))
        }
    }
}

@Serializable
internal data class ChatSignal(
    val participantName: String,
    val text: String,
)
