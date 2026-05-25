package com.vonage.android.compose.layout

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
class TwoPaneScaffoldScreenshotTest {

    @get:Rule
    val snapshotOutputDirRule = SnapshotOutputDirRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
    )

    @Test
    fun twoPaneScaffoldCompact_light() {
        composeTestRule.setContent { TwoPaneScaffoldCompactPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun twoPaneScaffoldCompact_dark() {
        composeTestRule.setContent { TwoPaneScaffoldCompactPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun twoPaneScaffoldExpanded_light() {
        composeTestRule.setContent { TwoPaneScaffoldExpandedPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun twoPaneScaffoldExpanded_dark() {
        composeTestRule.setContent { TwoPaneScaffoldExpandedPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
