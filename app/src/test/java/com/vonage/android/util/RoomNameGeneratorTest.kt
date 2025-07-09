package com.vonage.android.util

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RoomNameGeneratorTest {

    @Test
    fun `should generate a correct random room name`() {
        val (adjective, animal) = RoomNameGenerator.generateRoomName().split("-")
        assertTrue(RoomNameGenerator.animals.contains(animal))
        assertTrue(RoomNameGenerator.adjectives.contains(adjective))
    }
}