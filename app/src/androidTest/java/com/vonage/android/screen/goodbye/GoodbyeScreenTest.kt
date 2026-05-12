package com.vonage.android.screen.goodbye

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.android.compose.theme.VonageVideoTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GoodbyeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createComposeRule()

    private val screen = GoodbyeScreenObject(compose)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun given_idle_state_THEN_header_displayed_and_archives_container_absent() {
        compose.setContent {
            VonageVideoTheme {
                GoodbyeScreen(
                    uiState = GoodbyeScreenUiState.Idle,
                    actions = GoodbyeScreenActions(),
                )
            }
        }

        screen.header.assertIsDisplayed()
        screen.rejoinContainer.assertIsDisplayed()
        screen.archivesContainer.assertDoesNotExist()
    }

    @Test
    fun given_content_state_THEN_all_components_displayed() {
        val archive = Archive(
            id = ArchiveId("1"),
            name = "Recording 1",
            url = "url",
            status = ArchiveStatus.AVAILABLE,
            createdAt = 1231,
            duration = 123,
            size = 123123,
        )

        compose.setContent {
            VonageVideoTheme {
                GoodbyeScreen(
                    uiState = GoodbyeScreenUiState.Content(
                        archives = persistentListOf(archive),
                    ),
                    actions = GoodbyeScreenActions(),
                )
            }
        }

        screen.header.assertIsDisplayed()
        screen.rejoinContainer.assertIsDisplayed()
        screen.archivesContainer.assertIsDisplayed()
    }

    @Test
    fun given_rejoin_button_clicked_THEN_onReEnter_callback_invoked() {
        var wasCalled = false

        compose.setContent {
            VonageVideoTheme {
                GoodbyeScreen(
                    uiState = GoodbyeScreenUiState.Idle,
                    actions = GoodbyeScreenActions(onReEnter = { wasCalled = true }),
                )
            }
        }

        screen.rejoinButton.performClick()

        assertTrue(wasCalled)
    }

    @Test
    fun given_go_home_button_clicked_THEN_onGoHome_callback_invoked() {
        var wasCalled = false

        compose.setContent {
            VonageVideoTheme {
                GoodbyeScreen(
                    uiState = GoodbyeScreenUiState.Idle,
                    actions = GoodbyeScreenActions(onGoHome = { wasCalled = true }),
                )
            }
        }

        screen.goHomeButton.performClick()

        assertTrue(wasCalled)
    }
}
