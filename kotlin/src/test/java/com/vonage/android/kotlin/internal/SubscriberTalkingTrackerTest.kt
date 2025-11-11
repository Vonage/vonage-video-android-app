package com.vonage.android.kotlin.internal

import app.cash.turbine.test
import com.vonage.android.kotlin.ext.mapTalking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SubscriberTalkingTrackerTest {

    private lateinit var tracker: SubscriberTalkingTracker

    @Before
    fun setUp() {
        tracker = SubscriberTalkingTracker()
    }

    @Test
    fun `given low audio level when tracking then no talking state emitted`() = runTest {
        val audioLevelFlow = flow {
            emit(0.05f) // below SPEAKING_VOLUME (0.1)
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given high audio level when tracking then talking state changes to true after threshold`() = runTest {
        val audioLevelFlow = flow {
            emit(0.15f) // above SPEAKING_VOLUME
            delay(150) // beyond SPEAKING_TIME_THRESHOLD (100ms)
            emit(0.15f)
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem()) // initial state
            assertEquals(true, awaitItem()) // talking detected
            awaitComplete()
        }
    }

    @Test
    fun `given talking state when low audio levels continue then talking state changes to false after timeout`() = runTest {
        val audioLevelFlow = flow {
            emit(0.15f) // high audio
            delay(150) // beyond SPEAKING_TIME_THRESHOLD
            emit(0.15f) // triggers talking = true
            emit(0.05f) // low audio
            delay(900) // beyond NOT_SPEAKING_TIME_THRESHOLD (800ms)
            emit(0.05f) // triggers talking = false
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem()) // initial
            assertEquals(true, awaitItem()) // talking started
            assertEquals(false, awaitItem()) // talking stopped
            awaitComplete()
        }
    }

    @Test
    fun `given short duration high audio when within time thresholds then no talking notification`() = runTest {
        val audioLevelFlow = flow {
            emit(0.15f) // high audio
            delay(50) // less than SPEAKING_TIME_THRESHOLD (100ms)
            emit(0.05f) // switch to low before threshold reached
            delay(200)
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem()) // stays false
            awaitComplete()
        }
    }

    @Test
    fun `given talking state when high audio continues then state stays true`() = runTest {
        val audioLevelFlow = flow {
            emit(0.15f)
            delay(150)
            emit(0.15f) // triggers first talking = true
            delay(200)
            emit(0.15f) // continues talking
            delay(200)
            emit(0.15f) // still talking
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem()) // initial
            assertEquals(true, awaitItem()) // talking detected
            // distinctUntilChanged ensures no duplicate emissions
            awaitComplete()
        }
    }

    @Test
    fun `given multiple talking cycles when state changes repeatedly then correct sequence of notifications`() = runTest {
        val audioLevelFlow = flow {
            emit(0.15f)
            delay(150)
            emit(0.15f) // triggers talking = true
            
            emit(0.05f)
            delay(900)
            emit(0.05f) // triggers talking = false
            
            emit(0.15f)
            delay(150)
            emit(0.15f) // triggers talking = true again
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given exact threshold audio level when tracking then talking detected`() = runTest {
        val audioLevelFlow = flow {
            emit(0.1f) // exactly SPEAKING_VOLUME
            delay(150)
            emit(0.1f)
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given boundary audio level just above threshold when tracking then talking detected`() = runTest {
        val audioLevelFlow = flow {
            emit(0.10001f) // just above SPEAKING_VOLUME (0.1)
            delay(150)
            emit(0.10001f)
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given boundary audio level just below threshold when tracking then no talking detected`() = runTest {
        val audioLevelFlow = flow {
            emit(0.09999f) // just below SPEAKING_VOLUME (0.1)
            delay(150)
            emit(0.09999f)
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem()) // stays false
            awaitComplete()
        }
    }

    @Test
    fun `given rapid audio level changes when tracking then state updates correctly`() = runTest {
        val audioLevelFlow = flow {
            emit(0.15f) // high
            emit(0.05f) // low
            emit(0.15f) // high
            delay(150)
            emit(0.15f) // sustained high
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem()) // talking detected after sustained high
            awaitComplete()
        }
    }

    @Test
    fun `given distinctUntilChanged when same state persists then no duplicate emissions`() = runTest {
        val audioLevelFlow = flow {
            emit(0.05f) // low
            emit(0.05f) // still low
            emit(0.05f) // still low
            emit(0.05f) // still low
        }

        tracker.mapTalking(audioLevelFlow).test {
            assertEquals(false, awaitItem()) // only one emission
            awaitComplete()
        }
    }
}