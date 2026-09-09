package com.vonage.android.config

import javax.inject.Inject

class GetConfig @Inject constructor() {

    operator fun invoke(): Config = Config(
        allowCameraControl = AppConfig.VideoSettings.ALLOW_CAMERA_CONTROL,
        allowMicrophoneControl = AppConfig.AudioSettings.ALLOW_MICROPHONE_CONTROL,
        allowShowParticipantList = AppConfig.MeetingRoomSettings.SHOW_PARTICIPANT_LIST,
        allowVideoOnJoin = AppConfig.VideoSettings.ALLOW_VIDEO_ON_JOIN,
        allowAudioOnJoin = AppConfig.AudioSettings.ALLOW_AUDIO_ON_JOIN,
    )
}

data class Config(
    val allowCameraControl: Boolean,
    val allowMicrophoneControl: Boolean,
    val allowShowParticipantList: Boolean,
    val allowVideoOnJoin: Boolean,
    val allowAudioOnJoin: Boolean,
)
