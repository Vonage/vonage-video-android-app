package com.vonage.android.meetingroom.internal.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.meetingroom.internal.screen.components.TopBarTestTags.TOP_BAR_TITLE
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBarTestTags.BOTTOM_BAR_CAMERA_BUTTON
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBarTestTags.BOTTOM_BAR_MIC_BUTTON
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MeetingRoomScreenTest {

    @get:Rule
    val compose = createComposeRule()

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

        compose.onNodeWithTag(MEETING_ROOM_TOP_BAR).assertIsDisplayed()
        compose.onNodeWithTag(TOP_BAR_TITLE, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals("sample-name")
        compose.onNodeWithTag(MEETING_ROOM_CONTENT).assertIsDisplayed()
        compose.onNodeWithTag(MEETING_ROOM_BOTTOM_BAR).assertIsDisplayed()
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

        compose.onNodeWithTag(MEETING_ROOM_TOP_BAR).assertIsDisplayed()
        compose.onNodeWithTag(TOP_BAR_TITLE, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals("sample-name")
        compose.onNodeWithTag(MEETING_ROOM_CONTENT).assertIsDisplayed()
        compose.onNodeWithTag(BOTTOM_BAR_CAMERA_BUTTON, useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag(BOTTOM_BAR_MIC_BUTTON, useUnmergedTree = true).assertIsNotDisplayed()
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

        compose.onNodeWithTag(MEETING_ROOM_TOP_BAR).assertIsDisplayed()
        compose.onNodeWithTag(TOP_BAR_TITLE, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals("sample-name")
        compose.onNodeWithTag(MEETING_ROOM_CONTENT).assertIsDisplayed()
        compose.onNodeWithTag(BOTTOM_BAR_CAMERA_BUTTON, useUnmergedTree = true).assertIsNotDisplayed()
        compose.onNodeWithTag(BOTTOM_BAR_MIC_BUTTON, useUnmergedTree = true).assertIsDisplayed()
    }
}
