package com.vonage.android.compose.components.bottombar

import androidx.compose.ui.test.junit4.v2.createComposeRule
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
class ActionCellScreenshotTest {

    @get:Rule
    val snapshotOutputDirRule = SnapshotOutputDirRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
    )

    @Test
    fun actionCellUnselected_light() {
        composeTestRule.setContent { ActionCellUnselectedPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun actionCellUnselected_dark() {
        composeTestRule.setContent { ActionCellUnselectedPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun actionCellSelected_light() {
        composeTestRule.setContent { ActionCellSelectedPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun actionCellSelected_dark() {
        composeTestRule.setContent { ActionCellSelectedPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun actionCellWithBadge_light() {
        composeTestRule.setContent { ActionCellWithBadgePreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun actionCellWithBadge_dark() {
        composeTestRule.setContent { ActionCellWithBadgePreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
