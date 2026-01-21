package com.vonage.android.config

import javax.inject.Inject

class GetConfig @Inject constructor() {

    operator fun invoke(): Config = Config(
        allowCameraControl = AppConfig.VideoSettings.ALLOW_CAMERA_CONTROL,
        allowMicrophoneControl = AppConfig.AudioSettings.ALLOW_MICROPHONE_CONTROL,
        allowShowParticipantList = AppConfig.MeetingRoomSettings.SHOW_PARTICIPANT_LIST,
    )
}

data class Config(
    val allowCameraControl: Boolean,
    val allowMicrophoneControl: Boolean,
    val allowShowParticipantList: Boolean,
)
