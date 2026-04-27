package com.vonage.android.meetingroom.api

import android.os.Bundle

/**
 * Runtime configuration for the meeting room.
 *
 * Pass an instance to [MeetingRoomActivity] via [MeetingRoom.launch] or to the
 * [MeetingRoomComponent] composable. All fields have sensible defaults so callers
 * only need to supply [baseUrl] and [roomName].
 *
 * @param baseUrl       Base URL of the Vonage Video backend (e.g. "https://my-backend.example.com").
 * @param roomName      Name of the meeting room to join.
 * @param userName      Display name for the local participant. Defaults to empty string.
 * @param allowCameraControl       Whether the UI shows a camera toggle. Default true.
 * @param allowMicrophoneControl   Whether the UI shows a mic toggle. Default true.
 * @param allowShowParticipantList Whether the UI shows the participant list. Default true.
 */
data class MeetingRoomConfig(
    val baseUrl: String,
    val roomName: String,
    val userName: String = "",
    val allowCameraControl: Boolean = true,
    val allowMicrophoneControl: Boolean = true,
    val allowShowParticipantList: Boolean = true,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString(KEY_BASE_URL, baseUrl)
        putString(KEY_ROOM_NAME, roomName)
        putString(KEY_USER_NAME, userName)
        putBoolean(KEY_ALLOW_CAMERA, allowCameraControl)
        putBoolean(KEY_ALLOW_MIC, allowMicrophoneControl)
        putBoolean(KEY_ALLOW_PARTICIPANTS, allowShowParticipantList)
    }

    companion object {
        const val KEY_BASE_URL = "meetingRoom_baseUrl"
        const val KEY_ROOM_NAME = "meetingRoom_roomName"
        const val KEY_USER_NAME = "meetingRoom_userName"
        const val KEY_ALLOW_CAMERA = "meetingRoom_allowCamera"
        const val KEY_ALLOW_MIC = "meetingRoom_allowMic"
        const val KEY_ALLOW_PARTICIPANTS = "meetingRoom_allowParticipants"

        fun fromBundle(bundle: Bundle): MeetingRoomConfig = MeetingRoomConfig(
            baseUrl = bundle.getString(KEY_BASE_URL, ""),
            roomName = bundle.getString(KEY_ROOM_NAME, ""),
            userName = bundle.getString(KEY_USER_NAME, ""),
            allowCameraControl = bundle.getBoolean(KEY_ALLOW_CAMERA, true),
            allowMicrophoneControl = bundle.getBoolean(KEY_ALLOW_MIC, true),
            allowShowParticipantList = bundle.getBoolean(KEY_ALLOW_PARTICIPANTS, true),
        )
    }
}
