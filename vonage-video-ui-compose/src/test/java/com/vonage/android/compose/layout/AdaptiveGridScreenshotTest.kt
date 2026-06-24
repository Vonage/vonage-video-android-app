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
class AdaptiveGridScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = SnapshotTestDefaults.OPTIONS

    @Test
    fun adaptiveGrid_4participants_light() {
        composeTestRule.setContent { AdaptiveGrid4ParticipantsPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun adaptiveGrid_4participants_dark() {
        composeTestRule.setContent { AdaptiveGrid4ParticipantsPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun adaptiveGrid_7participants_overflow_light() {
        composeTestRule.setContent { AdaptiveGrid7ParticipantsOverflowPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun adaptiveGrid_7participants_overflow_dark() {
        composeTestRule.setContent { AdaptiveGrid7ParticipantsOverflowPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "land")
    fun adaptiveGrid_4participants_landscape_light() {
        composeTestRule.setContent { AdaptiveGrid4ParticipantsPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+land-night")
    fun adaptiveGrid_4participants_landscape_dark() {
        composeTestRule.setContent { AdaptiveGrid4ParticipantsPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
