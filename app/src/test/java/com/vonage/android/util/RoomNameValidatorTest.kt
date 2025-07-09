package com.vonage.android.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class RoomNameValidatorTest {

    @ParameterizedTest
    @MethodSource("roomNames")
    fun `should validate room name`(roomName: String, isValid: Boolean) {
        assertEquals(isValid, roomName.isValidRoomName())
    }

    companion object {
        @JvmStatic
        fun roomNames() = listOf(
            Arguments.of("a", true),
            Arguments.of("room_name", true),
            Arguments.of("room+name", true),
            Arguments.of("another-room_name", true),
            Arguments.of("123roomname", true),
            Arguments.of("", false),
            Arguments.of("room@name", false),
            Arguments.of("room#name", false),
            Arguments.of("room\$name", false),
            Arguments.of("  room    name", false),
            Arguments.of("🙈", false),
            Arguments.of("roomname🙈", false),
            Arguments.of("roomname 🙈", false),
        )
    }
}