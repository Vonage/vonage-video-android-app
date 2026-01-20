package com.vonage.android.kotlin.signal

import org.junit.Assert.assertEquals
import org.junit.Test

class EmojiReactionTest {

    @Test
    fun `should create EmojiReaction with all properties`() {
        val reaction = EmojiReaction(
            id = 123456789L,
            emoji = "👍",
            sender = "John Doe",
            isYou = false,
            startTime = 123456789L
        )

        assertEquals(123456789L, reaction.id)
        assertEquals("👍", reaction.emoji)
        assertEquals("John Doe", reaction.sender)
        assertEquals(false, reaction.isYou)
        assertEquals(123456789L, reaction.startTime)
    }

    @Test
    fun `should support data class copy`() {
        val original = EmojiReaction(
            id = 111L,
            emoji = "😊",
            sender = "Alice",
            isYou = true,
            startTime = 111L
        )

        val copy = original.copy(emoji = "😂")

        assertEquals(111L, copy.id)
        assertEquals("😂", copy.emoji)
        assertEquals("Alice", copy.sender)
        assertEquals(true, copy.isYou)
        assertEquals(111L, copy.startTime)
    }

    @Test
    fun `should support equality comparison`() {
        val reaction1 = EmojiReaction(
            id = 999L,
            emoji = "❤️",
            sender = "Bob",
            isYou = false,
            startTime = 999L
        )

        val reaction2 = EmojiReaction(
            id = 999L,
            emoji = "❤️",
            sender = "Bob",
            isYou = false,
            startTime = 999L
        )

        assertEquals(reaction1, reaction2)
        assertEquals(reaction1.hashCode(), reaction2.hashCode())
    }

    @Test
    fun `should handle different isYou values`() {
        val myReaction = EmojiReaction(
            id = 1L,
            emoji = "🎉",
            sender = "Me",
            isYou = true,
            startTime = 1L
        )

        val theirReaction = EmojiReaction(
            id = 2L,
            emoji = "🎉",
            sender = "Them",
            isYou = false,
            startTime = 2L
        )

        assertEquals(true, myReaction.isYou)
        assertEquals(false, theirReaction.isYou)
    }
}
