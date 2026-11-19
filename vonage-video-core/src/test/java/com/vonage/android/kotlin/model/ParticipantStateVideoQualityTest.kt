package com.vonage.android.kotlin.model

import com.vonage.android.kotlin.internal.SubscriberVideoQualityPolicy
import com.vonage.android.kotlin.internal.SubscriberVideoTier
import com.vonage.android.kotlin.sdk.VonageConnection
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import com.vonage.android.kotlin.sdk.VonageVideoSize
import com.vonage.android.kotlin.sdk.VonageVideoType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ParticipantStateVideoQualityTest {

    private val settle = SubscriberVideoQualityPolicy.SETTLE_MILLIS

    @Test
    fun `given_noRenderedSize_THEN_noPreferenceIsSet`() = runTest {
        val (subscriber, state) = startState()

        advanceTimeBy(settle * 2)

        assertEquals(SubscriberVideoTier.FULL, state.videoTier.value)
        verify(exactly = 0) { subscriber.setPreferredResolution(any()) }
    }

    @Test
    fun `given_thumbnailSize_THEN_thumbnailLayerAndFpsRequestedAfterSettle`() = runTest {
        val (subscriber, state) = startState()

        state.onRenderedSizeChanged(320, 180)
        advanceTimeBy(settle - 1)
        verify(exactly = 0) { subscriber.setPreferredResolution(any()) }

        advanceTimeBy(2)
        verify { subscriber.setPreferredResolution(VonageVideoSize(320, 180)) }
        verify { subscriber.setPreferredFrameRate(15f) }
        assertEquals(SubscriberVideoTier.THUMBNAIL, state.videoTier.value)
    }

    @Test
    fun `given_thumbnailThenFullScreen_THEN_preferenceIsCleared`() = runTest {
        val (subscriber, state) = startState()
        state.onRenderedSizeChanged(320, 180)
        advanceTimeBy(settle + 1)

        state.onRenderedSizeChanged(2400, 1080)
        advanceTimeBy(settle + 1)

        verify { subscriber.setPreferredResolution(null) }
        verify { subscriber.setPreferredFrameRate(null) }
        assertEquals(SubscriberVideoTier.FULL, state.videoTier.value)
    }

    @Test
    fun `given_rapidResizes_THEN_onlyFinalSizeApplied`() = runTest {
        val (subscriber, state) = startState()

        state.onRenderedSizeChanged(320, 180)
        advanceTimeBy(settle / 2)
        state.onRenderedSizeChanged(640, 360)
        advanceTimeBy(settle + 1)

        verify(exactly = 0) { subscriber.setPreferredResolution(VonageVideoSize(320, 180)) }
        verify(exactly = 1) { subscriber.setPreferredResolution(VonageVideoSize(640, 360)) }
    }

    @Test
    fun `given_screenShare_THEN_neverDowngraded`() = runTest {
        val (subscriber, state) = startState(videoType = VonageVideoType.SCREEN)

        state.onRenderedSizeChanged(160, 90)
        advanceTimeBy(settle + 1)

        assertEquals(SubscriberVideoTier.FULL, state.videoTier.value)
        verify(exactly = 0) { subscriber.setPreferredResolution(any()) }
    }

    private fun TestScope.startState(
        videoType: VonageVideoType = VonageVideoType.CAMERA,
    ): Pair<VonageSubscriber, ParticipantState> {
        val stream = VonageStream(
            streamId = "stream-test",
            name = "Test User",
            connection = VonageConnection(connectionId = "conn-test"),
            creationTime = 0L,
            videoType = videoType,
            hasVideo = true,
            hasAudio = true,
        )
        val subscriber = mockk<VonageSubscriber>(relaxed = true) {
            every { this@mockk.stream } returns stream
        }
        val state = ParticipantState(vonageSubscriber = subscriber)
        backgroundScope.launch { state.setup() }
        runCurrent()
        return subscriber to state
    }
}
