package com.vonage.android.kotlin.model

import com.vonage.android.kotlin.internal.SubscriberVideoQualityPolicy
import com.vonage.android.kotlin.internal.SubscriberVideoTier
import com.vonage.android.kotlin.sdk.VonageConnection
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import com.vonage.android.kotlin.sdk.VonageSubscriberVideoStatsEntry
import com.vonage.android.kotlin.sdk.VonageSubscriberVideoStatsListener
import com.vonage.android.kotlin.sdk.VonageVideoSize
import com.vonage.android.kotlin.sdk.VonageVideoType
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ParticipantStateVideoQualityTest {

    private val settle = SubscriberVideoQualityPolicy.SETTLE_MILLIS

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

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

    @Test
    fun `given_portraitStatsArriveAfterLayout_THEN_tierReevaluatedWithPortraitLayer`() = runTest {
        val (subscriber, state) = startState()
        // Assumed 16:9 in a 300x600 view: fitted short edge 169 px -> THUMBNAIL.
        state.onRenderedSizeChanged(300, 600)
        advanceTimeBy(settle + 1)
        assertEquals(SubscriberVideoTier.THUMBNAIL, state.videoTier.value)

        // Real 9:16 source fills the view: fitted short edge 300 px -> MEDIUM.
        emitStats(width = 360, height = 640)
        advanceTimeBy(settle + 1)

        verify { subscriber.setPreferredResolution(VonageVideoSize(360, 640)) }
        assertEquals(SubscriberVideoTier.MEDIUM, state.videoTier.value)
    }

    @Test
    fun `given_layerSwitchWithSameAspect_THEN_tierNotReevaluated`() = runTest {
        val (subscriber, state) = startState()
        emitStats(width = 1280, height = 720)
        state.onRenderedSizeChanged(320, 180)
        advanceTimeBy(settle + 1)

        emitStats(width = 320, height = 180)
        advanceTimeBy(settle + 1)

        verify(exactly = 1) { subscriber.setPreferredResolution(any()) }
        assertEquals(SubscriberVideoTier.THUMBNAIL, state.videoTier.value)
    }

    @Test
    fun `given_sourceRotatesWithSameTier_THEN_layerOrientationUpdated`() = runTest {
        val (subscriber, state) = startState()
        // Square 400 px view: both 16:9 and 9:16 fit with a 225 px short edge -> THUMBNAIL.
        emitStats(width = 1280, height = 720)
        state.onRenderedSizeChanged(400, 400)
        advanceTimeBy(settle + 1)
        verify { subscriber.setPreferredResolution(VonageVideoSize(320, 180)) }

        emitStats(width = 720, height = 1280)
        advanceTimeBy(settle + 1)

        verify { subscriber.setPreferredResolution(VonageVideoSize(180, 320)) }
        assertEquals(SubscriberVideoTier.THUMBNAIL, state.videoTier.value)
    }

    private var statsListener: VonageSubscriberVideoStatsListener? = null

    private fun TestScope.emitStats(width: Int, height: Int) {
        statsListener!!.onVideoStats(
            VonageSubscriberVideoStatsEntry(
                videoPacketsReceived = 0,
                videoPacketsLost = 0,
                videoBytesReceived = 0,
                width = width,
                height = height,
                codec = "VP8",
                decodedFrameRate = 30.0,
                bitrate = 0L,
                freezeCount = 0L,
                totalFreezesDuration = 0L,
                estimatedBandwidthInBps = null,
            ),
        )
        runCurrent()
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
            val listener = slot<VonageSubscriberVideoStatsListener>()
            every { setVideoStatsListener(capture(listener)) } answers { statsListener = listener.captured }
        }
        val state = ParticipantState(vonageSubscriber = subscriber)
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        backgroundScope.launch { state.setup() }
        runCurrent()
        return subscriber to state
    }
}
