package com.vonage.android.screen.room

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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.audio.ui.AudioDevices
import com.vonage.android.audio.ui.AudioDevicesEffect
import com.vonage.android.chat.ui.ChatPanel
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.components.GenericLoading
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.screen.room.components.bottombar.BottomBar
import com.vonage.android.screen.room.components.bottombar.BottomBarState
import com.vonage.android.screen.room.components.MeetingRoomContent
import com.vonage.android.screen.room.components.MeetingTopBar
import com.vonage.android.screen.room.components.captions.CaptionsOverlay
import com.vonage.android.screen.room.components.emoji.EmojiReactionOverlay
import com.vonage.android.util.ext.isExtraPaneShow
import com.vonage.android.util.ext.toggleChat
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    var showAudioOutputs by remember { mutableStateOf(false) }
    val audioOutputsSheetState = rememberModalBottomSheetState()

    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    LaunchedEffect(navigator.scaffoldValue) {
        actions.onListenUnread(navigator.isExtraPaneShow().not())
    }

    AudioDevicesEffect()

    val isChatShow by remember(navigator.scaffoldValue) {
        derivedStateOf { navigator.isExtraPaneShow() }
    }

    when {
        (uiState.isError.not() && uiState.isLoading.not() && uiState.isEndCall.not()) -> {
            val participants by uiState.call.participantsStateFlow.collectAsStateWithLifecycle()
            val publisher by uiState.call.publisher.collectAsStateWithLifecycle()
            Scaffold(
                modifier = modifier
                    .systemBarsPadding(),
                topBar = {
                    MeetingTopBar(
                        modifier = Modifier
                            .testTag(MEETING_ROOM_TOP_BAR),
                        roomName = uiState.roomName,
                        archivingUiState = uiState.archivingUiState,
                        actions = actions,
                        onToggleAudioDeviceSelector = { showAudioOutputs = showAudioOutputs.toggle() },
                    )
                },
                bottomBar = {
                    BottomBar(
                        modifier = Modifier
                            .testTag(MEETING_ROOM_BOTTOM_BAR),
                        call = uiState.call,
                        roomActions = actions,
                        state = BottomBarState(
                            onShowChat = { scope.launch { navigator.toggleChat() } },
                            isChatShow = isChatShow,
                            publisher = publisher,
                            participants = participants,
                            layoutType = uiState.layoutType,
                            archivingUiState = uiState.archivingUiState,
                            screenSharingState = uiState.screenSharingState,
                            captionsState = uiState.captionsState,
                            allowShowParticipantList = uiState.allowShowParticipantList,
                            allowMicrophoneControl = uiState.allowMicrophoneControl,
                            allowCameraControl = uiState.allowCameraControl,
                        ),
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                        ) {
                            EmojiReactionOverlay(call = uiState.call)
                            CaptionsOverlay(call = uiState.call)
                            MeetingRoomContent(
                                modifier = Modifier
                                    .testTag(MEETING_ROOM_CONTENT),
                                call = uiState.call,
                                participants = participants,
                                layoutType = uiState.layoutType,
                            )
                        }
                    },
                    supportingPane = { },
                    extraPane = {
                        ExtraPane(
                            call = uiState.call,
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
                    AudioDevices(
                        onDismissRequest = { showAudioOutputs = false },
                    )
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

    AnimatedPane(
        modifier = modifier
    ) {
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

@PreviewLightDark
@Composable
internal fun MeetingRoomScreenLoadingPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState(
                roomName = "room-name",
                isLoading = true,
            ),
            actions = MeetingRoomActions(),
        )
    }
}

@PreviewLightDark
@Composable
internal fun MeetingRoomScreenSessionErrorPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState(
                roomName = "room-name",
                isError = true,
            ),
            actions = MeetingRoomActions(),
        )
    }
}

@PreviewScreenSizes
@Composable
internal fun MeetingRoomScreenSessionPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState(
                roomName = "sample-room-name",
                archivingUiState = ArchivingUiState.RECORDING,
                call = buildCallWithParticipants(10),
                isLoading = false,
                isError = false,
            ),
            actions = MeetingRoomActions(),
        )
    }
}
