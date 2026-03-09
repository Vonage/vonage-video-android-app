package com.vonage.android.util

import org.junit.Assert.assertTrue
import org.junit.Test

class RoomNameGeneratorTest {

    @Test
    fun `should generate a correct random room name`() {
        val roomNameGenerator = RoomNameGenerator()
        val (adjective, animal) = roomNameGenerator.generateRoomName().split("-")
        assertTrue(roomNameGenerator.animals.contains(animal))
        assertTrue(roomNameGenerator.adjectives.contains(adjective))
    }
}
