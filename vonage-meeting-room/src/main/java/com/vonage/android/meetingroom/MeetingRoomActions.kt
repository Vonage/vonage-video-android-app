package com.vonage.android.meetingroom

import androidx.compose.runtime.Stable

/**
 * All user-initiated actions available in the meeting room.
 *
 * Every field has a no-op default so callers only override what they need,
 * and the Composable previews can be created without wiring a real ViewModel.
 */
@Stable
data class MeetingRoomActions(
    val onShare: (String) -> Unit = {},
    val onRetry: () -> Unit = {},
    val onEmojiSent: (String) -> Unit = {},
    val onToggleMic: () -> Unit = {},
    val onToggleCamera: () -> Unit = {},
    val onEndCall: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onCameraSwitch: () -> Unit = {},
    val onCycleCameraBlur: () -> Unit = {},
    val onAudioSwitch: () -> Unit = {},
    val onMessageSent: (String) -> Unit = {},
    val onListenUnread: (Boolean) -> Unit = {},
    val onToggleRecording: (Boolean) -> Unit = {},
    val onToggleCaptions: (Boolean) -> Unit = {},
    val onToggleScreenSharing: (Boolean) -> Unit = {},
    val onShowFeedbackScreen: () -> Unit = {},
    val onChangeLayout: (CallLayoutType) -> Unit = {},
    val onSettings: () -> Unit = {},
    val onTogglePinParticipant: (String) -> Unit = {},
    val onForceMuteParticipant: (String) -> Unit = {},
)
