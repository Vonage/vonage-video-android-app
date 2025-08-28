package com.vonage.android.kotlin.model

import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.signal.ChatMessage
import com.vonage.android.kotlin.signal.EmojiReaction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface CallFacade : SessionFacade, PublisherFacade, ChatFacade, EmojiFacade {
    val participantsStateFlow: StateFlow<ImmutableList<Participant>>
    val signalStateFlow: StateFlow<SignalState?>
}

interface PublisherFacade {
    fun observeLocalAudioLevel(): Flow<Float>
    fun toggleLocalVideo()
    fun toggleLocalCamera()
    fun toggleLocalAudio()
}

interface SessionFacade {
    fun connect(): Flow<SessionEvent>
    fun pauseSession()
    fun resumeSession()
    fun endSession()
}

interface ChatFacade {
    fun sendChatMessage(message: String)
    fun listenUnreadChatMessages(enable: Boolean)
}

interface EmojiFacade {
    fun sendEmoji(emoji: String)
}

enum class SignalType(val signal: String) {
    CHAT("chat"),
    REACTION("emoji");
}

data class SignalState(
    val signals: Map<String, SignalStateContent>
)

sealed interface SignalStateContent

data class ChatState(
    val unreadCount: Int = 0,
    val messages: ImmutableList<ChatMessage> = persistentListOf(),
) : SignalStateContent

data class EmojiState(
    val reactions: ImmutableList<EmojiReaction> = persistentListOf(),
) : SignalStateContent
