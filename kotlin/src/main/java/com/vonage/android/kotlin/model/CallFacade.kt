package com.vonage.android.kotlin.model

import android.content.Context
import android.media.projection.MediaProjection
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.signal.EmojiReaction
import com.vonage.android.shared.ChatMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
interface CallFacade : SessionFacade, PublisherFacade, ChatFacade, EmojiFacade, ScreenShareFacade {

    fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>)

    val participantsStateFlow: Flow<ImmutableList<Participant>>
    val participantsCount: StateFlow<Int>
    val activeSpeaker: StateFlow<Participant?>
    val signalStateFlow: StateFlow<SignalState?>
    
    fun signalState(signalType: SignalType): StateFlow<SignalStateContent?>
    
    fun chatSignalState(): StateFlow<ChatState?> = signalState(SignalType.CHAT)
        .map { it as? ChatState }
        .stateIn(scope = CoroutineScope(Dispatchers.Default), started = SharingStarted.Lazily, initialValue = null)
    
    fun emojiSignalState(): StateFlow<EmojiState?> = signalState(SignalType.REACTION)
        .map { it as? EmojiState }
        .stateIn(scope = CoroutineScope(Dispatchers.Default), started = SharingStarted.Lazily, initialValue = null)
    
    val captionsStateFlow: StateFlow<String?>
}

interface PublisherFacade {
    val localAudioLevel: StateFlow<Float>
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

data class ChatState(
    val unreadCount: Int = 0,
    val messages: ImmutableList<ChatMessage> = persistentListOf(),
) : SignalStateContent

data class EmojiState(
    val reactions: ImmutableList<EmojiReaction> = persistentListOf(),
) : SignalStateContent
