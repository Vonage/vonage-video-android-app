package com.vonage.android.kotlin.internal

import app.cash.turbine.test
import com.vonage.android.kotlin.internal.SubscriberVideoTier.FULL
import com.vonage.android.kotlin.internal.SubscriberVideoTier.MEDIUM
import com.vonage.android.kotlin.internal.SubscriberVideoTier.THUMBNAIL
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.sdk.VonageVideoSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoQualityPolicyTest {

    // region Subscriber

    /** Tier for a 16:9 landscape source drawn in a [w]x[h] px view. */
    private fun tier(w: Int, h: Int, current: SubscriberVideoTier = FULL, screen: Boolean = false) =
        SubscriberVideoQualityPolicy.tierFor(
            viewWidth = w,
            viewHeight = h,
            sourceWidth = 1280,
            sourceHeight = 720,
            isScreenShare = screen,
            current = current,
        )

    @Test
    fun `given_thumbnailSizedView_THEN_thumbnailTier`() {
        // 320x180 view -> fitted short edge 180 <= 240
        assertEquals(THUMBNAIL, tier(320, 180))
    }

    @Test
    fun `given_mediumSizedView_THEN_mediumTier`() {
        // 960x540 view -> fitted short edge 540 <= 540
        assertEquals(MEDIUM, tier(960, 540, current = THUMBNAIL))
    }

    @Test
    fun `given_fullScreenView_THEN_fullTier`() {
        assertEquals(FULL, tier(2400, 1080))
    }

    @Test
    fun `given_portraitPhoneFullScreen_THEN_letterboxedVideoDecidesTier`() {
        // 1080x2400 portrait view: a 16:9 video is letterboxed to 1080x608 -> short edge 608 -> FULL
        assertEquals(FULL, tier(1080, 2400, current = THUMBNAIL))
        // 720x1600 view: letterboxed to 720x405 -> MEDIUM
        assertEquals(MEDIUM, tier(720, 1600, current = THUMBNAIL))
    }

    @Test
    fun `given_screenShare_THEN_alwaysFullTier`() {
        assertEquals(FULL, tier(160, 90, current = THUMBNAIL, screen = true))
    }

    @Test
    fun `given_zeroSize_THEN_tierUnchanged`() {
        assertEquals(MEDIUM, tier(0, 0, current = MEDIUM))
    }

    @Test
    fun `given_sizeGrows_THEN_upgradesImmediately`() {
        // Just above the thumbnail threshold upgrades without margin
        assertEquals(MEDIUM, tier(450, 241, current = THUMBNAIL))
        assertEquals(FULL, tier(980, 551, current = MEDIUM))
    }

    @Test
    fun `given_sizeShrinksWithinMargin_THEN_keepsCurrentTier`() {
        // 230 px is below 240 but within the 15% margin (204 px)
        assertEquals(MEDIUM, tier(409, 230, current = MEDIUM))
        // 500 px is below 540 but within the margin (459 px)
        assertEquals(FULL, tier(889, 500, current = FULL))
    }

    @Test
    fun `given_sizeShrinksPastMargin_THEN_downgrades`() {
        assertEquals(THUMBNAIL, tier(356, 200, current = MEDIUM))
        assertEquals(MEDIUM, tier(800, 450, current = FULL))
        assertEquals(THUMBNAIL, tier(320, 180, current = FULL))
    }

    @Test
    fun `given_unknownSource_THEN_assumes16by9`() {
        assertEquals(180, SubscriberVideoQualityPolicy.fittedShortEdge(320, 400, 0, 0))
    }

    @Test
    fun `given_portraitSource_THEN_preferredResolutionIsPortrait`() {
        assertEquals(VonageVideoSize(180, 320), THUMBNAIL.preferredResolution(sourceIsPortrait = true))
        assertEquals(VonageVideoSize(640, 360), MEDIUM.preferredResolution(sourceIsPortrait = false))
        assertNull(FULL.preferredResolution(sourceIsPortrait = false))
        assertNull(FULL.frameRate)
    }

    // endregion

    // region Publisher

    private val default = VideoBitrateConfig()

    private fun publisherPreset(remotes: Int, speaking: Boolean = false, user: VideoBitrateConfig = default) =
        PublisherVideoQualityPolicy.effectiveConfig(user, remotes, speaking).preset

    @Test
    fun `given_smallRoom_THEN_userConfigKept`() {
        assertEquals(default, PublisherVideoQualityPolicy.effectiveConfig(default, 2, false))
    }

    @Test
    fun `given_mediumRoom_THEN_bwSaver`() {
        assertEquals(VideoBitratePreset.BW_SAVER, publisherPreset(3))
        assertEquals(VideoBitratePreset.BW_SAVER, publisherPreset(6))
    }

    @Test
    fun `given_largeRoom_THEN_extraBwSaver`() {
        assertEquals(VideoBitratePreset.EXTRA_BW_SAVER, publisherPreset(7))
    }

    @Test
    fun `given_speaking_THEN_userConfigKept`() {
        assertEquals(VideoBitratePreset.DEFAULT, publisherPreset(10, speaking = true))
    }

    @Test
    fun `given_userChoseNonDefault_THEN_neverOverridden`() {
        val custom = VideoBitrateConfig(VideoBitratePreset.CUSTOM, 1_500_000)
        assertEquals(custom, PublisherVideoQualityPolicy.effectiveConfig(custom, 10, false))
    }

    @Test
    fun `given_speakingStops_THEN_falseEmittedOnlyAfterHold`() = runTest {
        val talking = MutableStateFlow(false)
        talking.holdTrueFor(1_000).test {
            assertEquals(false, awaitItem())
            talking.value = true
            assertEquals(true, awaitItem())
            talking.value = false
            testScheduler.advanceTimeBy(500)
            talking.value = true // short pause: no change
            talking.value = false
            expectNoEvents()
            testScheduler.advanceTimeBy(1_001)
            assertEquals(false, awaitItem())
        }
    }

    // endregion
}
