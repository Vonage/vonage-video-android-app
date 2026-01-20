package com.vonage.android.kotlin.signal

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ReactionSignalTest {

    @Test
    fun `should serialize ReactionSignal to JSON`() {
        val signal = ReactionSignal(
            emoji = "👍",
            time = 1234567890L
        )

        val json = Json.encodeToString(ReactionSignal.serializer(), signal)

        assertEquals(true, json.contains("\"emoji\":\"👍\""))
        assertEquals(true, json.contains("\"time\":1234567890"))
    }

    @Test
    fun `should deserialize JSON to ReactionSignal`() {
        val json = """{"emoji":"❤️","time":9876543210}"""

        val signal = Json.decodeFromString<ReactionSignal>(json)

        assertEquals("❤️", signal.emoji)
        assertEquals(9876543210L, signal.time)
    }

    @Test
    fun `should round-trip serialize and deserialize`() {
        val original = ReactionSignal(
            emoji = "🎉",
            time = 1111111111L
        )

        val json = Json.encodeToString(ReactionSignal.serializer(), original)
        val deserialized = Json.decodeFromString<ReactionSignal>(json)

        assertEquals(original.emoji, deserialized.emoji)
        assertEquals(original.time, deserialized.time)
    }

    @Test
    fun `should handle various emoji types`() {
        val emojis = listOf("😊", "🔥", "👏", "💯", "🚀")

        emojis.forEach { emoji ->
            val signal = ReactionSignal(emoji = emoji, time = System.currentTimeMillis())
            val json = Json.encodeToString(ReactionSignal.serializer(), signal)
            val deserialized = Json.decodeFromString<ReactionSignal>(json)

            assertEquals(emoji, deserialized.emoji)
        }
    }

    @Test
    fun `should support data class copy`() {
        val original = ReactionSignal("😀", 123L)
        val copy = original.copy(emoji = "😎")

        assertEquals("😎", copy.emoji)
        assertEquals(123L, copy.time)
    }
}
