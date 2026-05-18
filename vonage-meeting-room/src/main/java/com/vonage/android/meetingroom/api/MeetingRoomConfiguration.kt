package com.vonage.android.meetingroom.api

/**
 * Customises the meeting room UI controls.
 *
 * Pass an instance to [MeetingRoomBuilder.configuration]. All flags default to `true` so
 * callers only need to supply values they want to override.
 *
 * @param allowCameraControl       Show a camera on/off toggle in the bottom bar. Default `true`.
 * @param allowMicrophoneControl   Show a mic on/off toggle in the bottom bar. Default `true`.
 * @param allowShowParticipantList Show the participant list button. Default `true`.
 */
@ExperimentalMeetingRoomApi
data class MeetingRoomConfiguration(
    val allowCameraControl: Boolean = true,
    val allowMicrophoneControl: Boolean = true,
    val allowShowParticipantList: Boolean = true,
)
