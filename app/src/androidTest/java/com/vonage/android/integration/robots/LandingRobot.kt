package com.vonage.android.integration.robots

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.vonage.android.screen.landing.LandingScreenObject

class LandingRobot(compose: ComposeTestRule) {

    val screen = LandingScreenObject(compose)

    fun enterRoomName(roomName: String) {
        screen.roomInput
            .performTextInput(roomName)
    }

    fun clickCreateRoom() {
        screen.createRoomButton
            .performClick()
    }

    fun join() {
        screen.joinButton
            .performClick()
    }
}

fun landing(composeRule: ComposeTestRule, func: LandingRobot.() -> Unit) =
    LandingRobot(composeRule).apply { func() }
