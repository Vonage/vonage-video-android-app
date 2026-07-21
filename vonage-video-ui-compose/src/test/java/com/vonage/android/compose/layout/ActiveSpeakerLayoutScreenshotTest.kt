package com.vonage.android.compose.layout

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.vonage.android.compose.SnapshotTestDefaults
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ActiveSpeakerLayoutScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = SnapshotTestDefaults.OPTIONS

    @Test
    fun activeSpeakerLayout_noSpeaker_light() {
        composeTestRule.setContent { ActiveSpeakerLayoutNoSpeakerPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun activeSpeakerLayout_noSpeaker_dark() {
        composeTestRule.setContent { ActiveSpeakerLayoutNoSpeakerPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun activeSpeakerLayout_filledSpotlight_light() {
        composeTestRule.setContent { ActiveSpeakerLayoutFilledSpotlightPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun activeSpeakerLayout_filledSpotlight_dark() {
        composeTestRule.setContent { ActiveSpeakerLayoutFilledSpotlightPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun activeSpeakerLayout_filmstripOverflow_light() {
        composeTestRule.setContent { ActiveSpeakerLayoutFilmstripOverflowPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun activeSpeakerLayout_filmstripOverflow_dark() {
        composeTestRule.setContent { ActiveSpeakerLayoutFilmstripOverflowPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "land")
    fun activeSpeakerLayout_landscape_light() {
        composeTestRule.setContent { ActiveSpeakerLayoutFilledSpotlightPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+land-night")
    fun activeSpeakerLayout_landscape_dark() {
        composeTestRule.setContent { ActiveSpeakerLayoutFilledSpotlightPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun activeSpeakerLayout_singleParticipant_light() {
        composeTestRule.setContent { ActiveSpeakerLayoutSingleParticipantPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun activeSpeakerLayout_singleParticipant_dark() {
        composeTestRule.setContent { ActiveSpeakerLayoutSingleParticipantPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
