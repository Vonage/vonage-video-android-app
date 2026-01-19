package com.vonage.android.config

object Config {

    fun isAllowVideoOnJoin(): Boolean {
        return AppConfig.VideoSettings.ALLOW_VIDEO_ON_JOIN
    }

    fun isAllowAudioOnJoin(): Boolean {
        return AppConfig.AudioSettings.ALLOW_AUDIO_ON_JOIN
    }

    fun isAllowCameraControl(): Boolean {
        return AppConfig.VideoSettings.ALLOW_CAMERA_CONTROL
    }

    fun isAllowMicrophoneControl(): Boolean {
        return AppConfig.AudioSettings.ALLOW_MICROPHONE_CONTROL
    }

    fun isShowParticipantList(): Boolean {
        return AppConfig.MeetingRoomSettings.SHOW_PARTICIPANT_LIST
    }
}
