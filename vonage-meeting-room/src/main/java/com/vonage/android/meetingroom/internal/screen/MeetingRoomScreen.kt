package com.vonage.android.meetingroom.internal.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.captions.ui.CaptionsOverlay
import com.vonage.android.chat.ui.ChatPanel
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.components.GenericLoading
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.fx.ui.VideoEffectsScreen
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.api.MeetingRoomBottomBarAction
import com.vonage.android.meetingroom.api.MeetingRoomBottomBarState
import com.vonage.android.meetingroom.api.MeetingRoomCustomActions
import com.vonage.android.meetingroom.api.MeetingRoomFeature
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_SCREEN_TAG
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.meetingroom.internal.screen.audio.AudioDevicesMenu
import com.vonage.android.meetingroom.internal.screen.components.MeetingRoomContent
import com.vonage.android.meetingroom.internal.screen.components.MeetingTopBar
import com.vonage.android.meetingroom.internal.screen.components.RecordingStartedOverlay
import com.vonage.android.meetingroom.internal.screen.components.SpeakingWhileMutedOverlay
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBar
import com.vonage.android.meetingroom.internal.screen.components.bottombar.BottomBarState
import com.vonage.android.meetingroom.internal.util.ext.isExtraPaneShow
import com.vonage.android.meetingroom.internal.util.ext.toggleChat
import com.vonage.android.meetingroom.internal.util.rememberNoiseSuppression
import com.vonage.android.reactions.ui.EmojiReactionOverlay
import com.vonage.android.screensharing.ScreenSharingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.vonage.android.meetingroom.internal.factory.ReportingContent as DefaultReportingContent

/**
 * Maps the runtime [MeetingRoomFeature] set to the [BottomBarActionType] list that should be
 * shown. Features absent from [enabledFeatures] are filtered out; the compile-time flavor system
 * already handles the disabled case for flavor-gated features, so this is purely additive.
 * Note: REPORT is intentionally excluded — it is always appended as a CUSTOM action in BottomBar.
 */
private fun enabledBottomBarActions(
    enabledFeatures: Set<MeetingRoomFeature>,
): ImmutableList<BottomBarActionType> = BottomBarActionType.entries.filter { actionType ->
    when (actionType) {
        BottomBarActionType.CHAT -> MeetingRoomFeature.CHAT in enabledFeatures
        BottomBarActionType.RECORD_SESSION -> MeetingRoomFeature.ARCHIVING in enabledFeatures
        BottomBarActionType.CAPTIONS -> MeetingRoomFeature.CAPTIONS in enabledFeatures
        BottomBarActionType.SCREEN_SHARING -> MeetingRoomFeature.SCREEN_SHARE in enabledFeatures
        // CUSTOM is never in the action type list — it is injected via additionalActions
        BottomBarActionType.CUSTOM -> false
        // CHANGE_LAYOUT, PARTICIPANTS are always allowed (not feature-gated)
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
    additionalBottomBarActions: StateFlow<List<MeetingRoomBottomBarAction>>? = null,
    customBottomBar: (@Composable (MeetingRoomBottomBarState, MeetingRoomCustomActions) -> Unit)? = null,
) {
    var showBars by remember { mutableStateOf(true) }

    var showAudioOutputs by remember { mutableStateOf(false) }
    val audioOutputsSheetState = rememberModalBottomSheetState()

    var showRecordingConfirmDialog by remember { mutableStateOf(false) }

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

            // Collect additional custom buttons (dynamic — isSelected/badgeCount can change).
            val extraActions by (additionalBottomBarActions ?: MutableStateFlow(emptyList()))
                .collectAsStateWithLifecycle()
            val mappedExtraActions: ImmutableList<BottomBarAction> = remember(extraActions) {
                extraActions.map { action ->
                    BottomBarAction(
                        type = BottomBarActionType.CUSTOM,
                        icon = action.icon,
                        label = action.label,
                        isSelected = action.isSelected,
                        badgeCount = action.badgeCount,
                        onClick = action.onClick,
                    )
                }.toImmutableList()
            }

            // Collect publisher mic/camera state for the custom bottom bar state.
            val isMicEnabled by (publisher?.isMicEnabled ?: MutableStateFlow(false))
                .collectAsStateWithLifecycle()
            val isCameraEnabled by (publisher?.isCameraEnabled ?: MutableStateFlow(false))
                .collectAsStateWithLifecycle()

            // Minimal state exposed to a custom bottom bar composable.
            val bottomBarState = remember(isMicEnabled, isCameraEnabled, uiState) {
                MeetingRoomBottomBarState(
                    isMicEnabled = isMicEnabled,
                    isCameraEnabled = isCameraEnabled,
                    isScreenSharingActive = uiState.screenSharingState == ScreenSharingState.SHARING,
                    isRecordingActive = uiState.archivingUiState == ArchivingUiState.RECORDING,
                    isCaptionsActive = uiState.captionsUiState == CaptionsUiState.ENABLED,
                )
            }

            // Sync selectedEffect to the real publisher's current effect each time the sheet
            // opens, so the grid always highlights the correct initial selection.
            LaunchedEffect(showVideoEffects) {
                if (showVideoEffects) {
                    selectedEffect = publisher?.videoEffect?.value ?: VideoEffect.None
                }
            }
            // If the user deletes the active background while the sheet is open, reset the
            // local selection so the grid no longer highlights a non-existent item.
            // Guard with isNotEmpty() so the effect doesn't fire while the list is still
            // loading (empty initial state would otherwise clear any active selection).
            LaunchedEffect(uiState.backgrounds) {
                val current = selectedEffect
                if (current is VideoEffect.BackgroundImage &&
                    uiState.backgrounds.isNotEmpty() &&
                    uiState.backgrounds.none { it.id == current.id }
                ) {
                    selectedEffect = VideoEffect.None
                }
            }

            // Wrap actions to show confirmation dialog before starting recording
            val wrappedActions = remember(actions, uiState.archivingUiState) {
                actions.copy(
                    onToggleRecording = { enable ->
                        if (enable && uiState.archivingUiState == ArchivingUiState.IDLE) {
                            showRecordingConfirmDialog = true
                        } else if (!enable && uiState.archivingUiState == ArchivingUiState.RECORDING) {
                            showRecordingConfirmDialog = true
                        } else {
                            actions.onToggleRecording(enable)
                        }
                    }
                )
            }

            Scaffold(
                modifier = modifier
                    .testTag(MEETING_ROOM_SCREEN_TAG)
                    .systemBarsPadding(),
                topBar = {
                    AnimatedVisibility(
                        visible = showBars,
                        enter = slideInVertically(
                            animationSpec = tween(durationMillis = BAR_TOGGLE_DURATION_MS),
                            initialOffsetY = { -it },
                        ),
                        exit = slideOutVertically(
                            animationSpec = tween(durationMillis = BAR_TOGGLE_DURATION_MS),
                            targetOffsetY = { -it },
                        ),
                    ) {
                        MeetingTopBar(
                            modifier = Modifier.testTag(MEETING_ROOM_TOP_BAR),
                            roomName = uiState.roomName,
                            archivingUiState = uiState.archivingUiState,
                            actions = actions,
                            onToggleAudioDeviceSelector = {
                                showAudioOutputs = !showAudioOutputs
                            },
                            audioDevicesState = uiState.audioDevicesState,
                        )
                    }
                },
                bottomBar = {
                    AnimatedVisibility(
                        visible = showBars,
                        enter = slideInVertically(
                            animationSpec = tween(durationMillis = BAR_TOGGLE_DURATION_MS),
                            initialOffsetY = { it },
                        ),
                        exit = slideOutVertically(
                            animationSpec = tween(durationMillis = BAR_TOGGLE_DURATION_MS),
                            targetOffsetY = { it },
                        ),
                    ) {
                        if (customBottomBar != null) {
                            val customActions = remember(wrappedActions) {
                                MeetingRoomCustomActions(
                                    onToggleMic = wrappedActions.onToggleMic,
                                    onToggleCamera = wrappedActions.onToggleCamera,
                                    onEndCall = wrappedActions.onEndCall,
                                    onToggleRecording = wrappedActions.onToggleRecording,
                                    onToggleCaptions = wrappedActions.onToggleCaptions,
                                    onToggleScreenSharing = wrappedActions.onToggleScreenSharing,
                                )
                            }
                            customBottomBar(bottomBarState, customActions)
                        } else {
                            BottomBar(
                                modifier = Modifier.testTag(MEETING_ROOM_BOTTOM_BAR),
                                call = call,
                                roomActions = wrappedActions,
                                actions = remember(uiState.enabledFeatures) {
                                    enabledBottomBarActions(uiState.enabledFeatures)
                                },
                                additionalActions = mappedExtraActions,
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
                                reportingContent = reportingContent
                                    ?: { onDismiss -> DefaultReportingContent(onDismiss) },
                            )
                        }
                    }
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = { showBars = !showBars })
                                },
                        ) {
                            EmojiReactionOverlay(call = call)
                            CaptionsOverlay(captionLines = captionLines)
                            SpeakingWhileMutedOverlay(publisher = publisher)
                            RecordingStartedOverlay(isRecordingStartedByOthers = uiState.recordingStartedByOthers)
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
                        remainingBackgroundSlots = uiState.remainingBackgroundSlots,
                        onEffectSelect = { effect ->
                            selectedEffect = effect
                            actions.onApplyVideoEffect(effect)
                        },
                        onAddBackground = actions.onAddBackground,
                        onDeleteBackground = actions.onDeleteBackground,
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

    // Recording confirmation dialog
    if (showRecordingConfirmDialog && uiState.archivingUiState == ArchivingUiState.IDLE) {
        BasicAlertDialog(
            text = stringResource(R.string.recording_confirm_dialog_message),
            acceptLabel = stringResource(R.string.recording_confirm_button),
            cancelLabel = stringResource(R.string.generic_cancel),
            onAccept = {
                showRecordingConfirmDialog = false
                actions.onToggleRecording(true)
            },
            onCancel = {
                showRecordingConfirmDialog = false
            }
        )
    }
    if (showRecordingConfirmDialog && uiState.archivingUiState == ArchivingUiState.RECORDING) {
        BasicAlertDialog(
            text = stringResource(R.string.recording_stop_confirm_dialog_message),
            acceptLabel = stringResource(R.string.recording_stop_confirm_button),
            cancelLabel = stringResource(R.string.generic_cancel),
            onAccept = {
                showRecordingConfirmDialog = false
                actions.onToggleRecording(false)
            },
            onCancel = {
                showRecordingConfirmDialog = false
            }
        )
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

private const val BAR_TOGGLE_DURATION_MS = 300

internal object MeetingRoomScreenTestTags {
    const val MEETING_ROOM_SCREEN_TAG = "meeting-room-screen"
    const val MEETING_ROOM_TOP_BAR = "meeting-room-top-bar"
    const val MEETING_ROOM_CONTENT = "meeting-room-content"
    const val MEETING_ROOM_BOTTOM_BAR = "meeting-room-bottom-bar"
    const val MEETING_ROOM_PUBLISHER_EFFECTS_BUTTON = "meeting-room-publisher-effects-button"
}
