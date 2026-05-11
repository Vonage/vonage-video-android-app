package com.vonage.android.meetingroom.internal.screen

import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.model.VideoEffect

@Stable
internal data class MeetingRoomActions(
    val onShare: (String) -> Unit = {},
    val onRetry: () -> Unit = {},
    val onEmojiSent: (String) -> Unit = {},
    val onToggleMic: () -> Unit = {},
    val onToggleCamera: () -> Unit = {},
    val onEndCall: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onCameraSwitch: () -> Unit = {},
    val onOpenVideoEffects: () -> Unit = {},
    val onApplyVideoEffect: (VideoEffect) -> Unit = {},
    val onAudioSwitch: () -> Unit = {},
    val onMessageSent: (String) -> Unit = {},
    val onListenUnread: (Boolean) -> Unit = {},
    val onToggleRecording: (Boolean) -> Unit = {},
    val onToggleCaptions: (Boolean) -> Unit = {},
    val onToggleScreenSharing: (Boolean) -> Unit = {},
    val onChangeLayout: (CallLayoutType) -> Unit = {},
    val onSettings: () -> Unit = {},
    val onTogglePinParticipant: (String) -> Unit = {},
    val onForceMuteParticipant: (String) -> Unit = {},
)
