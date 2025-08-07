package com.vonage.android.kotlin.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Stable
interface CallFacade : SessionFacade, PublisherFacade, ChatFacade {
    val participantsStateFlow: StateFlow<ImmutableList<Participant>>
}

interface PublisherFacade {
    fun observePublisherAudio(): Flow<Float>
    fun togglePublisherVideo()
    fun togglePublisherCamera()
    fun togglePublisherAudio()
}

interface SessionFacade {
    fun connect(): Flow<SessionEvent>
    fun pauseSession()
    fun resumeSession()
    fun endSession()
}

interface ChatFacade {
    val chatStateFlow: StateFlow<ChatState>
    fun sendChatMessage(message: String)
    fun listenUnreadChatMessages(enable: Boolean)
}

enum class SignalType(val signal: String) {
    CHAT("chat"),
    REACTION("reaction");
}

@Serializable
internal data class ChatSignal(
    val participantName: String,
    val text: String,
)

data class ChatState(
    val unreadCount: Int = 0,
    val messages: ImmutableList<ChatMessage> = persistentListOf(),
)

data class ChatMessage(
    val id: UUID,
    val date: Date,
    val participantName: String,
    val text: String,
)
