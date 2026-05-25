package com.vonage.android.compose.components

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.vonage.android.compose.SnapshotOutputDirRule
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
class AudioVolumeIndicatorScreenshotTest {

    @get:Rule
    val snapshotOutputDirRule = SnapshotOutputDirRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = SnapshotTestDefaults.OPTIONS

    @Test
    fun audioVolumeIndicatorSilent_light() {
        composeTestRule.setContent { AudioVolumeIndicatorSilentPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun audioVolumeIndicatorSilent_dark() {
        composeTestRule.setContent { AudioVolumeIndicatorSilentPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun audioVolumeIndicatorMedium_light() {
        composeTestRule.setContent { AudioVolumeIndicatorMediumPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun audioVolumeIndicatorMedium_dark() {
        composeTestRule.setContent { AudioVolumeIndicatorMediumPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun audioVolumeIndicatorLoud_light() {
        composeTestRule.setContent { AudioVolumeIndicatorLoudPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun audioVolumeIndicatorLoud_dark() {
        composeTestRule.setContent { AudioVolumeIndicatorLoudPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
