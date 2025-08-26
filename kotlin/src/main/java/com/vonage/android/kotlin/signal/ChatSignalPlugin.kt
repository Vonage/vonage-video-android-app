package com.vonage.android.kotlin.signal

import com.opentok.android.Session
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class ChatSignalPlugin : SignalPlugin {

    private val chatMessages: MutableList<ChatMessage> = CopyOnWriteArrayList(mutableListOf<ChatMessage>())
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
        return ChatState(
            unreadCount = unreadCount,
            messages = chatMessages.toImmutableList(),
        )
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

    fun listenUnread(enable: Boolean): ChatState? {
        listenUnread = enable
        if (!enable) {
            chatMessagesUnreadCount = 0
            return ChatState(
                unreadCount = 0,
                messages = chatMessages.toImmutableList(),
            )
        }
        return null
    }

    companion object {
        const val PAYLOAD_PARTICIPANT_NAME_KEY = "participantName"
    }
}

@Serializable
internal data class ChatSignal(
    val participantName: String,
    val text: String,
)

data class ChatMessage(
    val id: UUID,
    val date: Date,
    val participantName: String,
    val text: String,
)
