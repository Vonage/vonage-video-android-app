package com.vonage.android.kotlin.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Stable
interface CallFacade : SessionFacade, PublisherFacade {
    val participantsStateFlow: StateFlow<ImmutableList<Participant>>
    val chatStateFlow: StateFlow<ImmutableList<ChatMessage>>
    fun sendChatMessage(message: String)

    //    fun <T> signalStateFlow(type: SignalType): Flow<T>
//    fun signalStateFlow(): Flow<ImmutableList<ChatMessage>>
}

enum class SignalType {
    REACTION,
    CHAT,
}

@Serializable
data class ChatSignal(
    val participantName: String,
    val text: String,
)

data class ChatMessage(
    val id: UUID,
    val date: Date,
    val participantName: String,
    val text: String,
)

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
