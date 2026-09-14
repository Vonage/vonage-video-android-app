package com.vonage.android.config

import javax.inject.Inject

/**
 * Provides the app's view of `config/app-config.json` for Hilt-injected consumers.
 *
 * Prefer this in ViewModels. Composables that cannot receive an injected dependency should call
 * [Config.fromAppConfig] directly — both go through the same mapping, so the two cannot drift.
 */
class GetConfig @Inject constructor() {

    operator fun invoke(): Config = Config.fromAppConfig()
}

/**
 * Typed snapshot of the generated [AppConfig] constants.
 *
 * Every field of `app-config.json` that drives Android behavior is represented here, so the
 * generated constants are read in exactly one place ([fromAppConfig]).
 *
 * Two config fields are intentionally absent because they are not implemented on Android and
 * exist only for parity with the shared cross-platform config schema:
 *  - `waitingRoomSettings.bypassWaitingRoom`
 *  - `videoSettings.defaultResolution` (capture resolution is chosen by the SDK per device, or by
 *    the user in the in-call settings panel)
 *
 * See `docs/CONFIGURATION.md`.
 */
@Suppress("LongParameterList")
data class Config(
    // Video
    val allowBackgroundEffects: Boolean,
    val allowCameraControl: Boolean,
    val allowVideoOnJoin: Boolean,
    // Audio
    val allowAdvancedNoiseSuppression: Boolean,
    val allowAudioOnJoin: Boolean,
    val allowMicrophoneControl: Boolean,
    val allowAudioDiagnostics: Boolean,
    // Waiting room
    val allowWaitingRoomDeviceSelection: Boolean,
    val allowWaitingRoomSettings: Boolean,
    // Meeting room
    val allowMeetingRoomSettings: Boolean,
    val allowArchiving: Boolean,
    val allowCaptions: Boolean,
    val allowChat: Boolean,
    val allowMeetingRoomDeviceSelection: Boolean,
    val allowEmojis: Boolean,
    val allowFeedback: Boolean,
    val allowPictureInPicture: Boolean,
    val allowScreenShare: Boolean,
    val allowShowParticipantList: Boolean,
    /**
     * Raw `meetingRoomSettings.defaultLayoutMode` value. Kept as a String so this class stays
     * free of the meeting-room module's experimental API; parsed by the caller via
     * `MeetingRoomLayoutMode.fromConfigValue`.
     */
    val defaultLayoutMode: String,
) {
    companion object {

        /** The single place where generated [AppConfig] constants are read. */
        fun fromAppConfig(): Config = Config(
            allowBackgroundEffects = AppConfig.VideoSettings.ALLOW_BACKGROUND_EFFECTS,
            allowCameraControl = AppConfig.VideoSettings.ALLOW_CAMERA_CONTROL,
            allowVideoOnJoin = AppConfig.VideoSettings.ALLOW_VIDEO_ON_JOIN,
            allowAdvancedNoiseSuppression = AppConfig.AudioSettings.ALLOW_ADVANCED_NOISE_SUPPRESSION,
            allowAudioOnJoin = AppConfig.AudioSettings.ALLOW_AUDIO_ON_JOIN,
            allowMicrophoneControl = AppConfig.AudioSettings.ALLOW_MICROPHONE_CONTROL,
            allowAudioDiagnostics = AppConfig.AudioSettings.ALLOW_AUDIO_DIAGNOSTICS,
            allowWaitingRoomDeviceSelection = AppConfig.WaitingRoomSettings.ALLOW_DEVICE_SELECTION,
            allowWaitingRoomSettings = AppConfig.WaitingRoomSettings.ALLOW_SETTINGS,
            allowMeetingRoomSettings = AppConfig.MeetingRoomSettings.ALLOW_SETTINGS,
            allowArchiving = AppConfig.MeetingRoomSettings.ALLOW_ARCHIVING,
            allowCaptions = AppConfig.MeetingRoomSettings.ALLOW_CAPTIONS,
            allowChat = AppConfig.MeetingRoomSettings.ALLOW_CHAT,
            allowMeetingRoomDeviceSelection = AppConfig.MeetingRoomSettings.ALLOW_DEVICE_SELECTION,
            allowEmojis = AppConfig.MeetingRoomSettings.ALLOW_EMOJIS,
            allowFeedback = AppConfig.MeetingRoomSettings.ALLOW_FEEDBACK,
            allowPictureInPicture = AppConfig.MeetingRoomSettings.ALLOW_PICTURE_IN_PICTURE,
            allowScreenShare = AppConfig.MeetingRoomSettings.ALLOW_SCREEN_SHARE,
            allowShowParticipantList = AppConfig.MeetingRoomSettings.SHOW_PARTICIPANT_LIST,
            defaultLayoutMode = AppConfig.MeetingRoomSettings.DEFAULT_LAYOUT_MODE,
        )
    }
}
