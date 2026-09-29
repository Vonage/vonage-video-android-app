package com.vonage.android.meetingroom.api

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalMeetingRoomApi::class)
class MeetingRoomLayoutModeTest {

    @Test
    fun `given grid when fromConfigValue then returns GRID`() {
        assertEquals(MeetingRoomLayoutMode.GRID, MeetingRoomLayoutMode.fromConfigValue("grid"))
    }

    @Test
    fun `given activespeaker when fromConfigValue then returns ACTIVE_SPEAKER`() {
        assertEquals(
            MeetingRoomLayoutMode.ACTIVE_SPEAKER,
            MeetingRoomLayoutMode.fromConfigValue("activespeaker"),
        )
    }

    @Test
    fun `given mixed case and separators when fromConfigValue then still parses`() {
        listOf("ActiveSpeaker", "ACTIVE_SPEAKER", "active-speaker").forEach { value ->
            assertEquals(
                MeetingRoomLayoutMode.ACTIVE_SPEAKER,
                MeetingRoomLayoutMode.fromConfigValue(value),
                "expected $value to parse as ACTIVE_SPEAKER",
            )
        }
    }

    @Test
    fun `given unrecognised value when fromConfigValue then falls back to default`() {
        assertEquals(MeetingRoomLayoutMode.GRID, MeetingRoomLayoutMode.fromConfigValue("filmstrip"))
        assertEquals(MeetingRoomLayoutMode.GRID, MeetingRoomLayoutMode.fromConfigValue(""))
    }

    @Test
    fun `given unrecognised value and explicit default when fromConfigValue then uses it`() {
        assertEquals(
            MeetingRoomLayoutMode.ACTIVE_SPEAKER,
            MeetingRoomLayoutMode.fromConfigValue(
                value = "filmstrip",
                default = MeetingRoomLayoutMode.ACTIVE_SPEAKER,
            ),
        )
    }
}
