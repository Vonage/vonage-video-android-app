package com.vonage.android.meetingroom

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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.components.GenericLoading
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.meetingroom.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.meetingroom.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.meetingroom.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.meetingroom.components.MeetingRoomContent
import com.vonage.android.meetingroom.components.MeetingTopBar
import com.vonage.android.meetingroom.components.SpeakingWhileMutedOverlay
import com.vonage.android.meetingroom.components.bottombar.BottomBar
import com.vonage.android.meetingroom.ext.isExtraPaneShow
import com.vonage.android.meetingroom.ext.togglePanel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

object MeetingRoomScreenTestTags {
    const val MEETING_ROOM_TOP_BAR = "meeting_room_top_bar"
    const val MEETING_ROOM_CONTENT = "meeting_room_content"
    const val MEETING_ROOM_BOTTOM_BAR = "meeting_room_bottom_bar"
}

/**
 * The main meeting room screen composable.
 *
 * @param uiState       Current UI state for the call.
 * @param actions       Callbacks for all user interactions.
 * @param plugins       Feature plugins that contribute overlays, side-panel content,
 *                      and bottom-bar actions. The first plugin with [MeetingRoomUiPlugin.hasPanelContent]
 *                      == true is used as the extra pane.
 * @param audioDeviceSheetContent  Optional content for the audio device selection bottom sheet.
 *                      When null the audio device selector icon is hidden from the top bar.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    plugins: ImmutableList<MeetingRoomUiPlugin> = persistentListOf(),
    audioDeviceSheetContent: (@Composable (onDismiss: () -> Unit) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showAudioOutputs by remember { mutableStateOf(false) }
    val audioOutputsSheetState = rememberModalBottomSheetState()

    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    val panelPlugin = remember(plugins) { plugins.firstOrNull { it.hasPanelContent } }

    BackHandler(navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    LaunchedEffect(navigator.scaffoldValue) {
        actions.onListenUnread(navigator.isExtraPaneShow().not())
    }

    val isPanelOpen by remember(navigator.scaffoldValue) {
        derivedStateOf { navigator.isExtraPaneShow() }
    }

    CompositionLocalProvider(LocalMeetingRoomPlugins provides plugins) {
    when {
        (uiState.isError.not() && uiState.isLoading.not() && uiState.isEndCall.not()) -> {
            val participants by uiState.call.participantsStateFlow.collectAsStateWithLifecycle()
            val publisher by uiState.call.publisher.collectAsStateWithLifecycle()
            Scaffold(
                modifier = modifier.systemBarsPadding(),
                topBar = {
                    MeetingTopBar(
                        modifier = Modifier.testTag(MEETING_ROOM_TOP_BAR),
                        roomName = uiState.roomName,
                        actions = actions,
                        audioDevicesState = uiState.audioDevicesState,
                        onToggleAudioDeviceSelector = if (audioDeviceSheetContent != null) {
                            { showAudioOutputs = showAudioOutputs.toggle() }
                        } else null,
                        titleDecoration = {
                            plugins.forEach { it.TopBarTitleDecoration() }
                        },
                        extraActions = {
                            plugins.forEach { it.TopBarActions(actions) }
                        },
                    )
                },
                bottomBar = {
                    val pluginActions = plugins
                        .flatMap { plugin ->
                            plugin.bottomBarActions(
                                actions = actions,
                                isPanelOpen = isPanelOpen,
                                onTogglePanel = { scope.launch { navigator.togglePanel() } },
                            )
                        }
                        .toImmutableList()
                    BottomBar(
                        modifier = Modifier.testTag(MEETING_ROOM_BOTTOM_BAR),
                        call = uiState.call,
                        roomActions = actions,
                        publisher = publisher,
                        participants = participants,
                        layoutType = uiState.layoutType,
                        allowShowParticipantList = uiState.allowShowParticipantList,
                        allowMicrophoneControl = uiState.allowMicrophoneControl,
                        allowCameraControl = uiState.allowCameraControl,
                        pluginActions = pluginActions,
                        moreActionsContent = {
                            plugins.forEach { it.MoreActionsSheetContent(actions) }
                        },
                    )
                },
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
                            plugins.forEach { it.OverlayContent(uiState.call) }
                            SpeakingWhileMutedOverlay(publisher = publisher)
                            MeetingRoomContent(
                                modifier = Modifier.testTag(MEETING_ROOM_CONTENT),
                                call = uiState.call,
                                actions = actions,
                                participants = participants,
                                layoutType = uiState.layoutType,
                            )
                        }
                    },
                    supportingPane = {},
                    extraPane = {
                        panelPlugin?.let { plugin ->
                            PanelPane(
                                call = uiState.call,
                                plugin = plugin,
                                onClosePanel = { scope.launch { navigator.navigateBack() } },
                            )
                        }
                    },
                )
            }

            if (showAudioOutputs && audioDeviceSheetContent != null) {
                ModalBottomSheet(
                    onDismissRequest = { showAudioOutputs = false },
                    sheetState = audioOutputsSheetState,
                ) {
                    audioDeviceSheetContent {
                        scope.launch {
                            audioOutputsSheetState.hide()
                            showAudioOutputs = false
                        }
                    }
                }
            }
        }

        (uiState.isLoading) -> GenericLoading()

        (uiState.isError) -> {
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

        (uiState.isEndCall) -> {
            LaunchedEffect(uiState) {
                actions.onEndCall()
            }
        }
    }
    } // CompositionLocalProvider
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldPaneScope.PanelPane(
    call: CallFacade,
    plugin: MeetingRoomUiPlugin,
    onClosePanel: () -> Unit,
) {
    AnimatedPane {
        plugin.PanelContent(call = call, onClose = onClosePanel)
    }
}

@PreviewLightDark
@Composable
internal fun MeetingRoomScreenLoadingPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState(roomName = "room-name", isLoading = true),
            actions = MeetingRoomActions(),
        )
    }
}

@PreviewLightDark
@Composable
internal fun MeetingRoomScreenSessionErrorPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState(roomName = "room-name", isError = true),
            actions = MeetingRoomActions(),
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun MeetingRoomScreenPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState(
                roomName = "room-name",
                call = buildCallWithParticipants(),
            ),
            actions = MeetingRoomActions(),
        )
    }
}
