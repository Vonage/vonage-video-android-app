package com.vonage.android.reactions

import app.cash.turbine.test
import com.vonage.android.kotlin.model.EmojiState
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ReactionSignalPluginTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `canHandle should return true for reaction signal type`() {
        val plugin = ReactionSignalPlugin(testDispatcher)

        assertTrue(plugin.canHandle("emoji"))
    }

    @Test
    fun `canHandle should return false for other signal types`() {
        val plugin = ReactionSignalPlugin(testDispatcher)

        assertFalse(plugin.canHandle("chat"))
        assertFalse(plugin.canHandle("notification"))
        assertFalse(plugin.canHandle(""))
    }

    @Test
    fun `sendSignal should create RawSignal with reaction type`() {
        val plugin = ReactionSignalPlugin(testDispatcher)

        val rawSignal = plugin.sendSignal("John Doe", "👍")

        assertEquals("emoji", rawSignal.type)
        assertTrue(rawSignal.data.contains("👍"))
    }

    @Test
    fun `sendSignal should serialize emoji and timestamp to JSON`() {
        val plugin = ReactionSignalPlugin(testDispatcher)

        val rawSignal = plugin.sendSignal("Alice", "❤️")

        val signal = Json.decodeFromString<ReactionSignal>(rawSignal.data)
        assertEquals("❤️", signal.emoji)
        assertTrue(signal.time > 0)
    }

    @Test
    fun `handleSignal should add reaction to state`() = runTest(testDispatcher) {
        val plugin = ReactionSignalPlugin(testDispatcher)
        val reactionData = Json.encodeToString(
            ReactionSignal.serializer(),
            ReactionSignal("🎉", System.currentTimeMillis())
        )

        plugin.output.test {
            val initial = awaitItem() as EmojiState
            assertEquals(0, initial.reactions.size)

            plugin.handleSignal("emoji", reactionData, "Bob", false)
            testDispatcher.scheduler.runCurrent()

            val updated = awaitItem() as EmojiState
            assertEquals(1, updated.reactions.size)
            assertEquals("🎉", updated.reactions[0].emoji)
            assertEquals("Bob", updated.reactions[0].sender)
            assertFalse(updated.reactions[0].isYou)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleSignal should mark reaction as isYou when flag is true`() = runTest(testDispatcher) {
        val plugin = ReactionSignalPlugin(testDispatcher)
        val reactionData = Json.encodeToString(
            ReactionSignal.serializer(),
            ReactionSignal("👏", System.currentTimeMillis())
        )

        plugin.output.test {
            skipItems(1) // Skip initial state

            plugin.handleSignal(
                type = "emoji",
                data = reactionData,
                senderName = "Me",
                isYou = true,
            )
            testDispatcher.scheduler.runCurrent()

            val state = awaitItem() as EmojiState
            assertEquals(1, state.reactions.size)
            assertTrue(state.reactions[0].isYou)
            assertEquals("Me", state.reactions[0].sender)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleSignal should ignore invalid JSON`() = runTest(testDispatcher) {
        val plugin = ReactionSignalPlugin(testDispatcher)
        val invalidData = "not valid json"

        plugin.output.test {
            val initial = awaitItem() as EmojiState
            assertEquals(0, initial.reactions.size)

            plugin.handleSignal("emoji", invalidData, "Alice", false)
            testDispatcher.scheduler.runCurrent()

            // Should not emit new state for invalid data
            expectNoEvents()
        }
    }

    @Test
    fun `handleSignal should ignore non-reaction signal types`() = runTest(testDispatcher) {
        val plugin = ReactionSignalPlugin(testDispatcher)
        val reactionData = Json.encodeToString(
            ReactionSignal.serializer(),
            ReactionSignal("👍", System.currentTimeMillis())
        )

        plugin.output.test {
            val initial = awaitItem() as EmojiState
            assertEquals(0, initial.reactions.size)

            plugin.handleSignal("chat", reactionData, "Bob", false)
            testDispatcher.scheduler.runCurrent()

            // Should not process non-reaction signals
            expectNoEvents()
        }
    }

    @Test
    fun `handleSignal should add reactions in reverse order`() = runTest(testDispatcher) {
        val plugin = ReactionSignalPlugin(testDispatcher)

        plugin.output.test {
            skipItems(1) // Skip initial state

            val reaction1 = Json.encodeToString(
                ReactionSignal.serializer(),
                ReactionSignal("😊", 1000L)
            )
            plugin.handleSignal("emoji", reaction1, "Alice", false)
            testDispatcher.scheduler.runCurrent()

            val state1 = awaitItem() as EmojiState
            assertEquals(1, state1.reactions.size)

            val reaction2 = Json.encodeToString(
                ReactionSignal.serializer(),
                ReactionSignal("🔥", 2000L)
            )
            plugin.handleSignal("emoji", reaction2, "Bob", false)
            testDispatcher.scheduler.runCurrent()

            val state2 = awaitItem() as EmojiState
            assertEquals(2, state2.reactions.size)
            assertEquals("🔥", state2.reactions[0].emoji) // Most recent first
            assertEquals("😊", state2.reactions[1].emoji)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `output should emit EmojiState`() = runTest(testDispatcher) {
        val plugin = ReactionSignalPlugin(testDispatcher)

        plugin.output.test {
            val state = awaitItem()
            assertNotNull(state)
            assertTrue(state is EmojiState)

            cancelAndIgnoreRemainingEvents()
        }
    }
}