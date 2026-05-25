package com.vonage.android.compose.components

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
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

// AlertDialog creates a separate composition root — use onAllNodes(isRoot())[1] to target the
// dialog itself rather than the empty main window at index 0.
@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BasicAlertDialogScreenshotTest {

    @get:Rule
    val snapshotOutputDirRule = SnapshotOutputDirRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
    )

    @Test
    fun basicAlertDialog_light() {
        composeTestRule.setContent { BasicAlertDialogPreview() }
        composeTestRule.onAllNodes(isRoot())[1].captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun basicAlertDialog_dark() {
        composeTestRule.setContent { BasicAlertDialogPreview() }
        composeTestRule.onAllNodes(isRoot())[1].captureRoboImage(roborazziOptions = options)
    }
}
