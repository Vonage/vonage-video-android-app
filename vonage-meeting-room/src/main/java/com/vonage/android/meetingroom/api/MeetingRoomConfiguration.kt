package com.vonage.android.meetingroom.api

/**
 * Customises the meeting room UI controls.
 *
 * Pass an instance to [MeetingRoomBuilder.configuration]. All flags default to the current
 * behavior so callers only need to supply values they want to override.
 *
 * @param allowCameraControl       Show a camera on/off toggle in the bottom bar. Default `true`.
 * @param allowMicrophoneControl   Show a mic on/off toggle in the bottom bar. Default `true`.
 * @param allowShowParticipantList Show the participant list button. Default `true`.
 * @param allowDeviceSelection     Allow opening the audio output selector. Default `true`.
 * @param allowPictureInPicture    Enter picture-in-picture when the user leaves the app during a
 *                                 call. Default `true`.
 * @param defaultLayoutMode        Layout used when entering the room. Users can still switch
 *                                 layout from the bottom bar. Default [MeetingRoomLayoutMode.GRID].
 */
@ExperimentalMeetingRoomApi
data class MeetingRoomConfiguration(
    val allowCameraControl: Boolean = true,
    val allowMicrophoneControl: Boolean = true,
    val allowShowParticipantList: Boolean = true,
    val allowDeviceSelection: Boolean = true,
    val allowPictureInPicture: Boolean = true,
    val defaultLayoutMode: MeetingRoomLayoutMode = MeetingRoomLayoutMode.GRID,
)
