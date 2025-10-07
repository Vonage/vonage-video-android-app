package com.vonage.android.kotlin.internal

import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.Before
import org.junit.Test

class SubscriberTalkingTrackerTest {

    private lateinit var tracker: SubscriberTalkingTracker
    private lateinit var listener: TalkingStateListener

    @Before
    fun setUp() {
        tracker = SubscriberTalkingTracker()
        listener = mockk(relaxed = true)
        tracker.setTalkingStateListener(listener)
    }

    @Test
    fun `given initial state when no audio level updates then no talking state changes`() {
        verify(exactly = 0) { listener.onTalkingStateChanged(any()) }
    }

    @Test
    fun `given low audio level when onAudioLevelUpdated then no talking state change`() {
        val lowAudioLevel = 0.05f // below SPEAKING_VOLUME (0.1)
        
        tracker.onAudioLevelUpdated(lowAudioLevel)
        
        verify(exactly = 0) { listener.onTalkingStateChanged(any()) }
    }

    @Test
    fun `given high audio level when onAudioLevelUpdated then talking state changes to true after threshold`() {
        val highAudioLevel = 0.15f // above SPEAKING_VOLUME (0.1)
        
        tracker.onAudioLevelUpdated(highAudioLevel)
        
        verify(exactly = 0) { listener.onTalkingStateChanged(any()) }
        
        Thread.sleep(150) // beyond SPEAKING_TIME_THRESHOLD (100ms)
        tracker.onAudioLevelUpdated(highAudioLevel)
        
        verify(exactly = 1) { listener.onTalkingStateChanged(true) }
    }

    @Test
    fun `given talking state when low audio levels continue then talking state changes to false after timeout`() {
        val highAudioLevel = 0.15f
        val lowAudioLevel = 0.05f
        
        tracker.onAudioLevelUpdated(highAudioLevel)
        Thread.sleep(150) // beyond SPEAKING_TIME_THRESHOLD
        tracker.onAudioLevelUpdated(highAudioLevel)
        
        tracker.onAudioLevelUpdated(lowAudioLevel)
        Thread.sleep(900) // beyond NOT_SPEAKING_TIME_THRESHOLD (800ms)
        tracker.onAudioLevelUpdated(lowAudioLevel)
        
        verifySequence {
            listener.onTalkingStateChanged(true)
            listener.onTalkingStateChanged(false)
        }
    }

    @Test
    fun `given short duration high audio when within time thresholds then no talking notification`() {
        val highAudioLevel = 0.15f
        val lowAudioLevel = 0.05f
        
        tracker.onAudioLevelUpdated(highAudioLevel) // start talking timer
        Thread.sleep(50) // less than SPEAKING_TIME_THRESHOLD (100ms)
        tracker.onAudioLevelUpdated(lowAudioLevel) // switch to low before threshold reached
        
        // Wait longer than the threshold to ensure any delayed notifications would have fired
        Thread.sleep(200)
        
        verify(exactly = 0) { listener.onTalkingStateChanged(any()) }
    }

    @Test
    fun `given talking state when high audio continues then multiple talking notifications`() {
        val highAudioLevel = 0.15f
        
        tracker.onAudioLevelUpdated(highAudioLevel)
        Thread.sleep(150)
        tracker.onAudioLevelUpdated(highAudioLevel) // triggers first notification
        
        Thread.sleep(200)
        tracker.onAudioLevelUpdated(highAudioLevel) // triggers second notification
        Thread.sleep(200)
        tracker.onAudioLevelUpdated(highAudioLevel) // triggers third notification
        
        verify(exactly = 3) { listener.onTalkingStateChanged(true) }
        verify(exactly = 0) { listener.onTalkingStateChanged(false) }
    }

    @Test
    fun `given multiple talking cycles when state changes repeatedly then correct sequence of notifications`() {
        val highAudioLevel = 0.15f
        val lowAudioLevel = 0.05f
        
        tracker.onAudioLevelUpdated(highAudioLevel)
        Thread.sleep(150)
        tracker.onAudioLevelUpdated(highAudioLevel) // triggers talking = true
        
        tracker.onAudioLevelUpdated(lowAudioLevel)
        Thread.sleep(900)
        tracker.onAudioLevelUpdated(lowAudioLevel) // triggers talking = false
        
        tracker.onAudioLevelUpdated(highAudioLevel)
        Thread.sleep(150)
        tracker.onAudioLevelUpdated(highAudioLevel) // triggers talking = true again
        
        verifySequence {
            listener.onTalkingStateChanged(true)
            listener.onTalkingStateChanged(false)
            listener.onTalkingStateChanged(true)
        }
    }

    @Test
    fun `given exact threshold audio level when onAudioLevelUpdated then talking detected`() {
        val exactThresholdLevel = 0.1f // exactly SPEAKING_VOLUME
        
        tracker.onAudioLevelUpdated(exactThresholdLevel)
        Thread.sleep(150)
        tracker.onAudioLevelUpdated(exactThresholdLevel)
        
        verify(exactly = 1) { listener.onTalkingStateChanged(true) }
    }

    @Test
    fun `given boundary audio level just above threshold when onAudioLevelUpdated then talking detected`() {
        val justAboveThreshold = 0.10001f // just above SPEAKING_VOLUME (0.1)
        
        tracker.onAudioLevelUpdated(justAboveThreshold)
        Thread.sleep(150)
        tracker.onAudioLevelUpdated(justAboveThreshold)
        
        verify(exactly = 1) { listener.onTalkingStateChanged(true) }
    }

    @Test
    fun `given boundary audio level just below threshold when onAudioLevelUpdated then no talking detected`() {
        val justBelowThreshold = 0.09999f // just below SPEAKING_VOLUME (0.1)
        
        tracker.onAudioLevelUpdated(justBelowThreshold)
        Thread.sleep(150)
        tracker.onAudioLevelUpdated(justBelowThreshold)
        
        verify(exactly = 0) { listener.onTalkingStateChanged(any()) }
    }

    @Test
    fun `given no listener set when onAudioLevelUpdated then no exceptions thrown`() {
        val trackerWithoutListener = SubscriberTalkingTracker()
        val highAudioLevel = 0.15f
        
        trackerWithoutListener.onAudioLevelUpdated(highAudioLevel)
        Thread.sleep(150)
        trackerWithoutListener.onAudioLevelUpdated(highAudioLevel)
    }

    @Test
    fun `given listener set multiple times when onAudioLevelUpdated then only latest listener receives callbacks`() {
        val firstListener = mockk<TalkingStateListener>(relaxed = true)
        val secondListener = mockk<TalkingStateListener>(relaxed = true)
        val highAudioLevel = 0.15f
        
        tracker.setTalkingStateListener(firstListener)
        tracker.setTalkingStateListener(secondListener) // replaces first listener
        
        tracker.onAudioLevelUpdated(highAudioLevel)
        Thread.sleep(150)
        tracker.onAudioLevelUpdated(highAudioLevel)
        
        verify(exactly = 0) { firstListener.onTalkingStateChanged(any()) }
        verify(exactly = 1) { secondListener.onTalkingStateChanged(true) }
    }
}