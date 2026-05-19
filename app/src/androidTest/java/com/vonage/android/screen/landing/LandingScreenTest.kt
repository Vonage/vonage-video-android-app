package com.vonage.android.screen.landing

import android.content.Context
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.util.hasText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LandingScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createComposeRule()

    private val screen = LandingScreenObject(compose)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun given_initial_state_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                LandingScreen(
                    uiState = LandingScreenUiState.Content(),
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
            .assert(hasText(""))
        screen.roomInputLabel
            .assertIsNotDisplayed()
    }

    @Test
    fun given_valid_state_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                LandingScreen(
                    uiState = LandingScreenUiState.Content(
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
                LandingScreen(
                    uiState = LandingScreenUiState.Content(
                        roomName = "hi@there",
                        isRoomNameWrong = true,
                    ),
                    actions = NO_OP_JOIN_MEETING_ROOM_ACTIONS,
                )
            }
        }

        screen.joinButton
            .assertIsDisplayed()
            .assertIsEnabled()
        screen.roomInput
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText("hi@there"))
        screen.roomInputLabel
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText(context, R.string.landing_room_name_error_message))
    }

    @Test
    fun given_join_button_clicked_THEN_onJoinRoomClick_callback_invoked() {
        var capturedArg: String? = null
        compose.setContent {
            VonageVideoTheme {
                LandingScreen(
                    uiState = LandingScreenUiState.Content(roomName = "test-room"),
                    actions = LandingScreenActions(
                        onJoinRoomClick = { capturedArg = it },
                        onCreateRoomClick = {},
                        onRoomNameChange = {},
                    ),
                )
            }
        }

        screen.joinButton.performScrollTo().performClick()

        assertTrue(capturedArg != null)
    }

    @Test
    fun given_create_room_button_clicked_THEN_onCreateRoomClick_callback_invoked() {
        var wasCalled = false
        compose.setContent {
            VonageVideoTheme {
                LandingScreen(
                    uiState = LandingScreenUiState.Content(),
                    actions = LandingScreenActions(
                        onJoinRoomClick = {},
                        onCreateRoomClick = { wasCalled = true },
                        onRoomNameChange = {},
                    ),
                )
            }
        }

        screen.createRoomButton.performScrollTo().performClick()

        assertTrue(wasCalled)
    }

    @Test
    fun given_room_name_input_THEN_onRoomNameChange_callback_invoked() {
        var capturedArg: String? = null
        compose.setContent {
            VonageVideoTheme {
                LandingScreen(
                    uiState = LandingScreenUiState.Content(),
                    actions = LandingScreenActions(
                        onJoinRoomClick = {},
                        onCreateRoomClick = {},
                        onRoomNameChange = { capturedArg = it },
                    ),
                )
            }
        }

        screen.roomInput.performScrollTo().performTextInput("new-room")

        assertTrue(capturedArg != null)
    }

    companion object {
        val NO_OP_JOIN_MEETING_ROOM_ACTIONS = LandingScreenActions(
            onJoinRoomClick = {},
            onCreateRoomClick = {},
            onRoomNameChange = {},
        )
    }
}