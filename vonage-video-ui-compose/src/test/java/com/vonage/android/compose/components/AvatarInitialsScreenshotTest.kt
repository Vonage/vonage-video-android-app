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
class AvatarInitialsScreenshotTest {

    @get:Rule
    val snapshotOutputDirRule = SnapshotOutputDirRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = SnapshotTestDefaults.OPTIONS

    @Test
    fun avatarInitialsWithName_light() {
        composeTestRule.setContent { AvatarInitialsWithNamePreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun avatarInitialsWithName_dark() {
        composeTestRule.setContent { AvatarInitialsWithNamePreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    fun avatarInitialsWithoutName_light() {
        composeTestRule.setContent { AvatarInitialsWithoutNamePreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun avatarInitialsWithoutName_dark() {
        composeTestRule.setContent { AvatarInitialsWithoutNamePreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
