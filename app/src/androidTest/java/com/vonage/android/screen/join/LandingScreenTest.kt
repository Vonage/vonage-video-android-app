package com.vonage.android.screen.join

import android.content.Context
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.util.hasText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
                    uiState = LandingScreenUiState(),
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
                LandingScreen(
                    uiState = LandingScreenUiState(
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
                    uiState = LandingScreenUiState(
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
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText("hi@there"))
        screen.roomInputLabel
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText(context, R.string.landing_room_name_error_message))
    }

    companion object {
        val NO_OP_JOIN_MEETING_ROOM_ACTIONS = JoinMeetingRoomActions(
            onJoinRoomClick = {},
            onCreateRoomClick = {},
            onRoomNameChange = {},
        )
    }
}