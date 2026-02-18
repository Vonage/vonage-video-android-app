package com.vonage.android.config

import org.junit.Test
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
    fun `invoke should return consistent results on multiple calls`() {
        val first = getConfig()
        val second = getConfig()

        assertEquals(first, second)
    }
}
