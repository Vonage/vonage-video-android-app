package com.vonage.android.kotlin.model

import com.vonage.android.kotlin.sdk.VonageConnection
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import com.vonage.android.kotlin.sdk.VonageSubscriberVideoListener
import com.vonage.android.kotlin.sdk.VonageVideoType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [ParticipantState] covering the four subscriber-state bugs:
 *
 * - Bug A: [ParticipantState.updateStreamProperties] must update both
 *   [ParticipantState.isCameraEnabled] and [ParticipantState.isMicEnabled] from
 *   a session-level stream-property change event.
 * - Bug B: [VonageSubscriberVideoListener.onVideoDisabled] /
 *   [VonageSubscriberVideoListener.onVideoEnabled] with reason "subscribe" (local
 *   scroll-optimisation) must NOT mutate [ParticipantState.isCameraEnabled].
 * - Bug C: [ParticipantState.changeVisibility] with visible=true must use the live
 *   [ParticipantState.isCameraEnabled] value, not the stale [VonageStream.hasVideo] snapshot.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ParticipantStateTest {

    /** Set by the mock when [ParticipantState.setup] registers the video listener. */
    private var capturedVideoListener: VonageSubscriberVideoListener? = null

    @Before
    fun setUp() {
        capturedVideoListener = null
    }

    // region Bug C — changeVisibility uses live _isCameraEnabled, not stale stream snapshot

    @Test
    fun `given_cameraOff_THEN_changeVisibilityTrue_setsSubscribeToVideoFalse`() {
        val subscriber = buildMockSubscriber(hasVideo = false)
        val state = ParticipantState(vonageSubscriber = subscriber)

        state.changeVisibility(true)

        // Must not attempt to decode video for a camera-off participant.
        verify { subscriber.subscribeToVideo = false }
    }

    @Test
    fun `given_cameraOn_THEN_changeVisibilityTrue_setsSubscribeToVideoTrue`() {
        val subscriber = buildMockSubscriber(hasVideo = true)
        val state = ParticipantState(vonageSubscriber = subscriber)

        state.changeVisibility(true)

        verify { subscriber.subscribeToVideo = true }
    }

    @Test
    fun `given_changeVisibilityFalse_THEN_subscribeToVideoFalse_regardlessCameraState`() {
        val subscriberCameraOn = buildMockSubscriber(hasVideo = true)
        val subscriberCameraOff = buildMockSubscriber(hasVideo = false)
        val stateOn = ParticipantState(vonageSubscriber = subscriberCameraOn)
        val stateOff = ParticipantState(vonageSubscriber = subscriberCameraOff)

        stateOn.changeVisibility(false)
        stateOff.changeVisibility(false)

        verify { subscriberCameraOn.subscribeToVideo = false }
        verify { subscriberCameraOff.subscribeToVideo = false }
    }

    /**
     * Core regression for Bug C: the subscriber joins with camera on (stream snapshot
     * hasVideo=true), the remote publisher disables camera (updateStreamProperties updates
     * _isCameraEnabled to false), and then the visibility system hides then re-shows the tile.
     * changeVisibility(true) must not re-enable video decoding by reading the stale snapshot.
     */
    @Test
    fun `given_cameraDisabledAfterSubscribe_THEN_changeVisibilityTrue_doesNotRestoreVideo`() {
        val subscriber = buildMockSubscriber(hasVideo = true)
        val state = ParticipantState(vonageSubscriber = subscriber)

        // Simulate the stream-property change arriving after subscription (Bug A path).
        state.updateStreamProperties(hasVideo = false, hasAudio = true)

        // Visibility system hides the tile then re-shows it.
        state.changeVisibility(false)
        state.changeVisibility(true)

        // The second changeVisibility(true) reads _isCameraEnabled.value (false), not
        // the stale stream snapshot (hasVideo=true from construction).
        verify { subscriber.subscribeToVideo = false }
    }

    // endregion

    // region Bug A — updateStreamProperties updates isCameraEnabled and isMicEnabled

    @Test
    fun `given_updateStreamProperties_THEN_bothStateFlowsUpdate`() {
        val state = ParticipantState(
            vonageSubscriber = buildMockSubscriber(hasVideo = true, hasAudio = true),
        )

        assertTrue(state.isCameraEnabled.value)
        assertTrue(state.isMicEnabled.value)

        state.updateStreamProperties(hasVideo = false, hasAudio = false)

        assertFalse(state.isCameraEnabled.value)
        assertFalse(state.isMicEnabled.value)
    }

    @Test
    fun `given_updateStreamPropertiesRestored_THEN_bothStateFlowsTrue`() {
        val state = ParticipantState(
            vonageSubscriber = buildMockSubscriber(hasVideo = false, hasAudio = false),
        )

        state.updateStreamProperties(hasVideo = true, hasAudio = true)

        assertTrue(state.isCameraEnabled.value)
        assertTrue(state.isMicEnabled.value)
    }

    @Test
    fun `given_updateStreamProperties_THEN_onlyCameraChanges`() {
        val state = ParticipantState(
            vonageSubscriber = buildMockSubscriber(hasVideo = true, hasAudio = true),
        )

        state.updateStreamProperties(hasVideo = false, hasAudio = true)

        assertFalse(state.isCameraEnabled.value)
        assertTrue(state.isMicEnabled.value)
    }

    @Test
    fun `given_updateStreamProperties_THEN_onlyAudioChanges`() {
        val state = ParticipantState(
            vonageSubscriber = buildMockSubscriber(hasVideo = true, hasAudio = true),
        )

        state.updateStreamProperties(hasVideo = true, hasAudio = false)

        assertTrue(state.isCameraEnabled.value)
        assertFalse(state.isMicEnabled.value)
    }

    // endregion

    // region Bug B — VonageSubscriberVideoListener reason filtering

    /**
     * "subscribe" reason fires when [com.vonage.android.kotlin.model.Participant.changeVisibility]
     * sets subscribeToVideo = false as a bandwidth optimisation. It must NOT be treated as
     * the remote publisher disabling their camera.
     */
    @Test
    fun `given_videoDisabledWithReasonSubscribe_THEN_isCameraEnabledUnchanged`() =
        runTest(UnconfinedTestDispatcher()) {
            val state = buildStateWithSetup(scope = backgroundScope, hasVideo = true)

            capturedVideoListener!!.onVideoDisabled("subscribe")

            assertTrue(
                "isCameraEnabled must remain true when reason='subscribe' (local optimisation)",
                state.isCameraEnabled.value,
            )
        }

    @Test
    fun `given_videoDisabledWithReasonPublishVideo_THEN_isCameraEnabledFalse`() =
        runTest(UnconfinedTestDispatcher()) {
            val state = buildStateWithSetup(scope = backgroundScope, hasVideo = true)

            capturedVideoListener!!.onVideoDisabled("publishVideo")

            assertFalse(state.isCameraEnabled.value)
        }

    @Test
    fun `given_videoDisabledWithReasonQuality_THEN_isCameraEnabledFalse`() =
        runTest(UnconfinedTestDispatcher()) {
            val state = buildStateWithSetup(scope = backgroundScope, hasVideo = true)

            capturedVideoListener!!.onVideoDisabled("quality")

            assertFalse(state.isCameraEnabled.value)
        }

    /**
     * "subscribe" reason fires when subscribeToVideo is re-enabled after a scroll-back event.
     * It must NOT reset isCameraEnabled to true if the remote publisher's camera is still off.
     */
    @Test
    fun `given_videoEnabledWithReasonSubscribe_THEN_isCameraEnabledUnchanged`() =
        runTest(UnconfinedTestDispatcher()) {
            // Start with camera already off (e.g. publisher joined with camera off).
            val state = buildStateWithSetup(scope = backgroundScope, hasVideo = false)

            capturedVideoListener!!.onVideoEnabled("subscribe")

            assertFalse(
                "isCameraEnabled must remain false when reason='subscribe' (local optimisation)",
                state.isCameraEnabled.value,
            )
        }

    @Test
    fun `given_videoEnabledWithReasonPublishVideo_THEN_isCameraEnabledTrue`() =
        runTest(UnconfinedTestDispatcher()) {
            val state = buildStateWithSetup(scope = backgroundScope, hasVideo = false)

            capturedVideoListener!!.onVideoEnabled("publishVideo")

            assertTrue(state.isCameraEnabled.value)
        }

    // endregion

    // region Helpers

    private fun buildMockSubscriber(
        hasVideo: Boolean = true,
        hasAudio: Boolean = true,
    ): VonageSubscriber {
        val stream = VonageStream(
            streamId = "stream-test",
            name = "Test User",
            connection = VonageConnection(connectionId = "conn-test"),
            creationTime = 0L,
            videoType = VonageVideoType.CAMERA,
            hasVideo = hasVideo,
            hasAudio = hasAudio,
        )
        return mockk(relaxed = true) {
            every { this@mockk.stream } returns stream
            every { setVideoListener(any()) } answers {
                capturedVideoListener = firstArg()
            }
        }
    }

    /**
     * Builds a [ParticipantState] and launches [ParticipantState.setup] in the given [scope]
     * so that the [VonageSubscriberVideoListener] is registered before this function returns.
     *
     * With [UnconfinedTestDispatcher], the launched coroutine runs eagerly on the calling
     * thread until its first suspension point (inside the audio-level `coroutineScope` block).
     * By that point all synchronous listener-registration calls in [ParticipantState.setup]
     * have already executed and [capturedVideoListener] is populated.
     *
     * The launched job is a child of [scope] and will be automatically cancelled when the
     * test completes.
     */
    private fun buildStateWithSetup(scope: CoroutineScope, hasVideo: Boolean): ParticipantState {
        val subscriber = buildMockSubscriber(hasVideo = hasVideo)
        val state = ParticipantState(vonageSubscriber = subscriber)
        scope.launch { state.setup() }
        checkNotNull(capturedVideoListener) {
            "setup() did not call setVideoListener — listener was not captured. " +
                "Ensure the test runs with UnconfinedTestDispatcher."
        }
        return state
    }

    // endregion
}
