package com.vonage.android.kotlin.model

import android.content.Context
import android.media.projection.MediaProjection
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.signal.EmojiReaction
import com.vonage.android.shared.ChatMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface CallFacade : SessionFacade, PublisherFacade, ChatFacade, EmojiFacade, ScreenShareFacade {

    fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>)

    val participantsStateFlow: StateFlow<ImmutableList<Participant>>
    val participantsCount: StateFlow<Int>
    val activeSpeaker: StateFlow<Participant?>
    val signalStateFlow: StateFlow<SignalState?>

    fun signalState(signalType: SignalType): StateFlow<SignalStateContent?>
    fun chatSignalState(): StateFlow<ChatState?>
    val emojiSignalState: StateFlow<EmojiState?>

    val captionsStateFlow: StateFlow<String?>
}

interface PublisherFacade {
    val publisher: StateFlow<PublisherState?>
    fun toggleLocalVideo()
    fun toggleLocalCamera()
    fun toggleLocalAudio()
}

interface SessionFacade {
    fun connect(context: Context): Flow<SessionEvent>
    fun enableCaptions(enable: Boolean)
    fun pauseSession()
    fun resumeSession()
    fun endSession()
}

interface ChatFacade {
    fun sendChatMessage(message: String)
    fun listenUnreadChatMessages(enable: Boolean)
}

fun interface EmojiFacade {
    fun sendEmoji(emoji: String)
}

interface ScreenShareFacade {
    fun startCapturingScreen(mediaProjection: MediaProjection)
    fun stopCapturingScreen()
}

enum class SignalType(val signal: String) {
    CHAT("chat"),
    REACTION("emoji");
}

data class SignalState(
    val signals: Map<String, SignalStateContent>,
)

typealias SignalFlows = MutableMap<SignalType, StateFlow<SignalStateContent?>>

sealed interface SignalStateContent

@Immutable
data class ChatState(
    val unreadCount: Int = 0,
    val messages: ImmutableList<ChatMessage> = persistentListOf(),
) : SignalStateContent

@Immutable
data class EmojiState(
    val reactions: ImmutableList<EmojiReaction> = persistentListOf(),
) : SignalStateContent
