package com.vonage.android.meetingroom.internal.util

import android.content.Context
import android.media.projection.MediaProjection
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.CaptionLine
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@Suppress("EmptyFunctionBlock")
internal val noOpCall = object : CallFacade {
    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {}
    override val participantsStateFlow: StateFlow<ImmutableList<ParticipantState>> = MutableStateFlow(persistentListOf())
    override val participantsCount: StateFlow<Int> = MutableStateFlow(1)
    override val activeSpeaker: StateFlow<Participant?> = MutableStateFlow(null)
    override val pinnedParticipantIds: StateFlow<Set<String>> = MutableStateFlow(emptySet())
    override fun togglePinParticipant(participantId: String) {}
    override fun forceMuteParticipant(participantId: String) {}
    override val signalStateFlow: StateFlow<SignalState?> = MutableStateFlow(null)
    override val captionsStateFlow: StateFlow<ImmutableList<CaptionLine>> = MutableStateFlow(persistentListOf())
    override val archivingStateFlow: StateFlow<ArchivingState> = MutableStateFlow(ArchivingState.Idle)
    override fun signalState(signalType: SignalType): StateFlow<SignalStateContent?> = MutableStateFlow(null)
    override val chatSignalState: StateFlow<ChatState?> = MutableStateFlow(null)
    override val emojiSignalState: StateFlow<EmojiState?> = MutableStateFlow(null)
    override fun connect(context: Context): Flow<SessionEvent> = flowOf()
    override fun enableCaptions() {}
    override fun disableCaptions() {}
    override fun pauseSession() {}
    override fun resumeSession() {}
    override fun endSession() {}
    override val publisher: StateFlow<PublisherState?> = MutableStateFlow(null)
    override fun toggleLocalVideo() {}
    override fun toggleLocalCamera() {}
    override fun toggleLocalAudio() {}
    override fun applyLocalVideoEffect(effect: VideoEffect) {}
    override fun setVideoBitrate(config: VideoBitrateConfig) {}
    override fun setDegradationPreference(preference: DegradationPreference) {}
    override fun sendChatMessage(message: String) {}
    override fun listenUnreadChatMessages(enable: Boolean) {}
    override fun sendEmoji(emoji: String) {}
    override fun startCapturingScreen(mediaProjection: MediaProjection) {}
    override fun stopCapturingScreen() {}
}
