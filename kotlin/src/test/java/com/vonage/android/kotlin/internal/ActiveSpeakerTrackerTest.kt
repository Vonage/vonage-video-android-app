package com.vonage.android.kotlin.internal

import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveSpeakerTrackerTest {

    private lateinit var tracker: ActiveSpeakerTracker

    @Test
    fun `audio level below threshold does not trigger change`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        tracker = ActiveSpeakerTracker(2000L, CoroutineScope(testDispatcher))

        tracker.activeSpeakerChanges.test {
            tracker.onSubscriberAudioLevelUpdated("stream1", 0.1f)

            advanceUntilIdle()
            advanceTimeBy(2000L)
            advanceUntilIdle()

            expectNoEvents()
        }
    }

    @Test
    fun `tracker accepts subscriber destruction without errors`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        tracker = ActiveSpeakerTracker(2000L, CoroutineScope(testDispatcher))

        tracker.onSubscriberDestroyed("nonexistent-stream")
        tracker.onSubscriberDestroyed("stream1")

        advanceUntilIdle()
        advanceTimeBy(2000L)
        advanceUntilIdle()
    }

    @Test
    fun `tracker accepts audio level updates without errors`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        tracker = ActiveSpeakerTracker(2000L, CoroutineScope(testDispatcher))

        tracker.onSubscriberAudioLevelUpdated("stream1", 0.0f)
        tracker.onSubscriberAudioLevelUpdated("stream2", 0.5f)
        tracker.onSubscriberAudioLevelUpdated("stream3", 1.0f)

        advanceUntilIdle()
        advanceTimeBy(2000L)
        advanceUntilIdle()
    }
}
