package com.vonage.android.util

import android.content.Context
import android.media.projection.MediaProjection
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@Suppress("EmptyFunctionBlock")
val noOpCall = object : CallFacade {
    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {}

    override val participantsStateFlow: StateFlow<ImmutableList<ParticipantState>> = MutableStateFlow(persistentListOf())
    override val participantsCount: StateFlow<Int> = MutableStateFlow(1)
    override val activeSpeaker: StateFlow<Participant?> = MutableStateFlow(null)
    override val signalStateFlow: StateFlow<SignalState?> = MutableStateFlow(null)
    override val captionsStateFlow: StateFlow<String?> = MutableStateFlow(null)
    override val archivingStateFlow: StateFlow<ArchivingState> = MutableStateFlow(ArchivingState.Idle)

    override fun signalState(signalType: SignalType): StateFlow<SignalStateContent?> = MutableStateFlow(null)
    override val chatSignalState: StateFlow<ChatState?> = MutableStateFlow(null)
    override val emojiSignalState: StateFlow<EmojiState?> = MutableStateFlow(null)

    override fun connect(context: Context): Flow<SessionEvent> = flowOf()
    override fun enableCaptions() { /* empty on purpose */ }
    override fun disableCaptions() { /* empty on purpose */ }
    override fun pauseSession() { /* empty on purpose */ }
    override fun resumeSession() { /* empty on purpose */ }
    override fun endSession() { /* empty on purpose */ }

    override val publisher: StateFlow<PublisherState?> = MutableStateFlow(null)

    override fun toggleLocalVideo() { /* empty on purpose */ }
    override fun toggleLocalCamera() { /* empty on purpose */ }
    override fun toggleLocalAudio() { /* empty on purpose */ }
    override fun cycleLocalCameraBlur() { /* empty on purpose */ }

    override fun sendChatMessage(message: String) { /* empty on purpose */ }
    override fun listenUnreadChatMessages(enable: Boolean) { /* empty on purpose */ }
    override fun sendEmoji(emoji: String) { /* empty on purpose */ }
    override fun startCapturingScreen(mediaProjection: MediaProjection) { /* empty on purpose */ }
    override fun stopCapturingScreen() { /* empty on purpose */ }
}
