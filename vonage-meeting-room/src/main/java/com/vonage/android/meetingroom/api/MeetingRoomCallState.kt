package com.vonage.android.meetingroom.api

/**
 * Snapshot of the current call state exposed to the host application.
 *
 * @param isConnected         `true` once the session is connected and media is flowing.
 * @param participantCount    Total number of remote participants currently in the call.
 * @param isLocalMicEnabled   `true` when the local microphone is publishing audio.
 * @param isLocalCameraEnabled `true` when the local camera is publishing video.
 * @param roomName            Name of the meeting room.
 */
@ExperimentalMeetingRoomApi
data class MeetingRoomCallState(
    val isConnected: Boolean = false,
    val participantCount: Int = 0,
    val isLocalMicEnabled: Boolean = true,
    val isLocalCameraEnabled: Boolean = true,
    val roomName: String = "",
)
