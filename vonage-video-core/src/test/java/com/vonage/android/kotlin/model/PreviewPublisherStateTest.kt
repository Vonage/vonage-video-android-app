package com.vonage.android.kotlin.model

import android.view.View
import com.vonage.android.kotlin.sdk.VonageBlurLevel
import com.vonage.android.kotlin.sdk.VonagePublisher
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [PreviewPublisherState.toggleVideo].
 *
 * Key invariant: toggling the camera back ON must reapply the active [VideoEffect]
 * to the SDK, because the OpenTok capture pipeline clears transformers on restart.
 */
class PreviewPublisherStateTest {

    private var publishVideoState: Boolean = true

    private lateinit var mockPublisher: VonagePublisher
    private lateinit var previewPublisherState: PreviewPublisherState

    @Before
    fun setUp() {
        publishVideoState = true
        mockPublisher = mockk(relaxed = true) {
            every { publishVideo } answers { publishVideoState }
            every { publishVideo = any() } answers { publishVideoState = firstArg() }
            every { view } returns mockk<View>(relaxed = true)
        }
        previewPublisherState = PreviewPublisherState(vonagePublisher = mockPublisher)
    }

    @Test
    fun `toggleVideo turns camera off then back on`() {
        assertTrue(publishVideoState)

        previewPublisherState.toggleVideo()
        assertFalse(publishVideoState)

        previewPublisherState.toggleVideo()
        assertTrue(publishVideoState)
    }

    @Test
    fun `toggleVideo reapplies background image effect when camera is turned back on`() {
        val effect = VideoEffect.BackgroundImage(id = "bg1", imagePath = "/path/to/image.jpg")
        previewPublisherState.applyVideoEffect(effect)

        previewPublisherState.toggleVideo() // off
        previewPublisherState.toggleVideo() // on

        verify(exactly = 2) { mockPublisher.applyBackgroundImage("/path/to/image.jpg") }
    }

    @Test
    fun `toggleVideo does not reapply effect when camera is turned off`() {
        val effect = VideoEffect.BackgroundImage(id = "bg1", imagePath = "/path/to/image.jpg")
        previewPublisherState.applyVideoEffect(effect)

        previewPublisherState.toggleVideo() // off only

        verify(exactly = 1) { mockPublisher.applyBackgroundImage("/path/to/image.jpg") }
    }

    @Test
    fun `toggleVideo reapplies blur effect when camera is turned back on`() {
        previewPublisherState.applyVideoEffect(VideoEffect.BlurHigh)

        previewPublisherState.toggleVideo() // off
        previewPublisherState.toggleVideo() // on

        verify(exactly = 2) { mockPublisher.applyBlur(VonageBlurLevel.HIGH) }
    }

    @Test
    fun `toggleVideo with no effect does not call unnecessary SDK methods on re-enable`() {
        previewPublisherState.toggleVideo() // off
        previewPublisherState.toggleVideo() // on

        verify(exactly = 1) { mockPublisher.applyBlur(VonageBlurLevel.NONE) }
    }
}
