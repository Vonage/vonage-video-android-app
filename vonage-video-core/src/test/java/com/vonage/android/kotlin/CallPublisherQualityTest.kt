package com.vonage.android.kotlin

import app.cash.turbine.test
import com.vonage.android.kotlin.internal.PublisherVideoQualityPolicy
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/** Wiring of [PublisherVideoQualityPolicy] inside [Call]. */
@OptIn(ExperimentalCoroutinesApi::class)
class CallPublisherQualityTest : CallTestBase() {

    private val isTalking = MutableStateFlow(false)
    private val bwSaver = VideoBitrateConfig(VideoBitratePreset.BW_SAVER, maxBitrate = null)
    private val extraBwSaver = VideoBitrateConfig(VideoBitratePreset.EXTRA_BW_SAVER, maxBitrate = null)

    @BeforeEach
    fun stubPublisher() {
        every { mockPublisherState.isTalking } returns isTalking
        every { mockPublisherFactory.currentConfig } returns null
        every { mockSession.subscribe(any(), any()) } answers {
            val stream = secondArg<VonageStream>()
            mockk<VonageSubscriber>(relaxed = true) { every { this@mockk.stream } returns stream }
        }
    }

    @Test
    fun `given_smallRoom_THEN_bitrateNotTouched`() = runTest(testDispatcher) {
        val call = createCall()
        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            addRemotes(2)

            verify(exactly = 0) { mockPublisherState.applyVideoBitrate(any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given_roomGrows_THEN_bwSaverThenExtraBwSaverApplied`() = runTest(testDispatcher) {
        val call = createCall()
        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()

            addRemotes(3)
            verify(exactly = 1) { mockPublisherState.applyVideoBitrate(bwSaver) }

            addRemotes(4, startIndex = 3)
            verify(exactly = 1) { mockPublisherState.applyVideoBitrate(extraBwSaver) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given_largeRoom_WHEN_userSpeaks_THEN_userConfigRestoredUntilHoldExpires`() = runTest(testDispatcher) {
        val call = createCall()
        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            addRemotes(3)

            isTalking.value = true
            runCurrent()
            verify(exactly = 1) { mockPublisherState.applyVideoBitrate(VideoBitrateConfig()) }

            isTalking.value = false
            callScheduler.advanceTimeBy(PublisherVideoQualityPolicy.SPEAKING_HOLD_MILLIS + 1)
            verify(exactly = 2) { mockPublisherState.applyVideoBitrate(bwSaver) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given_userPicksCustomBitrate_THEN_appliedAndNeverOverridden`() = runTest(testDispatcher) {
        val call = createCall()
        val custom = VideoBitrateConfig(VideoBitratePreset.CUSTOM, maxBitrate = 800_000)
        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()

            call.setVideoBitrate(custom)
            runCurrent()
            addRemotes(8)

            verify(exactly = 1) { mockPublisherState.applyVideoBitrate(custom) }
            verify(exactly = 0) { mockPublisherState.applyVideoBitrate(bwSaver) }
            verify(exactly = 0) { mockPublisherState.applyVideoBitrate(extraBwSaver) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun addRemotes(count: Int, startIndex: Int = 0) {
        repeat(count) { i ->
            capturedSessionListener!!.onStreamReceived(
                createVonageStream("stream-${startIndex + i}", "User ${startIndex + i}"),
            )
            runCurrent()
        }
    }
}
