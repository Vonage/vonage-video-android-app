package com.vonage.android.screen.room

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.util.preview.buildCallWithParticipants
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
                    uiState = MeetingRoomUiState.Content(
                        roomName = "sample-name",
                        call = buildCallWithParticipants(
                            participantCount = 5,
                            unreadCount = 8,
                        ),
                    ),
                    actions = MeetingRoomActions(),
                    audioLevel = 0.4f,
                )
            }
        }

        screen.topBar.assertIsDisplayedWithTitle("sample-name")
        screen.content.assertIsDisplayed()
        screen.bottomBar
            .assertIsDisplayed()
            .assertIsDisplayedWithParticipantBadge(5.toString())
            .assertIsDisplayedWithUnreadBadge(8.toString())
    }
}
