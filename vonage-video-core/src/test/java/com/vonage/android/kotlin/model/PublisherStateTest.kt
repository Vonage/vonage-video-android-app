package com.vonage.android.kotlin.model

import com.vonage.android.kotlin.sdk.VonagePublisher
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [PublisherState.changeVisibility] and [PublisherState.toggleVideo].
 *
 * Key invariant under test: [PublisherState.changeVisibility] must not permanently corrupt
 * the publisher's video state. Specifically, calling changeVisibility(false) followed by
 * changeVisibility(true) must restore publishVideo to the user's intended value — it must
 * NOT read stream.hasVideo, which the OpenTok SDK sets to false when publishVideo is false.
 */
class PublisherStateTest {

    /**
     * Backing store for the mocked publishVideo property.
     * Initialised to true (publisher created with video on).
     */
    private var publishVideoState: Boolean = true

    private lateinit var mockPublisher: VonagePublisher
    private lateinit var publisherState: PublisherState

    @BeforeEach
    fun setUp() {
        publishVideoState = true
        mockPublisher = mockk(relaxed = true) {
            every { publishVideo } answers { publishVideoState }
            every { publishVideo = any() } answers { publishVideoState = firstArg() }
        }
        publisherState = PublisherState(
            publisherId = "publisher",
            vonagePublisher = mockPublisher,
        )
    }

    // region changeVisibility round-trip

    @Test
    fun `changeVisibility false then true restores publishVideo to user intent`() {
        // Precondition: video is on
        assertTrue(publishVideoState)

        // System hides the publisher tile (bandwidth optimisation)
        publisherState.changeVisibility(false)
        assertFalse(publishVideoState)

        // In the real OpenTok SDK, stream.hasVideo would now be false too.
        // The fix must NOT read stream.hasVideo here — it uses userIntendedVideoOn instead.

        // System reveals the publisher tile again
        publisherState.changeVisibility(true)

        // Video must be restored to the original user-intended value (true)
        assertTrue(publishVideoState)
    }

    @Test
    fun `multiple changeVisibility false calls followed by true restores publishVideo`() {
        publisherState.changeVisibility(false)
        publisherState.changeVisibility(false)
        publisherState.changeVisibility(false)

        publisherState.changeVisibility(true)

        assertTrue(publishVideoState)
    }

    @Test
    fun `changeVisibility true does not override an explicit user mute`() {
        // User deliberately turns off their camera
        publisherState.toggleVideo()
        assertFalse(publishVideoState)

        // Visibility system calls changeVisibility(true) — must not re-enable the camera
        publisherState.changeVisibility(true)

        assertFalse(publishVideoState)
    }

    // endregion

    // region toggleVideo intent tracking

    @Test
    fun `toggleVideo turns camera off then back on`() {
        // Initially on
        assertTrue(publishVideoState)

        publisherState.toggleVideo()
        assertFalse(publishVideoState)

        publisherState.toggleVideo()
        assertTrue(publishVideoState)
    }

    @Test
    fun `toggleVideo tracks user intent independently of visibility state`() {
        // Visibility system hides publisher (publishVideo = false, userIntendedVideoOn stays true)
        publisherState.changeVisibility(false)
        assertFalse(publishVideoState)

        // User explicitly turns off (toggles from intent=true → intent=false)
        publisherState.toggleVideo()

        // Camera should remain off and intent is now false
        assertFalse(publishVideoState)

        // changeVisibility(true) must NOT re-enable because user's intent is off
        publisherState.changeVisibility(true)
        assertFalse(publishVideoState)

        // User turns camera back on
        publisherState.toggleVideo()
        assertTrue(publishVideoState)
    }

    // endregion
}
