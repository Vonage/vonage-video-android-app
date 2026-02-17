package com.vonage.android.screen.waiting

import android.Manifest
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.CameraType
import com.vonage.android.kotlin.model.PublisherParticipant
import com.vonage.android.kotlin.model.VideoSource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "",
                        publisher = buildPublisher(
                            isMicEnabled = true,
                            isCameraEnabled = true,
                        ),
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.volumeIndicator.assertIsDisplayed()
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
            .assertIsEnabled()
    }

    @Test
    fun given_state_with_user_name_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "user name",
                        publisher = buildPublisher(
                            isMicEnabled = true,
                            isCameraEnabled = true,
                        ),
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.volumeIndicator.assertIsDisplayed()
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
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "user name",
                        publisher = buildPublisher(
                            isMicEnabled = true,
                            isCameraEnabled = false,
                        ),
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.volumeIndicator.assertIsDisplayed()
        screen.cameraButtonDisabled.assertIsDisplayed()
        screen.initials.assertIsDisplayedWithText("UN")
    }

    @Test
    fun given_state_without_video_and_audio_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "user name",
                        publisher = buildPublisher(
                            isMicEnabled = false,
                            isCameraEnabled = false,
                        ),
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonDisabled.assertIsDisplayed()
        screen.volumeIndicator.assertIsNotDisplayed()
        screen.cameraButtonDisabled.assertIsDisplayed()
        screen.initials.assertIsDisplayedWithText("UN")
    }

    @Test
    fun given_state_without_video_and_without_user_name_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "",
                        publisher = buildPublisher(
                            isMicEnabled = true,
                            isCameraEnabled = false,
                        ),
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.volumeIndicator.assertIsDisplayed()
        screen.cameraButtonDisabled.assertIsDisplayed()
        screen.initials.assertIsDisplayedWithIcon()
        screen.userNameInput
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText(""))
        screen.joinButton
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun given_state_without_audio_and_without_user_name_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "",
                        publisher = buildPublisher(
                            isMicEnabled = false,
                            isCameraEnabled = false,
                        ),
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonDisabled.assertIsDisplayed()
        screen.volumeIndicator.assertIsNotDisplayed()
        screen.cameraButtonDisabled.assertIsDisplayed()
        screen.initials.assertIsDisplayedWithIcon()
        screen.userNameInput
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText(""))
        screen.joinButton
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun given_state_with_wrong_user_name_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "",
                        isUserNameValid = false,
                        publisher = buildPublisher(
                            isMicEnabled = false,
                            isCameraEnabled = false,
                        ),
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.userNameInput
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText(""))
        screen.joinButton
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
        screen.userNameInputError
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun given_initial_state_with_allowMicrophoneControl_false_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "",
                        publisher = buildPublisher(
                            isMicEnabled = true,
                            isCameraEnabled = true,
                        ),
                        allowMicrophoneControl = false,
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsNotDisplayed()
        screen.micButtonDisabled.assertIsNotDisplayed()
        screen.volumeIndicator.assertIsDisplayed()
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
            .assertIsEnabled()
    }

    @Test
    fun given_initial_state_with_allowCameraControl_false_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                WaitingRoomScreen(
                    uiState = WaitingRoomUiState(
                        roomName = "room-name",
                        userName = "",
                        publisher = buildPublisher(
                            isMicEnabled = true,
                            isCameraEnabled = true,
                        ),
                        allowCameraControl = false,
                    ),
                    actions = WaitingRoomActions(),
                )
            }
        }
        screen.micButtonEnabled.assertIsDisplayed()
        screen.volumeIndicator.assertIsDisplayed()
        screen.cameraButtonEnabled.assertIsNotDisplayed()
        screen.cameraButtonDisabled.assertIsNotDisplayed()
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
            .assertIsEnabled()
    }

    @Suppress("EmptyFunctionBlock")
    @Composable
    private fun buildPublisher(
        isMicEnabled: Boolean,
        isCameraEnabled: Boolean,
    ): PublisherParticipant {
        return object : PublisherParticipant {
            override val camera: StateFlow<CameraType> = MutableStateFlow(CameraType.FRONT)
            override val blurLevel: StateFlow<BlurLevel> = MutableStateFlow(BlurLevel.NONE)

            override fun toggleVideo() {}
            override fun toggleAudio() {}
            override fun cycleCamera() {}
            override fun cycleCameraBlur() {}
            override fun clean() {}

            override val id: String = "publisher"
            override val connectionId: String = "publisher-connection-id"
            override val creationTime: Long = 1L
            override val isScreenShare: Boolean = false
            override val videoSource: VideoSource = VideoSource.CAMERA
            override val name: String = "test publisher"
            override val isMicEnabled: StateFlow<Boolean> = MutableStateFlow(isMicEnabled)
            override val isCameraEnabled: StateFlow<Boolean> = MutableStateFlow(isCameraEnabled)
            override val isTalking: StateFlow<Boolean> = MutableStateFlow(isMicEnabled)
            override val audioLevel: StateFlow<Float> = MutableStateFlow(0F)
            override val view: View = previewCamera()
        }
    }
}
