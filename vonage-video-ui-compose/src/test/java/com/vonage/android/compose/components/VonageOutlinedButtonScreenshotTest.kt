package com.vonage.android.compose.components

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
class VonageOutlinedButtonScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = SnapshotTestDefaults.OPTIONS

    @Test
    fun vonageOutlinedButtonEnabled_light() {
        composeTestRule.setContent { VonageOutlinedButtonPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun vonageOutlinedButtonEnabled_dark() {
        composeTestRule.setContent { VonageOutlinedButtonPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun vonageOutlinedButtonDisabled_light() {
        composeTestRule.setContent { VonageOutlinedButtonDisabledPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun vonageOutlinedButtonDisabled_dark() {
        composeTestRule.setContent { VonageOutlinedButtonDisabledPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun vonageOutlinedButtonWithIcon_light() {
        composeTestRule.setContent { VonageOutlinedButtonWithIconPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun vonageOutlinedButtonWithIcon_dark() {
        composeTestRule.setContent { VonageOutlinedButtonWithIconPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
