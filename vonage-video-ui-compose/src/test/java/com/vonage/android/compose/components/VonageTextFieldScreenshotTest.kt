package com.vonage.android.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.vonage.android.compose.SnapshotOutputDirRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class VonageTextFieldScreenshotTest {

    @get:Rule
    val snapshotOutputDirRule = SnapshotOutputDirRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
    )

    @Test
    fun vonageTextFieldFilled_light() {
        composeTestRule.setContent { VonageTextFieldPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun vonageTextFieldFilled_dark() {
        composeTestRule.setContent { VonageTextFieldPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun vonageTextFieldEmpty_light() {
        composeTestRule.setContent { VonageTextFieldEmptyPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun vonageTextFieldEmpty_dark() {
        composeTestRule.setContent { VonageTextFieldEmptyPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun vonageTextFieldError_light() {
        composeTestRule.setContent { VonageTextFieldErrorPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun vonageTextFieldError_dark() {
        composeTestRule.setContent { VonageTextFieldErrorPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
