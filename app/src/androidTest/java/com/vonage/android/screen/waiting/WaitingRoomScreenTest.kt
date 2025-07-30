package com.vonage.android.screen.waiting

import android.Manifest
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.compose.preview.previewCamera
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WaitingRoomScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var runtimePermissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    @get:Rule(order = 2)
    val compose = createComposeRule()

    private val screen = WaitingRoomScreenObject(compose)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun given_initial_state_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState.Content(
                        roomName = "room-name",
                        userName = "",
                        isMicEnabled = true,
                        isCameraEnabled = true,
                        view = previewCamera(),
                        blurLevel = BlurLevel.NONE,
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.cameraButtonEnabled.assertIsDisplayed()
        screen.prepareToJoinText.assertIsDisplayed()
        screen.roomNameText
            .assertIsDisplayed()
            .assertTextEquals("room-name")
        screen.whatsYourNameText.assertIsDisplayed()
        screen.userNameInput
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText(""))
        screen.joinButton
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun given_state_with_user_name_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState.Content(
                        roomName = "room-name",
                        userName = "user name",
                        isMicEnabled = true,
                        isCameraEnabled = true,
                        view = previewCamera(),
                        blurLevel = BlurLevel.NONE,
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.cameraButtonEnabled.assertIsDisplayed()
        screen.prepareToJoinText.assertIsDisplayed()
        screen.roomNameText
            .assertIsDisplayed()
            .assertTextEquals("room-name")
        screen.whatsYourNameText.assertIsDisplayed()
        screen.userNameInput
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText("user name"))
        screen.joinButton
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun given_state_without_video_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState.Content(
                        roomName = "room-name",
                        userName = "user name",
                        isMicEnabled = true,
                        isCameraEnabled = false,
                        view = previewCamera(),
                        blurLevel = BlurLevel.NONE,
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.cameraButtonDisabled.assertIsDisplayed()
        screen.initials.assertIsDisplayedWithText("UN")
    }

    @Test
    fun given_state_without_video_and_audio_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState.Content(
                        roomName = "room-name",
                        userName = "user name",
                        isMicEnabled = false,
                        isCameraEnabled = false,
                        view = previewCamera(),
                        blurLevel = BlurLevel.NONE,
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonDisabled.assertIsDisplayed()
        screen.cameraButtonDisabled.assertIsDisplayed()
        screen.initials.assertIsDisplayedWithText("UN")
    }

    @Test
    fun given_state_without_video_and_without_user_name_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState.Content(
                        roomName = "room-name",
                        userName = "",
                        isMicEnabled = true,
                        isCameraEnabled = false,
                        view = previewCamera(),
                        blurLevel = BlurLevel.NONE,
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.cameraButtonDisabled.assertIsDisplayed()
        screen.initials.assertIsDisplayedWithIcon()
        screen.userNameInput
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText(""))
        screen.joinButton
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun given_state_without_audio_and_without_user_name_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState.Content(
                        roomName = "room-name",
                        userName = "",
                        isMicEnabled = false,
                        isCameraEnabled = false,
                        view = previewCamera(),
                        blurLevel = BlurLevel.NONE,
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonDisabled.assertIsDisplayed()
        screen.cameraButtonDisabled.assertIsDisplayed()
        screen.initials.assertIsDisplayedWithIcon()
        screen.userNameInput
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText(""))
        screen.joinButton
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }
}
