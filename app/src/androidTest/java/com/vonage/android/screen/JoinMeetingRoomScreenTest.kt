package com.vonage.android.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.join.JoinMeetingRoomActions
import com.vonage.android.screen.join.JoinMeetingRoomScreen
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.CREATE_ROOM_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.SUBTITLE_TAG
import com.vonage.android.screen.join.JoinMeetingRoomUiState
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
            VonageVideoTheme {
                JoinMeetingRoomScreen(
                    uiState = JoinMeetingRoomUiState.Content(
                        roomName = "hithere",
                        isRoomNameWrong = false,
                    ),
                    actions = JoinMeetingRoomActions(
                        onJoinRoomClick = {},
                        onCreateRoomClick = {},
                        onRoomNameChange = {},
                    ),
                )
            }
        }

        composeTestRule.onNodeWithTag(SUBTITLE_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(CREATE_ROOM_BUTTON_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithTag(JOIN_BUTTON_TAG).assertIsDisplayed()
    }
}