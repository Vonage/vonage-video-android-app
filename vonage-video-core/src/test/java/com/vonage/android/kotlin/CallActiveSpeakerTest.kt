package com.vonage.android.kotlin

import app.cash.turbine.test
import com.vonage.android.kotlin.internal.ActiveSpeakerChangedPayload
import com.vonage.android.kotlin.internal.ActiveSpeakerInfo
import com.vonage.android.kotlin.internal.ActiveSpeakerTracker
import com.vonage.android.kotlin.sdk.VonageSubscriber
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for active speaker fallback behaviour in [Call].
 *
 * Extracted from [CallTest] to keep both classes within detekt's [LargeClass] threshold.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CallActiveSpeakerTest : CallTestBase() {

    @Test
    fun `activeSpeaker should be null initially before any participant joins`() =
        runTest(testDispatcher) {
            val call = createCall()
            // No participants yet — latestJoinerFallback returns null
            assertNull(call.activeSpeaker.value)
        }

    @Test
    fun `activeSpeaker should default to publisher as latest joiner after connecting`() =
        runTest(testDispatcher) {
            val call = createCall()

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected
                callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
                runCurrent()

                assertEquals(
                    "Publisher should be the fallback when no one is speaking",
                    mockPublisherState,
                    call.activeSpeaker.value,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `activeSpeaker should default to latest joiner when no one is speaking`() =
        runTest(testDispatcher) {
            val olderStream = createVonageStream("sub-older", "Alice", creationTime = 2000L)
            val newerStream = createVonageStream("sub-newer", "Bob", creationTime = 3000L)
            val olderSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { stream } returns olderStream
            }
            val newerSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { stream } returns newerStream
            }
            every { mockSession.subscribe(any(), eq(olderStream)) } returns olderSubscriber
            every { mockSession.subscribe(any(), eq(newerStream)) } returns newerSubscriber

            val call = createCall()

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                capturedSessionListener!!.onStreamReceived(olderStream)
                runCurrent()
                awaitItem() // StreamReceived older

                capturedSessionListener!!.onStreamReceived(newerStream)
                runCurrent()
                awaitItem() // StreamReceived newer

                callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
                runCurrent()

                assertEquals(
                    "Latest joiner (newest creationTime) should be the fallback",
                    "sub-newer",
                    call.activeSpeaker.value?.id,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `activeSpeaker fallback should update when a newer participant joins`() =
        runTest(testDispatcher) {
            val firstStream = createVonageStream("sub-first", "Alice", creationTime = 2000L)
            val laterStream = createVonageStream("sub-later", "Bob", creationTime = 5000L)
            val firstSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { stream } returns firstStream
            }
            val laterSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { stream } returns laterStream
            }
            every { mockSession.subscribe(any(), eq(firstStream)) } returns firstSubscriber
            every { mockSession.subscribe(any(), eq(laterStream)) } returns laterSubscriber

            val call = createCall()

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                capturedSessionListener!!.onStreamReceived(firstStream)
                runCurrent()
                awaitItem()
                callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
                runCurrent()
                assertEquals("sub-first", call.activeSpeaker.value?.id)

                // A newer participant joins — fallback should switch to them
                capturedSessionListener!!.onStreamReceived(laterStream)
                runCurrent()
                awaitItem()
                callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
                runCurrent()

                assertEquals(
                    "Fallback should update to the newer joiner",
                    "sub-later",
                    call.activeSpeaker.value?.id,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `activeSpeaker fallback should revert to next latest joiner when fallback participant leaves`() =
        runTest(testDispatcher) {
            val olderStream = createVonageStream("sub-older", "Alice", creationTime = 2000L)
            val newerStream = createVonageStream("sub-newer", "Bob", creationTime = 3000L)
            val olderSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { stream } returns olderStream
            }
            val newerSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { stream } returns newerStream
            }
            every { mockSession.subscribe(any(), eq(olderStream)) } returns olderSubscriber
            every { mockSession.subscribe(any(), eq(newerStream)) } returns newerSubscriber

            val call = createCall()

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                capturedSessionListener!!.onStreamReceived(olderStream)
                runCurrent()
                awaitItem()
                capturedSessionListener!!.onStreamReceived(newerStream)
                runCurrent()
                awaitItem()
                callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
                runCurrent()
                assertEquals("sub-newer should be initial fallback", "sub-newer", call.activeSpeaker.value?.id)

                // The newest participant leaves
                capturedSessionListener!!.onStreamDropped(newerStream)
                runCurrent()
                awaitItem()
                callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
                runCurrent()

                assertEquals(
                    "Fallback should revert to the next latest joiner after newest leaves",
                    "sub-older",
                    call.activeSpeaker.value?.id,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `activeSpeaker should not be audio-promoted when camera is off`() =
        runTest(testDispatcher) {
            // extraBufferCapacity = 1 so tryEmit can buffer without suspending (avoids a
            // deadlock between the test-body coroutine and the callDispatcher collector).
            val mockActiveSpeakerChanges = MutableSharedFlow<ActiveSpeakerChangedPayload>(
                extraBufferCapacity = 1,
            )
            val mockCurrentActiveSpeaker = MutableStateFlow(ActiveSpeakerInfo(null, 0F))
            val mockTracker = mockk<ActiveSpeakerTracker>(relaxed = true) {
                every { activeSpeakerChanges } returns mockActiveSpeakerChanges
                every { currentActiveSpeaker } returns mockCurrentActiveSpeaker
            }
            val call = createCallWithTracker(mockTracker)

            // Camera-on speaker added first (lower creationTime).
            val cameraOnStream = createVonageStream("sub-camera-on", "CameraOn", hasVideo = true, creationTime = 2000L)
            val cameraOnSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns cameraOnStream
            }
            // Camera-off participant added second (higher creationTime, becomes latestJoinerFallback).
            val cameraOffStream = createVonageStream("sub-camera-off", "NoCamera", hasVideo = false, creationTime = 3000L)
            val cameraOffSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns cameraOffStream
            }
            every { mockSession.subscribe(any(), eq(cameraOnStream)) } returns cameraOnSubscriber
            every { mockSession.subscribe(any(), eq(cameraOffStream)) } returns cameraOffSubscriber

            // Start collecting in backgroundScope so capturedSessionListener is set eagerly.
            backgroundScope.launch { call.connect(mockContext).collect { } }

            capturedSessionListener!!.onConnected()
            Thread.sleep(200)
            callScheduler.runCurrent()

            // Subscribe both participants.
            capturedSessionListener!!.onStreamReceived(cameraOnStream)
            Thread.sleep(200)
            callScheduler.runCurrent()

            capturedSessionListener!!.onStreamReceived(cameraOffStream)
            Thread.sleep(200)
            callScheduler.runCurrent()

            // activeSpeaker is now the camera-off joiner via latestJoinerFallback (expected —
            // the fallback path is allowed regardless of camera state so the spotlight is never blank).
            assertEquals("sub-camera-off", call.activeSpeaker.value?.id)

            // The camera-on participant is now speaking. Promote them so there is a known
            // real speaker before we test the camera-off guard.
            assertTrue(
                mockActiveSpeakerChanges.tryEmit(
                    ActiveSpeakerChangedPayload(
                        previousActiveSpeaker = ActiveSpeakerInfo(null, 0f),
                        newActiveSpeaker      = ActiveSpeakerInfo("sub-camera-on", 0.8f),
                    ),
                ),
            )
            callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
            callScheduler.runCurrent()
            assertEquals("sub-camera-on", call.activeSpeaker.value?.id)

            // Now the camera-off participant emits an audio-level event that would normally
            // cause a speaker switch. The guard must block this promotion.
            assertTrue(
                mockActiveSpeakerChanges.tryEmit(
                    ActiveSpeakerChangedPayload(
                        previousActiveSpeaker = ActiveSpeakerInfo("sub-camera-on", 0.8f),
                        newActiveSpeaker      = ActiveSpeakerInfo("sub-camera-off", 0.9f),
                    ),
                ),
            )
            callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
            callScheduler.runCurrent()

            // Camera-off participant must NOT be promoted; active speaker stays the camera-on participant.
            assertEquals(
                "Camera-off participant must not be audio-promoted to active speaker",
                "sub-camera-on",
                call.activeSpeaker.value?.id,
            )
        }

    @Test
    fun `activeSpeaker should revert to latest joiner fallback when real speaker stream is destroyed`() =
        runTest(testDispatcher) {
            val mockActiveSpeakerChanges = MutableSharedFlow<ActiveSpeakerChangedPayload>(
                extraBufferCapacity = 1,
            )
            val mockCurrentActiveSpeaker = MutableStateFlow(ActiveSpeakerInfo(null, 0F))
            val mockTracker = mockk<ActiveSpeakerTracker>(relaxed = true) {
                every { activeSpeakerChanges } returns mockActiveSpeakerChanges
                every { currentActiveSpeaker } returns mockCurrentActiveSpeaker
            }
            val stream = createVonageStream("sub-speaker", "Alice", creationTime = 2000L)
            val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns stream
            }
            every { mockSession.subscribe(any(), any()) } returns mockSubscriber

            val call = createCallWithTracker(mockTracker)
            backgroundScope.launch { call.connect(mockContext).collect { } }

            capturedSessionListener!!.onConnected()
            Thread.sleep(200)
            callScheduler.runCurrent()

            capturedSessionListener!!.onStreamReceived(stream)
            Thread.sleep(200)
            callScheduler.runCurrent()

            // Promote Alice to active speaker
            assertTrue(
                mockActiveSpeakerChanges.tryEmit(
                    ActiveSpeakerChangedPayload(
                        previousActiveSpeaker = ActiveSpeakerInfo(null, 0F),
                        newActiveSpeaker = ActiveSpeakerInfo("sub-speaker", 0.8F),
                    ),
                ),
            )
            callScheduler.runCurrent()
            callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
            callScheduler.runCurrent()
            assertEquals("sub-speaker", call.activeSpeaker.value?.id)

            // Alice's stream is destroyed — tracker resets to null
            mockCurrentActiveSpeaker.value = ActiveSpeakerInfo(null, 0F)
            capturedSessionListener!!.onStreamDropped(stream)
            Thread.sleep(200)
            callScheduler.runCurrent()
            callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
            callScheduler.runCurrent()

            assertEquals(
                "After real speaker leaves, fallback should be publisher (only remaining participant)",
                Call.PUBLISHER_ID,
                call.activeSpeaker.value?.id,
            )
        }

    @Test
    fun `activeSpeaker should return to fallback when tracker signals silence`() =
        runTest(testDispatcher) {
            val mockActiveSpeakerChanges = MutableSharedFlow<ActiveSpeakerChangedPayload>(
                extraBufferCapacity = 1,
            )
            val mockCurrentActiveSpeaker = MutableStateFlow(ActiveSpeakerInfo(null, 0F))
            val mockTracker = mockk<ActiveSpeakerTracker>(relaxed = true) {
                every { activeSpeakerChanges } returns mockActiveSpeakerChanges
                every { currentActiveSpeaker } returns mockCurrentActiveSpeaker
            }
            val stream = createVonageStream("sub-speaker", "Alice", creationTime = 2000L)
            val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns stream
            }
            every { mockSession.subscribe(any(), any()) } returns mockSubscriber

            val call = createCallWithTracker(mockTracker)
            backgroundScope.launch { call.connect(mockContext).collect { } }

            capturedSessionListener!!.onConnected()
            Thread.sleep(200)
            callScheduler.runCurrent()

            capturedSessionListener!!.onStreamReceived(stream)
            Thread.sleep(200)
            callScheduler.runCurrent()

            // Promote Alice to active speaker
            assertTrue(
                mockActiveSpeakerChanges.tryEmit(
                    ActiveSpeakerChangedPayload(
                        previousActiveSpeaker = ActiveSpeakerInfo(null, 0F),
                        newActiveSpeaker = ActiveSpeakerInfo("sub-speaker", 0.8F),
                    ),
                ),
            )
            callScheduler.runCurrent()
            callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
            callScheduler.runCurrent()
            assertEquals("sub-speaker", call.activeSpeaker.value?.id)

            // Audio goes silent — tracker signals nobody speaking (stream stays connected)
            mockCurrentActiveSpeaker.value = ActiveSpeakerInfo(null, 0F)
            callScheduler.runCurrent()
            callScheduler.advanceTimeBy(activeSpeakerDebounceMillis)
            callScheduler.runCurrent()

            assertEquals(
                "After silence, fallback should be the latest joiner (Alice, creationTime=2000)",
                "sub-speaker",
                call.activeSpeaker.value?.id,
            )
        }
}
