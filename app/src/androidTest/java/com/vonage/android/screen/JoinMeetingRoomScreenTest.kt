package com.vonage.android.screen

import android.content.Context
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.join.JoinMeetingRoomActions
import com.vonage.android.screen.join.JoinMeetingRoomScreen
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.CREATE_ROOM_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.PROGRESS_INDICATOR_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.ROOM_INPUT_ERROR_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.ROOM_INPUT_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.SUBTITLE_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.TITLE_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.VONAGE_ICON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomUiState
import com.vonage.android.util.hasText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinMeetingRoomScreenTest {

    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun given_initial_state_THEN_components_are_displayed() {
        testRule.setContent {
            VonageVideoTheme {
                JoinMeetingRoomScreen(
                    uiState = JoinMeetingRoomUiState.Content(),
                    actions = JoinMeetingRoomActions(
                        onJoinRoomClick = {},
                        onCreateRoomClick = {},
                        onRoomNameChange = {},
                    ),
                )
            }
        }

        testRule.onNodeWithTag(VONAGE_ICON_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(TITLE_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(SUBTITLE_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(CREATE_ROOM_BUTTON_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(JOIN_BUTTON_TAG)
            .assertIsDisplayed()
            .assertIsNotEnabled()
        testRule.onNodeWithTag(ROOM_INPUT_TAG)
            .assertIsDisplayed()
            .assert(hasText(""))
        testRule.onNodeWithTag(ROOM_INPUT_ERROR_TAG)
            .assertIsNotDisplayed()
    }

    @Test
    fun given_valid_state_THEN_components_are_displayed() {
        testRule.setContent {
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

        testRule.onNodeWithTag(VONAGE_ICON_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(TITLE_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(SUBTITLE_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(CREATE_ROOM_BUTTON_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(JOIN_BUTTON_TAG)
            .assertIsDisplayed()
            .assertIsEnabled()
        testRule.onNodeWithTag(ROOM_INPUT_TAG)
            .assertIsDisplayed()
            .assert(hasText("hithere"))
        testRule.onNodeWithTag(ROOM_INPUT_ERROR_TAG)
            .assertIsNotDisplayed()
    }

    @Test
    fun given_not_valid_state_THEN_components_are_displayed() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

        testRule.setContent {
            VonageVideoTheme {
                JoinMeetingRoomScreen(
                    uiState = JoinMeetingRoomUiState.Content(
                        roomName = "hi@there",
                        isRoomNameWrong = true,
                    ),
                    actions = JoinMeetingRoomActions(
                        onJoinRoomClick = {},
                        onCreateRoomClick = {},
                        onRoomNameChange = {},
                    ),
                )
            }
        }

        testRule.onNodeWithTag(JOIN_BUTTON_TAG)
            .assertIsDisplayed()
            .assertIsNotEnabled()
        testRule.onNodeWithTag(ROOM_INPUT_TAG)
            .assertIsDisplayed()
            .assert(hasText("hi@there"))
        testRule.onNodeWithTag(ROOM_INPUT_ERROR_TAG, useUnmergedTree = true)
            .assertIsDisplayed()
            .assert(hasText(context, R.string.landing_room_name_error_message))
    }

    @Test
    fun given_loading_state_THEN_components_are_displayed() {
        testRule.setContent {
            VonageVideoTheme {
                JoinMeetingRoomScreen(
                    uiState = JoinMeetingRoomUiState.Loading,
                    actions = JoinMeetingRoomActions(
                        onJoinRoomClick = {},
                        onCreateRoomClick = {},
                        onRoomNameChange = {},
                    ),
                )
            }
        }

        testRule.onNodeWithTag(VONAGE_ICON_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(TITLE_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(PROGRESS_INDICATOR_TAG)
            .assertIsDisplayed()
        testRule.onNodeWithTag(JOIN_BUTTON_TAG)
            .assertIsNotDisplayed()
        testRule.onNodeWithTag(ROOM_INPUT_TAG)
            .assertIsNotDisplayed()
        testRule.onNodeWithTag(ROOM_INPUT_ERROR_TAG, useUnmergedTree = true)
            .assertIsNotDisplayed()
    }
}