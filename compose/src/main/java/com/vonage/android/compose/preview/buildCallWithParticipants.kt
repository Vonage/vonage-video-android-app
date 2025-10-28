package com.vonage.android.compose.preview

import android.content.Context
import android.media.projection.MediaProjection
import androidx.compose.runtime.Composable
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@Suppress("EmptyFunctionBlock")
@Composable
fun buildCallWithParticipants(
    participantCount: Int = 3,
    unreadCount: Int = 1,
    messagesCount: Int = 5,
): CallFacade = object : CallFacade {
    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) { }

    // Participants state
    override val participantsStateFlow: StateFlow<ImmutableList<Participant>> =
        MutableStateFlow(buildParticipants(participantCount).toImmutableList())
    override val participantsCount: StateFlow<Int> = MutableStateFlow(participantCount)
    override val activeSpeaker: StateFlow<Participant?> = MutableStateFlow(null)

    // Session related methods
    override fun connect(context: Context): Flow<SessionEvent> = flowOf()
    override fun enableCaptions(enable: Boolean) { /* empty on purpose */ }
    override fun pauseSession() { /* empty on purpose */ }
    override fun resumeSession() { /* empty on purpose */ }
    override fun endSession() { /* empty on purpose */ }

    // Publisher related methods
    override val localAudioLevel: StateFlow<Float> = MutableStateFlow(0F)
    override fun toggleLocalVideo() { /* empty on purpose */ }
    override fun toggleLocalCamera() { /* empty on purpose */ }
    override fun toggleLocalAudio() { /* empty on purpose */ }

    // Chat related methods
    override val signalStateFlow: StateFlow<SignalState> = MutableStateFlow(
        SignalState(
            signals = mapOf(
                SignalType.CHAT.signal to ChatState(
                    unreadCount = unreadCount,
                    messages = buildChatMessages(messagesCount).toImmutableList(),
                ),
            )
        )
    )
    override val captionsStateFlow: StateFlow<String?> = MutableStateFlow(null)

    override fun signalState(signalType: SignalType): StateFlow<SignalStateContent?> {
        val signalState = signalStateFlow.value.signals[signalType.signal]
        return MutableStateFlow(signalState)
    }

    override fun chatSignalState(): StateFlow<ChatState?> {
        val chatState = signalStateFlow.value.signals[SignalType.CHAT.signal] as? ChatState
        return MutableStateFlow(chatState)
    }

    override fun emojiSignalState(): StateFlow<EmojiState?> {
        val emojiState = signalStateFlow.value.signals[SignalType.REACTION.signal] as? EmojiState
        return MutableStateFlow(emojiState)
    }

    override fun sendChatMessage(message: String) { /* empty on purpose */ }
    override fun listenUnreadChatMessages(enable: Boolean) { /* empty on purpose */ }

    // Reactions related methods
    override fun sendEmoji(emoji: String) { /* empty on purpose */ }

    // Screen sharing related methods
    override fun startCapturingScreen(mediaProjection: MediaProjection) { /* empty on purpose */ }
    override fun stopCapturingScreen() { /* empty on purpose */ }
}
