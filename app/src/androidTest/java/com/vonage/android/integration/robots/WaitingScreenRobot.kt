package com.vonage.android.integration.robots

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.vonage.android.screen.waiting.WaitingRoomScreenObject

class WaitingScreenRobot(compose: ComposeTestRule) {

    val screen = WaitingRoomScreenObject(compose)

    fun checkRoomName(roomName: String) {
        screen.roomNameText
            .assertTextEquals(roomName)
    }

    fun enterUserName(userName: String) {
        screen.userNameInput
            .performTextInput(userName)
        screen.joinButton
            .assertIsEnabled()
    }

    fun disableCamera() {
        screen.cameraButtonEnabled
            .performClick()
        screen.cameraButtonDisabled
            .assertIsDisplayed()
    }

    fun disableAudio() {
        screen.micButtonEnabled
            .performClick()
        screen.micButtonDisabled
            .assertIsDisplayed()
    }

    fun checkInitials(initials: String) {
        screen.initials
            .assertIsDisplayedWithText(initials)
    }

    fun join() {
        screen.joinButton.performClick()
    }
}

fun waiting(composeRule: ComposeTestRule, func: WaitingScreenRobot.() -> Unit) =
    WaitingScreenRobot(composeRule).apply { func() }
