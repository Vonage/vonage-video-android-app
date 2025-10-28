package com.vonage.android.chat

import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.kotlin.signal.RawSignal
import com.vonage.android.shared.ChatMessage
import com.vonage.android.shared.DateProvider
import com.vonage.android.shared.ForegroundChecker
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class EnabledChatSignalPlugin(
    private val chatNotifications: ChatNotifications,
    private val foregroundChecker: ForegroundChecker,
    private val dateProvider: DateProvider,
) : ChatSignalPlugin {

    private val chatMessages: MutableList<ChatMessage> = CopyOnWriteArrayList()
    private var chatMessagesUnreadCount: Int = 0
    private var listenUnread: Boolean = true

    private val _output = MutableStateFlow(ChatState(unreadCount = 0, messages = persistentListOf()))
    override val output: StateFlow<SignalStateContent> = _output

    override fun canHandle(signalType: String): Boolean = signalType == SignalType.CHAT.signal

    override fun handleSignal(type: String, data: String, senderName: String, isYou: Boolean) {
        if (!canHandle(type)) return

        val chatSignal = try {
            Json.decodeFromString<ChatSignal>(data)
        } catch (_: SerializationException) {
            return
        }

        val message = ChatMessage(
            id = UUID.randomUUID(),
            date = dateProvider.current(),
            participantName = chatSignal.participantName,
            text = chatSignal.text,
        )
        chatMessages.add(0, message)
        val unreadCount = if (listenUnread) {
            ++chatMessagesUnreadCount
        } else 0

        if (listenUnread) {
            showNotificationWhenInBackground()
        }

        _output.value = ChatState(
            unreadCount = unreadCount,
            messages = chatMessages.toImmutableList(),
        )
    }

    override fun sendSignal(senderName: String, message: String): RawSignal {
        val signalData = Json.encodeToString(
            ChatSignal(
                participantName = senderName,
                text = message,
            )
        )
        return RawSignal(SignalType.CHAT.signal, signalData)
    }

    override fun listenUnread(enable: Boolean) {
        listenUnread = enable
        if (!enable) {
            chatMessagesUnreadCount = 0
            chatNotifications.cancelNotification()
            _output.value = ChatState(
                unreadCount = 0,
                messages = chatMessages.toImmutableList(),
            )
        }
    }

    private fun showNotificationWhenInBackground() {
        if (foregroundChecker.isInBackground()) {
            chatNotifications.showChatNotification(chatMessages.take(chatMessagesUnreadCount))
        }
    }
}

@Serializable
internal data class ChatSignal(
    val participantName: String,
    val text: String,
)
