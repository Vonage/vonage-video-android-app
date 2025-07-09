package com.vonage.android.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.theme.VonageVideoAndroidTheme
import com.vonage.android.screen.join.JoinMeetingRoomScreen
import com.vonage.android.screen.join.MainUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinMeetingRoomScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun given_valid_state_THEN_components_are_displayed() {
        composeTestRule.setContent {
            VonageVideoAndroidTheme {
                JoinMeetingRoomScreen(
                    mainUiState = MainUiState.Content(
                        roomName = "hithere",
                        isRoomNameWrong = false,
                    ),
                )
            }
        }

        composeTestRule.onNodeWithTag("join_meeting_room_screen_subtitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("join_meeting_room_screen_create_room_button").assertIsDisplayed()
    }
}