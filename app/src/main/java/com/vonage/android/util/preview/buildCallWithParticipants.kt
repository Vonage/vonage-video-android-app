package com.vonage.android.util.preview

import androidx.compose.runtime.Composable
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ChatMessage
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.screen.room.components.buildChatMessages
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@Suppress("EmptyFunctionBlock")
@Composable
fun buildCallWithParticipants(count: Int): CallFacade {
    return object : CallFacade {
        // Participants state
        override val participantsStateFlow: StateFlow<ImmutableList<Participant>> =
            MutableStateFlow(buildParticipants(count).toImmutableList())

        // Session related methods
        override fun connect(): Flow<SessionEvent> = flowOf()
        override fun pauseSession() {}
        override fun resumeSession() {}
        override fun endSession() {}

        // Publisher related methods
        override fun observePublisherAudio(): Flow<Float> = flowOf()
        override fun togglePublisherVideo() {}
        override fun togglePublisherCamera() {}
        override fun togglePublisherAudio() {}

        // Chat related methods
        override val chatStateFlow: StateFlow<ImmutableList<ChatMessage>> =
            MutableStateFlow(buildChatMessages(2).toImmutableList())
        override fun sendChatMessage(message: String) {}
    }
}
