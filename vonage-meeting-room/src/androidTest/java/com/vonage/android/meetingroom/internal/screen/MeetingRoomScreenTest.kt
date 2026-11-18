package com.vonage.android.meetingroom.internal.screen

import android.content.Context
import android.media.projection.MediaProjection
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.components.ParticipantsPlaceholdersTestTags.PARTICIPANTS_PLACEHOLDER_TAG
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.preview.buildPublisher
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.fx.ui.VideoEffectsTestTags
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.CaptionLine
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_PUBLISHER_EFFECTS_BUTTON
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.meetingroom.internal.screen.components.TopBarTestTags.TOP_BAR_CAMERA_SWITCH_ACTION
import com.vonage.android.meetingroom.internal.screen.components.TopBarTestTags.TOP_BAR_TITLE
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBarTestTags.BOTTOM_BAR_CAMERA_BUTTON
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBarTestTags.BOTTOM_BAR_MIC_BUTTON
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBarTestTags.BOTTOM_BAR_MORE_BUTTON
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BUTTON
import com.vonage.android.meetingroom.internal.screen.components.bottombar.SEARCH_TAG
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MeetingRoomScreenTest {

    @get:Rule
    val compose = createComposeRule()

    /**
     * The bottom bar only shows as many actions as fit its width and moves the rest into the
     * "more" sheet. Forcing a wide window keeps tests that look for a specific bar button
     * independent of the width of the device they run on.
     */
    @Composable
    private fun WideScreen(content: @Composable () -> Unit) {
        DeviceConfigurationOverride(
            override = DeviceConfigurationOverride.ForcedSize(DpSize(WIDE_SCREEN_DP.dp, WIDE_SCREEN_DP.dp)),
            content = content,
        )
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
            WideScreen {
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
        }

        compose.onNodeWithTag(MEETING_ROOM_TOP_BAR).assertIsDisplayed()
        compose.onNodeWithTag(TOP_BAR_TITLE, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals("sample-name")
        compose.onNodeWithTag(MEETING_ROOM_CONTENT).assertIsDisplayed()
        compose.onNodeWithTag("$BOTTOM_BAR_CAMERA_BUTTON-disabled", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag("$BOTTOM_BAR_MIC_BUTTON-disabled", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun given_initial_state_with_allowCameraControl_false_THEN_components_are_displayed() {
        compose.setContent {
            WideScreen {
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
        }

        compose.onNodeWithTag(MEETING_ROOM_TOP_BAR).assertIsDisplayed()
        compose.onNodeWithTag(TOP_BAR_TITLE, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals("sample-name")
        compose.onNodeWithTag(MEETING_ROOM_CONTENT).assertIsDisplayed()
        compose.onNodeWithTag("$BOTTOM_BAR_CAMERA_BUTTON-disabled", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithTag("$BOTTOM_BAR_MIC_BUTTON-disabled", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag(TOP_BAR_CAMERA_SWITCH_ACTION, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun given_all_optional_features_disabled_THEN_more_options_button_not_displayed() {
        compose.setContent {
            WideScreen {
                VonageVideoTheme {
                    MeetingRoomScreen(
                        uiState = MeetingRoomUiState(
                            roomName = "sample-name",
                            call = buildCallWithParticipants(
                                participantCount = 5,
                                unreadCount = 8,
                            ),
                            enabledFeatures = emptySet(),
                        ),
                        actions = MeetingRoomActions(),
                    )
                }
            }
        }

        compose.onNodeWithTag(MEETING_ROOM_BOTTOM_BAR).assertIsDisplayed()
        compose.onNodeWithTag(BOTTOM_BAR_MORE_BUTTON, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun given_participants_button_tapped_THEN_participants_sheet_opens() {
        compose.setContent {
            WideScreen {
                VonageVideoTheme {
                    MeetingRoomScreen(
                        uiState = MeetingRoomUiState(
                            roomName = "sample-name",
                            call = buildCallWithParticipants(participantCount = 3),
                        ),
                        actions = MeetingRoomActions(),
                    )
                }
            }
        }

        compose.onNodeWithTag(SEARCH_TAG).assertDoesNotExist()
        compose.onNodeWithTag(BOTTOM_BAR_PARTICIPANTS_BUTTON).performClick()
        compose.onNodeWithTag(SEARCH_TAG).assertIsDisplayed()
    }

    @Test
    fun given_grid_overflow_placeholder_tapped_THEN_participants_sheet_opens() {
        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "sample-name",
                        // 8 participants exceeds the adaptive grid's MAX_GRID_TILES (6), forcing the
                        // "+N" overflow placeholder to render instead of a 7th/8th video tile.
                        call = buildCallWithParticipants(participantCount = 8),
                        layoutType = CallLayoutType.ADAPTIVE_GRID,
                    ),
                    actions = MeetingRoomActions(),
                )
            }
        }

        compose.onNodeWithTag(PARTICIPANTS_PLACEHOLDER_TAG).performClick()
        compose.onNodeWithTag(SEARCH_TAG).assertIsDisplayed()
    }

    @Test
    fun given_filmstrip_overflow_placeholder_tapped_THEN_participants_sheet_opens() {
        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "sample-name",
                        // 5 participants with no active speaker exceeds the filmstrip's
                        // MAX_FILMSTRIP_TILES (2), forcing the "+N" overflow placeholder to render.
                        call = buildCallWithParticipants(participantCount = 5),
                        layoutType = CallLayoutType.SPEAKER_LAYOUT,
                    ),
                    actions = MeetingRoomActions(),
                )
            }
        }

        compose.onNodeWithTag(PARTICIPANTS_PLACEHOLDER_TAG).performClick()
        compose.onNodeWithTag(SEARCH_TAG).assertIsDisplayed()
    }

    @Test
    fun given_videoEffects_button_tapped_THEN_effects_sheet_opens_and_callback_invoked() {
        var appliedEffect: VideoEffect? = null

        compose.setContent {
            VonageVideoTheme {
                MeetingRoomScreen(
                    uiState = MeetingRoomUiState(
                        roomName = "sample-name",
                        // enabledFeatures defaults to MeetingRoomFeature.all, which includes BACKGROUND_EFFECTS
                        call = buildCallWithPublisherInParticipants(),
                    ),
                    actions = MeetingRoomActions(onApplyVideoEffect = { appliedEffect = it }),
                )
            }
        }

        compose.onNodeWithTag(MEETING_ROOM_PUBLISHER_EFFECTS_BUTTON).performClick()
        compose.onNodeWithTag(VideoEffectsTestTags.VIDEO_EFFECTS_SHEET_CONTENT).assertIsDisplayed()
        compose.onNodeWithTag(VideoEffectsTestTags.VIDEO_EFFECTS_BLUR_LOW_TILE).performClick()
        assertEquals(VideoEffect.BlurLow, appliedEffect)
    }

    @Suppress("EmptyFunctionBlock")
    @Composable
    private fun buildCallWithPublisherInParticipants(): CallFacade {
        val pub = buildPublisher()
        return object : CallFacade {
            override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {}
            override val participantsStateFlow: StateFlow<ImmutableList<Participant>> =
                MutableStateFlow(persistentListOf(pub))
            override val participantsCount: StateFlow<Int> = MutableStateFlow(1)
            override val activeSpeaker: StateFlow<Participant?> = MutableStateFlow(null)
            override val pinnedParticipantIds: StateFlow<Set<String>> = MutableStateFlow(emptySet())
            override fun togglePinParticipant(participantId: String) {}
            override fun forceMuteParticipant(participantId: String) {}
            override val captionsStateFlow: StateFlow<ImmutableList<CaptionLine>> = MutableStateFlow(persistentListOf())
            override val activeArchives: StateFlow<Map<String, String?>> = MutableStateFlow(emptyMap())
            override val chatSignalState: StateFlow<ChatState?> = MutableStateFlow(null)
            override val emojiSignalState: StateFlow<EmojiState?> = MutableStateFlow(null)
            override fun connect(context: Context): Flow<SessionEvent> = flowOf()
            override fun enableCaptions() {}
            override fun disableCaptions() {}
            override fun setCaptionsTranslationLanguage(languageCode: String?) {}
            override fun pauseSession() {}
            override fun resumeSession() {}
            override fun endSession() {}
            override val publisher: StateFlow<PublisherState?> = MutableStateFlow(null)
            override fun toggleLocalVideo() {}
            override fun toggleLocalCamera() {}
            override fun toggleLocalAudio() {}
            override fun applyLocalVideoEffect(effect: VideoEffect) {}
            override fun setVideoBitrate(config: VideoBitrateConfig) {}
            override fun setDegradationPreference(preference: DegradationPreference) {}
            override fun sendChatMessage(message: String) {}
            override fun listenUnreadChatMessages(enable: Boolean) {}
            override fun sendEmoji(emoji: String) {}
            override fun startCapturingScreen(mediaProjection: MediaProjection) {}
            override fun stopCapturingScreen() {}
        }
    }
}

private const val WIDE_SCREEN_DP = 900
