package com.vonage.android.kotlin.internal

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SpeakingWhileMutedDetectorTest {

    private val isMicEnabled = MutableStateFlow(true)
    private val audioLevel = MutableStateFlow(0f)

    private val detector = SpeakingWhileMutedDetector(
        isMicEnabled = isMicEnabled,
        audioLevel = audioLevel,
    )

    @Test
    fun `should not trigger when mic is enabled`() = runTest {
        detector.isSpeakingWhileMuted.test {
            // Initial state
            assertFalse(awaitItem())

            // Mic enabled, speaking loudly
            audioLevel.value = 0.5f
            expectNoEvents()

            cancel()
        }
    }

    @Test
    fun `should not trigger when muted but silent`() = runTest {
        detector.isSpeakingWhileMuted.test {
            assertFalse(awaitItem())

            isMicEnabled.value = false
            audioLevel.value = 0.01f
            expectNoEvents()

            cancel()
        }
    }

    @Test
    fun `should trigger when muted and speaking above threshold`() = runTest {
        detector.isSpeakingWhileMuted.test {
            assertFalse(awaitItem())

            isMicEnabled.value = false

            // Emit enough consecutive loud samples to exceed TRIGGER_THRESHOLD
            repeat(SpeakingWhileMutedDetector.TRIGGER_THRESHOLD + 1) {
                audioLevel.value = 0f // reset to trigger re-emission
                audioLevel.value = 0.2f
            }

            assertTrue(awaitItem())
            cancel()
        }
    }

    @Test
    fun `should reset when mic is re-enabled`() = runTest {
        detector.isSpeakingWhileMuted.test {
            assertFalse(awaitItem())

            // Trigger speaking while muted
            isMicEnabled.value = false
            repeat(SpeakingWhileMutedDetector.TRIGGER_THRESHOLD + 1) {
                audioLevel.value = 0f
                audioLevel.value = 0.2f
            }
            assertTrue(awaitItem())

            // Re-enable mic - should reset since condition no longer met
            isMicEnabled.value = true
            assertFalse(awaitItem())

            cancel()
        }
    }

    @Test
    fun `constants should have expected values`() {
        assertTrue(SpeakingWhileMutedDetector.AUDIO_LEVEL_THRESHOLD > 0f)
        assertTrue(SpeakingWhileMutedDetector.AUDIO_LEVEL_THRESHOLD < 1f)
        assertTrue(SpeakingWhileMutedDetector.TRIGGER_THRESHOLD > 0)
    }
}
