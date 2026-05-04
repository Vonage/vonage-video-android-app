package com.vonage.android.screen.room

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MeetingRoomScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createComposeRule()

    private val screen = MeetingRoomScreenObject(compose)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun given_initial_state_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "sample-name",
                        call = buildCallWithParticipants(
                            participantCount = 5,
                            unreadCount = 8,
                        ),
                    ),
                    actions = MeetingRoomActions(),
                )
            }
        }

        screen.topBar.assertIsDisplayedWithTitle("sample-name")
        screen.content.assertIsDisplayed()
        screen.bottomBar.assertIsDisplayed()
    }

    @Test
    fun given_initial_state_with_allowMicrophoneControl_false_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "sample-name",
                        call = buildCallWithParticipants(
                            participantCount = 5,
                            unreadCount = 8,
                        ),
                        allowMicrophoneControl = false,
                    ),
                    actions = MeetingRoomActions(),
                )
            }
        }

        screen.topBar.assertIsDisplayedWithTitle("sample-name")
        screen.content.assertIsDisplayed()
        screen.bottomBar.cameraButton.assertIsDisplayed()
        screen.bottomBar.micButton.assertIsNotDisplayed()
    }

    @Test
    fun given_initial_state_with_allowCameraControl_false_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "sample-name",
                        call = buildCallWithParticipants(
                            participantCount = 5,
                            unreadCount = 8,
                        ),
                        allowCameraControl = false,
                    ),
                    actions = MeetingRoomActions(),
                )
            }
        }

        screen.topBar.assertIsDisplayedWithTitle("sample-name")
        screen.content.assertIsDisplayed()
        screen.bottomBar.cameraButton.assertIsNotDisplayed()
        screen.bottomBar.micButton.assertIsDisplayed()
    }

    @Test
    fun given_end_call_button_clicked_THEN_onEndCall_callback_invoked() {
        var wasCalled = false
        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "room",
                        call = buildCallWithParticipants(participantCount = 5, unreadCount = 0),
                    ),
                    actions = MeetingRoomActions(onEndCall = { wasCalled = true }),
                )
            }
        }

        screen.bottomBar.endCallButton.performClick()

        assertTrue(wasCalled)
    }

    @Test
    fun given_mic_button_clicked_THEN_onToggleMic_callback_invoked() {
        var wasCalled = false
        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "room",
                        call = buildCallWithParticipants(participantCount = 5, unreadCount = 0),
                    ),
                    actions = MeetingRoomActions(onToggleMic = { wasCalled = true }),
                )
            }
        }

        screen.bottomBar.micButton.performClick()

        assertTrue(wasCalled)
    }

    @Test
    fun given_camera_button_clicked_THEN_onToggleCamera_callback_invoked() {
        var wasCalled = false
        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "room",
                        call = buildCallWithParticipants(participantCount = 5, unreadCount = 0),
                    ),
                    actions = MeetingRoomActions(onToggleCamera = { wasCalled = true }),
                )
            }
        }

        screen.bottomBar.cameraButton.performClick()

        assertTrue(wasCalled)
    }
}
