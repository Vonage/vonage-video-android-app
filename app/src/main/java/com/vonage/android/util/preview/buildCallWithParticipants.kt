package com.vonage.android.util.preview

import androidx.compose.runtime.Composable
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalState
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
    participantCount: Int,
    unreadCount: Int = 1,
    messagesCount: Int = 5,
): CallFacade {
    return object : CallFacade {
        // Participants state
        override val participantsStateFlow: StateFlow<ImmutableList<Participant>> =
            MutableStateFlow(buildParticipants(participantCount).toImmutableList())

        // Session related methods
        override fun connect(): Flow<SessionEvent> = flowOf()
        override fun pauseSession() {}
        override fun resumeSession() {}
        override fun endSession() {}

        // Publisher related methods
        override fun observeLocalAudioLevel(): Flow<Float> = flowOf()
        override fun toggleLocalVideo() {}
        override fun toggleLocalCamera() {}
        override fun toggleLocalAudio() {}

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

        override fun sendChatMessage(message: String) {}
        override fun listenUnreadChatMessages(enable: Boolean) {}

        // Reactions related methods
        override fun sendEmoji(emoji: String) {}
    }
}
