package com.vonage.android.kotlin.ext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlowTalkingTrackerTest {

    @Test
    fun `mapTalking constants should have expected values`() {
        assertEquals(100L, SPEAKING_TIME_THRESHOLD)
        assertEquals(800L, NOT_SPEAKING_TIME_THRESHOLD)
        assertEquals(0.1f, SPEAKING_VOLUME, 0.001f)
    }

    @Test
    fun `mapTalking should require longer silence to stop than start`() {
        // This is the hysteresis behavior
        assertTrue(NOT_SPEAKING_TIME_THRESHOLD > SPEAKING_TIME_THRESHOLD)
    }

    @Test
    fun `SPEAKING_VOLUME threshold should be reasonable`() {
        // Volume should be between 0 and 1
        assertTrue(SPEAKING_VOLUME > 0f)
        assertTrue(SPEAKING_VOLUME < 1f)
    }
}
