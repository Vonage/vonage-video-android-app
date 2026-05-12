package com.vonage.android.meetingroom.internal.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.vonage.android.captions.ui.CaptionsOverlay
import com.vonage.android.chat.ui.ChatPanel
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.components.GenericLoading
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.fx.ui.VideoEffectsScreen
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.api.MeetingRoomFeature
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.meetingroom.internal.screen.audio.AudioDevicesMenu
import com.vonage.android.meetingroom.internal.screen.components.MeetingRoomContent
import com.vonage.android.meetingroom.internal.screen.components.MeetingTopBar
import com.vonage.android.meetingroom.internal.screen.components.SpeakingWhileMutedOverlay
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBar
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBarState
import com.vonage.android.meetingroom.internal.factory.ReportingContent as DefaultReportingContent
import com.vonage.android.meetingroom.internal.util.ext.isExtraPaneShow
import com.vonage.android.meetingroom.internal.util.ext.toggleChat
import com.vonage.android.meetingroom.internal.util.rememberNoiseSuppression
import com.vonage.android.reactions.ui.EmojiReactionOverlay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

/**
 * Maps the runtime [MeetingRoomFeature] set to the [BottomBarActionType] list that should be
 * shown. Features absent from [enabledFeatures] are filtered out; the compile-time flavor system
 * already handles the disabled case for flavor-gated features, so this is purely additive.
 */
private fun enabledBottomBarActions(
    enabledFeatures: Set<MeetingRoomFeature>,
): ImmutableList<BottomBarActionType> = BottomBarActionType.entries.filter { actionType ->
    when (actionType) {
        BottomBarActionType.CHAT -> MeetingRoomFeature.CHAT in enabledFeatures
        BottomBarActionType.RECORD_SESSION -> MeetingRoomFeature.ARCHIVING in enabledFeatures
        BottomBarActionType.CAPTIONS -> MeetingRoomFeature.CAPTIONS in enabledFeatures
        BottomBarActionType.SCREEN_SHARING -> MeetingRoomFeature.SCREEN_SHARE in enabledFeatures
        // CHANGE_LAYOUT, PARTICIPANTS, REPORT are always allowed (not feature-gated)
        else -> true
    }
}.toImmutableList()

@Suppress("LongMethod", "CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun MeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
    reportingContent: (@Composable (() -> Unit) -> Unit)? = null,
) {
    var showAudioOutputs by remember { mutableStateOf(false) }
    val audioOutputsSheetState = rememberModalBottomSheetState()

    var showVideoEffects by remember { mutableStateOf(false) }
    var selectedEffect by remember { mutableStateOf<VideoEffect>(VideoEffect.None) }
    val videoEffectsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Wire onOpenVideoEffects to local sheet state so no VM involvement is needed.
    val effectsActions = remember(actions, uiState.enabledFeatures) {
        actions.copy(onOpenVideoEffects = {
            if (MeetingRoomFeature.BACKGROUND_EFFECTS in uiState.enabledFeatures) {
                showVideoEffects = true
            }
        })
    }

    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    LaunchedEffect(navigator.scaffoldValue) {
        actions.onListenUnread(navigator.isExtraPaneShow().not())
    }

    val isChatShow by remember(navigator.scaffoldValue) {
        derivedStateOf { navigator.isExtraPaneShow() }
    }

    val call = uiState.call

    when {
        (uiState.isError.not() && uiState.isLoading.not() && uiState.isEndCall.not() && call != null) -> {
            val participants by call.participantsStateFlow.collectAsStateWithLifecycle()
            val publisher by call.publisher.collectAsStateWithLifecycle()
            val captionLines by call.captionsStateFlow.collectAsStateWithLifecycle()
            // Sync selectedEffect to the real publisher's current effect each time the sheet
            // opens, so the grid always highlights the correct initial selection.
            LaunchedEffect(showVideoEffects) {
                if (showVideoEffects) {
                    selectedEffect = publisher?.videoEffect?.value ?: VideoEffect.None
                }
            }

            Scaffold(
                modifier = modifier.systemBarsPadding(),
                topBar = {
                    MeetingTopBar(
                        modifier = Modifier.testTag(MEETING_ROOM_TOP_BAR),
                        roomName = uiState.roomName,
                        archivingUiState = uiState.archivingUiState,
                        actions = actions,
                        onToggleAudioDeviceSelector = { showAudioOutputs = showAudioOutputs.toggle() },
                        audioDevicesState = uiState.audioDevicesState,
                    )
                },
                bottomBar = {
                    BottomBar(
                        modifier = Modifier.testTag(MEETING_ROOM_BOTTOM_BAR),
                        call = call,
                        roomActions = actions,
                        actions = remember(uiState.enabledFeatures) {
                            enabledBottomBarActions(uiState.enabledFeatures)
                        },
                        state = BottomBarState(
                            onShowChat = { scope.launch { navigator.toggleChat() } },
                            isChatShow = isChatShow,
                            publisher = publisher,
                            participants = participants,
                            layoutType = uiState.layoutType,
                            archivingUiState = uiState.archivingUiState,
                            screenSharingState = uiState.screenSharingState,
                            captionsUiState = uiState.captionsUiState,
                            allowShowParticipantList = uiState.allowShowParticipantList,
                            allowMicrophoneControl = uiState.allowMicrophoneControl,
                            allowCameraControl = uiState.allowCameraControl,
                        ),
                        reportingContent = reportingContent ?: { onDismiss -> DefaultReportingContent(onDismiss) },
                    )
                }
            ) { paddingValues ->
                SupportingPaneScaffold(
                    modifier = Modifier
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .fillMaxSize(),
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    mainPane = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            EmojiReactionOverlay(call = call)
                            CaptionsOverlay(captionLines = captionLines)
                            SpeakingWhileMutedOverlay(publisher = publisher)
                            // MeetingRoomContent is always visible — the effects sheet is a
                            // ModalBottomSheet that overlays it without disturbing the publisher.
                            MeetingRoomContent(
                                modifier = Modifier.testTag(MEETING_ROOM_CONTENT),
                                call = call,
                                actions = effectsActions,
                                participants = participants,
                                layoutType = uiState.layoutType,
                            )
                        }
                    },
                    supportingPane = { },
                    extraPane = {
                        ExtraPane(
                            call = call,
                            actions = actions,
                            onCloseChat = { scope.launch { navigator.navigateBack() } }
                        )
                    }
                )
            }

            if (showAudioOutputs) {
                ModalBottomSheet(
                    onDismissRequest = { showAudioOutputs = false },
                    sheetState = audioOutputsSheetState,
                ) {
                    uiState.audioDevicesState?.let {
                        val noiseSuppression by rememberNoiseSuppression(publisher)
                            .collectAsStateWithLifecycle()
                        AudioDevicesMenu(
                            audioDevicesState = uiState.audioDevicesState,
                            onDismissRequest = {
                                scope.launch {
                                    audioOutputsSheetState.hide()
                                    showAudioOutputs = false
                                }
                            },
                            noiseSuppressionEnabled = noiseSuppression.isEnabled(),
                            onNoiseSuppressorToggle = {
                                publisher?.toggleNoiseSuppression()
                            }
                        )
                    }
                }
            }

            if (showVideoEffects && MeetingRoomFeature.BACKGROUND_EFFECTS in uiState.enabledFeatures) {
                ModalBottomSheet(
                    onDismissRequest = { showVideoEffects = false },
                    sheetState = videoEffectsSheetState,
                ) {
                    VideoEffectsScreen(
                        backgrounds = uiState.backgrounds,
                        selectedEffect = selectedEffect,
                        onEffectSelect = { effect ->
                            selectedEffect = effect
                            actions.onApplyVideoEffect(effect)
                        },
                    )
                }
            }
        }

        uiState.isLoading -> GenericLoading()

        uiState.isError -> {
            val message = if (uiState.errorMessage?.isNotBlank() == true) {
                uiState.errorMessage
            } else {
                stringResource(R.string.meeting_screen_session_creation_error)
            }
            BasicAlertDialog(
                text = message,
                acceptLabel = stringResource(R.string.generic_retry),
                onAccept = actions.onRetry,
                onCancel = actions.onBack,
            )
        }

        uiState.isEndCall -> {
            LaunchedEffect(uiState) {
                actions.onEndCall()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldPaneScope.ExtraPane(
    call: CallFacade,
    actions: MeetingRoomActions,
    onCloseChat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val chatState by call.chatSignalState.collectAsStateWithLifecycle()

    AnimatedPane(modifier = modifier) {
        ChatPanel(
            title = stringResource(R.string.chat_panel_title),
            sendLabel = stringResource(R.string.chat_panel_input_text_placeholder),
            jumpToBottomLabel = stringResource(R.string.chat_panel_jump_to_bottom),
            messages = chatState?.messages.orEmpty().toImmutableList(),
            onSendMessage = actions.onMessageSent,
            onCloseChat = onCloseChat,
        )
    }
}

internal object MeetingRoomScreenTestTags {
    const val MEETING_ROOM_TOP_BAR = "meeting_room_top_bar"
    const val MEETING_ROOM_CONTENT = "meeting_room_content"
    const val MEETING_ROOM_BOTTOM_BAR = "meeting_room_bottom_bar"
    const val MEETING_ROOM_PUBLISHER_EFFECTS_BUTTON = "meeting_room_publisher_effects_button"
}
