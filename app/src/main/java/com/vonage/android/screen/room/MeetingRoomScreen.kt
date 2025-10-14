package com.vonage.android.screen.room

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.audio.ui.AudioDevicesEffect
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.model.VideoSource
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.screen.room.components.BottomBar
import com.vonage.android.screen.room.components.BottomBarState
import com.vonage.android.screen.room.components.GenericLoading
import com.vonage.android.screen.room.components.MeetingRoomContent
import com.vonage.android.screen.room.components.TopBar
import com.vonage.android.screen.room.components.captions.CaptionsOverlay
import com.vonage.android.screen.room.components.chat.ChatPanel
import com.vonage.android.screen.room.components.emoji.EmojiReactionOverlay
import com.vonage.android.util.ext.isExtraPaneShow
import com.vonage.android.util.ext.toggleChat
import com.vonage.android.util.preview.buildCallWithParticipants
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    val participantsSheetState = rememberModalBottomSheetState()
    val audioDeviceSelectorSheetState = rememberModalBottomSheetState()
    val moreActionsSheetState = rememberModalBottomSheetState()
    val reportSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showParticipants by remember { mutableStateOf(false) }
    var showAudioDeviceSelector by remember { mutableStateOf(false) }
    var showMoreActions by remember { mutableStateOf(false) }
    var showReporting by remember { mutableStateOf(false) }

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
            val participants by uiState.call.participantsStateFlow.collectAsStateWithLifecycle(persistentListOf())
            val signalState by uiState.call.signalStateFlow.collectAsStateWithLifecycle(null)
            val chatState = signalState?.signals[SignalType.CHAT.signal] as? ChatState
            val emojiState = signalState?.signals[SignalType.REACTION.signal] as? EmojiState
            val captions by uiState.call.captionsStateFlow.collectAsStateWithLifecycle()
            val publisher = participants.filterIsInstance<VeraPublisher>().firstOrNull()

            Scaffold(
                modifier = modifier,
                bottomBar = {
                    BottomBar(
                        modifier = Modifier.testTag(MEETING_ROOM_BOTTOM_BAR),
                        actions = actions,
                        bottomBarState = BottomBarState(
                            onToggleParticipants = { showParticipants = showParticipants.toggle() },
                            onToggleMoreActions = { showMoreActions = showMoreActions.toggle() },
                            onShowChat = { scope.launch { navigator.toggleChat() } },
                            isMicEnabled = publisher?.isMicEnabled ?: MutableStateFlow(false),
                            isCameraEnabled = publisher?.isCameraEnabled ?: MutableStateFlow(false),
                            isChatShow = isChatShow,
                            participantsCount = uiState.call.participantsCount,
                            unreadCount = chatState?.unreadCount ?: 0,
                            layoutType = uiState.layoutType,
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
                        Box(modifier = Modifier.fillMaxSize()) {
                            EmojiReactionOverlay(
                                reactions = emojiState?.reactions.orEmpty(),
                            )
                            CaptionsOverlay(
                                captions = captions,
                            )
                            Column(verticalArrangement = Arrangement.Top) {
                                TopBar(
                                    modifier = Modifier.testTag(MEETING_ROOM_TOP_BAR),
                                    roomName = uiState.roomName,
                                    recordingState = uiState.recordingState,
                                    actions = actions,
                                    onToggleAudioDeviceSelector = {
                                        showAudioDeviceSelector = showAudioDeviceSelector.toggle()
                                    },
                                )
                                MeetingRoomContent(
                                    modifier = Modifier.testTag(MEETING_ROOM_CONTENT),
                                    actions = actions,
                                    participants = participants,
                                    call = uiState.call,
                                    showParticipants = showParticipants,
                                    showMoreActions = showMoreActions,
                                    showReporting = showReporting,
                                    showAudioDeviceSelector = showAudioDeviceSelector,
                                    participantsSheetState = participantsSheetState,
                                    audioDeviceSelectorSheetState = audioDeviceSelectorSheetState,
                                    moreActionsSheetState = moreActionsSheetState,
                                    reportSheetState = reportSheetState,
                                    onDismissParticipants = { showParticipants = false },
                                    onDismissAudioDeviceSelector = { showAudioDeviceSelector = false },
                                    onDismissMoreActions = { showMoreActions = false },
                                    onShowReporting = { showReporting = showReporting.toggle() },
                                    onDismissReporting = { showReporting = false },
                                    recordingState = uiState.recordingState,
                                    screenSharingState = uiState.screenSharingState,
                                    captionsState = uiState.captionsState,
                                    onEmojiClick = actions.onEmojiSent,
                                    layoutType = uiState.layoutType,
                                )
                            }
                        }
                    },
                    supportingPane = { },
                    extraPane = {
                        ExtraPane(
                            chatState = chatState,
                            actions = actions,
                            onCloseChat = { scope.launch { navigator.navigateBack() } }
                        )
                    }
                )
            }
        }

        (uiState.isLoading) -> GenericLoading()

        (uiState.isError) -> {
            BasicAlertDialog(
                text = stringResource(R.string.meeting_screen_session_creation_error),
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
fun ThreePaneScaffoldPaneScope.ExtraPane(
    chatState: ChatState?,
    actions: MeetingRoomActions,
    onCloseChat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedPane(
        modifier = modifier
            .padding(16.dp)
    ) {
        ChatPanel(
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

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun MeetingRoomScreenSessionPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState(
                roomName = "sample-room-name",
                recordingState = RecordingState.RECORDING,
                call = buildCallWithParticipants(1),
                isLoading = false,
                isError = false,
            ),
            actions = MeetingRoomActions(),
        )
    }
}
