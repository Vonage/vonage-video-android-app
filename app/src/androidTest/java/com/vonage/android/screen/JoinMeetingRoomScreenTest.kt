package com.vonage.android.screen

import android.content.Context
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.join.JoinMeetingRoomActions
import com.vonage.android.screen.join.JoinMeetingRoomScreen
import com.vonage.android.screen.join.JoinMeetingRoomUiState
import com.vonage.android.util.hasText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class JoinMeetingRoomScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createComposeRule()

    private val screen = JoinMeetingRoomScreenObject(compose)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun given_initial_state_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                JoinMeetingRoomScreen(
                    uiState = JoinMeetingRoomUiState.Content(),
                    actions = NO_OP_JOIN_MEETING_ROOM_ACTIONS,
                )
            }
        }

        screen.logo.assertIsDisplayed()
        screen.title.assertIsDisplayed()
        screen.subTitle.assertIsDisplayed()
        screen.createRoomButton.assertIsDisplayed()
        screen.joinButton
            .assertIsDisplayed()
            .assertIsNotEnabled()
        screen.roomInput
            .assertIsDisplayed()
            .assert(hasText(""))
        screen.roomInputLabel
            .assertIsNotDisplayed()
    }

    @Test
    fun given_valid_state_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                JoinMeetingRoomScreen(
                    uiState = JoinMeetingRoomUiState.Content(
                        roomName = "hithere",
                    ),
                    actions = NO_OP_JOIN_MEETING_ROOM_ACTIONS,
                )
            }
        }

        screen.logo.assertIsDisplayed()
        screen.title.assertIsDisplayed()
        screen.subTitle.assertIsDisplayed()
        screen.createRoomButton.assertIsDisplayed()
        screen.joinButton
            .assertIsDisplayed()
            .assertIsEnabled()
        screen.roomInput
            .assertIsDisplayed()
            .assert(hasText("hithere"))
        screen.roomInputLabel
            .assertIsNotDisplayed()
    }

    @Test
    fun given_not_valid_state_THEN_components_are_displayed() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

        compose.setContent {
            VonageVideoTheme {
                JoinMeetingRoomScreen(
                    uiState = JoinMeetingRoomUiState.Content(
                        roomName = "hi@there",
                        isRoomNameWrong = true,
                    ),
                    actions = NO_OP_JOIN_MEETING_ROOM_ACTIONS,
                )
            }
        }

        screen.joinButton
            .assertIsDisplayed()
            .assertIsNotEnabled()
        screen.roomInput
            .assertIsDisplayed()
            .assert(hasText("hi@there"))
        screen.roomInputLabel
            .assertIsDisplayed()
            .assert(hasText(context, R.string.landing_room_name_error_message))
    }

    @Test
    fun given_loading_state_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                JoinMeetingRoomScreen(
                    uiState = JoinMeetingRoomUiState.Loading,
                    actions = NO_OP_JOIN_MEETING_ROOM_ACTIONS,
                )
            }
        }

        screen.logo.assertIsDisplayed()
        screen.title.assertIsDisplayed()
        screen.progressIndicator.assertIsDisplayed()
        screen.joinButton.assertIsNotDisplayed()
        screen.roomInput.assertIsNotDisplayed()
        screen.roomInputLabel.assertIsNotDisplayed()
    }

    companion object {
        val NO_OP_JOIN_MEETING_ROOM_ACTIONS = JoinMeetingRoomActions(
            onJoinRoomClick = {},
            onCreateRoomClick = {},
            onRoomNameChange = {},
        )
    }
}