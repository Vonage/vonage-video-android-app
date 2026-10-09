package com.vonage.android.kotlin.internal

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CaptionsHideSchedulerTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    private val hiddenStreams = mutableListOf<String>()

    private fun createScheduler(delayMs: Long = 2_000L) = CaptionsHideScheduler(
        coroutineScope = testScope,
        hideDelayMs = delayMs,
        onHide = { hiddenStreams.add(it) },
    )

    @Test
    fun `schedule fires onHide after delay`() = testScope.runTest {
        val scheduler = createScheduler()

        scheduler.schedule("stream-1")

        assertTrue(hiddenStreams.isEmpty())
        advanceTimeBy(2_001L)

        assertEquals(listOf("stream-1"), hiddenStreams)
    }

    @Test
    fun `schedule before delay replaces previous job`() = testScope.runTest {
        val scheduler = createScheduler()

        scheduler.schedule("stream-1")
        advanceTimeBy(500L) // halfway through first job

        scheduler.schedule("stream-1") // replaces it
        advanceTimeBy(2_001L) // full delay from second call

        // onHide fired exactly once
        assertEquals(1, hiddenStreams.size)
        assertEquals("stream-1", hiddenStreams.first())
    }

    @Test
    fun `cancel prevents onHide from firing`() = testScope.runTest {
        val scheduler = createScheduler()

        scheduler.schedule("stream-1")
        scheduler.cancel("stream-1")
        advanceTimeBy(2_001L)

        assertTrue(hiddenStreams.isEmpty())
    }

    @Test
    fun `cancelAll prevents all pending hides`() = testScope.runTest {
        val scheduler = createScheduler()

        scheduler.schedule("stream-1")
        scheduler.schedule("stream-2")
        scheduler.cancelAll()
        advanceTimeBy(2_001L)

        assertTrue(hiddenStreams.isEmpty())
    }

    @Test
    fun `schedule after cancel works correctly`() = testScope.runTest {
        val scheduler = createScheduler()

        scheduler.schedule("stream-1")
        scheduler.cancel("stream-1")
        scheduler.schedule("stream-1")
        advanceTimeBy(2_001L)

        assertEquals(listOf("stream-1"), hiddenStreams)
    }

    @Test
    fun `independent streams have independent timers`() = testScope.runTest {
        val scheduler = createScheduler()

        scheduler.schedule("stream-1")
        advanceTimeBy(1_000L) // halfway

        scheduler.schedule("stream-2")
        advanceTimeBy(1_001L) // stream-1 fires, stream-2 halfway

        assertEquals(listOf("stream-1"), hiddenStreams)

        advanceTimeBy(1_000L) // stream-2 fires

        assertEquals(listOf("stream-1", "stream-2"), hiddenStreams)
    }

    @Test
    fun `cancel of unknown stream is safe`() = testScope.runTest {
        val scheduler = createScheduler()
        // Should not throw
        scheduler.cancel("non-existent")
    }

    @Test
    fun `cancelAll on empty scheduler is safe`() = testScope.runTest {
        val scheduler = createScheduler()
        // Should not throw
        scheduler.cancelAll()
    }
}
