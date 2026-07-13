package com.vonage.android.meetingroom.api

/**
 * Pre-wired SDK action callbacks exposed to a custom bottom bar composable.
 *
 * Provided as the second parameter of the composable slot registered via
 * [MeetingRoomBuilder.bottomBar]. Each lambda is already connected to the SDK internals, so the
 * host app does not need to manage any session state directly.
 *
 * @param onToggleMic           Toggle the local microphone on/off.
 * @param onToggleCamera        Toggle the local camera on/off.
 * @param onEndCall             End the active call and trigger [MeetingRoomSDKAction.CallDidEnd].
 * @param onToggleRecording     Start (`true`) or stop (`false`) session recording.
 * @param onToggleCaptions      Enable (`true`) or disable (`false`) live captions.
 * @param onToggleScreenSharing Start (`true`) or stop (`false`) screen sharing.
 */
@ExperimentalMeetingRoomApi
class MeetingRoomCustomActions(
    val onToggleMic: () -> Unit,
    val onToggleCamera: () -> Unit,
    val onEndCall: () -> Unit,
    val onToggleRecording: (Boolean) -> Unit,
    val onToggleCaptions: (Boolean) -> Unit,
    val onToggleScreenSharing: (Boolean) -> Unit,
)
