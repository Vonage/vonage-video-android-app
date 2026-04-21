package com.vonage.android.meetingroom

/**
 * Immutable configuration for the meeting room screen.
 *
 * Passed at construction time to control which core interactions the
 * local user is permitted to perform. Feature-level flags (recording,
 * captions, screensharing, etc.) are handled by the corresponding
 * [MeetingRoomUiPlugin] implementations.
 */
data class MeetingRoomConfig(
    val allowMicrophoneControl: Boolean = true,
    val allowCameraControl: Boolean = true,
    val allowShowParticipantList: Boolean = true,
    val defaultLayout: CallLayoutType = CallLayoutType.GRID,
)
