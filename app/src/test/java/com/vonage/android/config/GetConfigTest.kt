package com.vonage.android.config

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetConfigTest {

    private val getConfig = GetConfig()

    @Test
    fun `invoke should return Config with allowCameraControl from AppConfig`() {
        val config = getConfig()

        assertEquals(AppConfig.VideoSettings.ALLOW_CAMERA_CONTROL, config.allowCameraControl)
    }

    @Test
    fun `invoke should return Config with allowMicrophoneControl from AppConfig`() {
        val config = getConfig()

        assertEquals(AppConfig.AudioSettings.ALLOW_MICROPHONE_CONTROL, config.allowMicrophoneControl)
    }

    @Test
    fun `invoke should return Config with allowShowParticipantList from AppConfig`() {
        val config = getConfig()

        assertEquals(AppConfig.MeetingRoomSettings.SHOW_PARTICIPANT_LIST, config.allowShowParticipantList)
    }

    @Test
    fun `invoke should return Config with allowVideoOnJoin from AppConfig`() {
        val config = getConfig()

        assertEquals(AppConfig.VideoSettings.ALLOW_VIDEO_ON_JOIN, config.allowVideoOnJoin)
    }

    @Test
    fun `invoke should return Config with allowAudioOnJoin from AppConfig`() {
        val config = getConfig()

        assertEquals(AppConfig.AudioSettings.ALLOW_AUDIO_ON_JOIN, config.allowAudioOnJoin)
    }

    @Test
    fun `invoke should return Config with allowAudioDiagnostics from AppConfig`() {
        val config = getConfig()

        assertEquals(AppConfig.AudioSettings.ALLOW_AUDIO_DIAGNOSTICS, config.allowAudioDiagnostics)
    }

    @Test
    fun `invoke should return Config with allowBackgroundEffects from AppConfig`() {
        val config = getConfig()

        assertEquals(AppConfig.VideoSettings.ALLOW_BACKGROUND_EFFECTS, config.allowBackgroundEffects)
    }

    @Test
    fun `invoke should return Config with advanced noise suppression from AppConfig`() {
        val config = getConfig()

        assertEquals(
            AppConfig.AudioSettings.ALLOW_ADVANCED_NOISE_SUPPRESSION,
            config.allowAdvancedNoiseSuppression,
        )
    }

    @Test
    fun `invoke should map waiting room settings from AppConfig`() {
        val config = getConfig()

        assertEquals(
            AppConfig.WaitingRoomSettings.ALLOW_DEVICE_SELECTION,
            config.allowWaitingRoomDeviceSelection,
        )
        assertEquals(AppConfig.WaitingRoomSettings.ALLOW_SETTINGS, config.allowWaitingRoomSettings)
    }

    @Test
    fun `invoke should map meeting room settings from AppConfig`() {
        val config = getConfig()

        assertEquals(AppConfig.MeetingRoomSettings.ALLOW_SETTINGS, config.allowMeetingRoomSettings)
        assertEquals(AppConfig.MeetingRoomSettings.ALLOW_ARCHIVING, config.allowArchiving)
        assertEquals(AppConfig.MeetingRoomSettings.ALLOW_CAPTIONS, config.allowCaptions)
        assertEquals(AppConfig.MeetingRoomSettings.ALLOW_CHAT, config.allowChat)
        assertEquals(
            AppConfig.MeetingRoomSettings.ALLOW_DEVICE_SELECTION,
            config.allowMeetingRoomDeviceSelection,
        )
        assertEquals(AppConfig.MeetingRoomSettings.ALLOW_EMOJIS, config.allowEmojis)
        assertEquals(AppConfig.MeetingRoomSettings.ALLOW_FEEDBACK, config.allowFeedback)
        assertEquals(
            AppConfig.MeetingRoomSettings.ALLOW_PICTURE_IN_PICTURE,
            config.allowPictureInPicture,
        )
        assertEquals(AppConfig.MeetingRoomSettings.ALLOW_SCREEN_SHARE, config.allowScreenShare)
        assertEquals(
            AppConfig.MeetingRoomSettings.DEFAULT_LAYOUT_MODE,
            config.defaultLayoutMode,
        )
    }

    @Test
    fun `invoke should return consistent results on multiple calls`() {
        val first = getConfig()
        val second = getConfig()

        assertEquals(first, second)
    }
}
