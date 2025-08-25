package com.vonage.android.screen.room

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.audio.AudioDevicesEffect
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.screen.room.components.BottomBar
import com.vonage.android.screen.room.components.BottomBarState
import com.vonage.android.screen.room.components.GenericLoading
import com.vonage.android.screen.room.components.MeetingRoomContent
import com.vonage.android.screen.room.components.TopBar
import com.vonage.android.screen.room.components.chat.ChatPanel
import com.vonage.android.util.ext.isExtraPaneShow
import com.vonage.android.util.ext.toggleChat
import com.vonage.android.util.preview.buildCallWithParticipants
import kotlinx.coroutines.launch

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    audioLevel: Float,
    modifier: Modifier = Modifier,
) {
    val participantsSheetState = rememberModalBottomSheetState()
    val audioDeviceSelectorSheetState = rememberModalBottomSheetState()
    var showParticipants by remember { mutableStateOf(false) }
    var showAudioDeviceSelector by remember { mutableStateOf(false) }

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

    when (uiState) {
        is MeetingRoomUiState.Content -> {
            val participants by uiState.call.participantsStateFlow.collectAsStateWithLifecycle()
            val chatState by uiState.call.chatStateFlow.collectAsStateWithLifecycle()
            val publisher = participants.filterIsInstance<VeraPublisher>().firstOrNull()

            Scaffold(
                modifier = modifier,
                bottomBar = {
                    BottomBar(
                        modifier = Modifier.testTag(MEETING_ROOM_BOTTOM_BAR),
                        actions = actions,
                        bottomBarState = BottomBarState(
                            onToggleParticipants = { showParticipants = showParticipants.toggle() },
                            onShowChat = { scope.launch { navigator.toggleChat() } },
                            isMicEnabled = publisher?.isMicEnabled ?: false,
                            isCameraEnabled = publisher?.isCameraEnabled ?: false,
                            isChatShow = isChatShow,
                            participantsCount = participants.size,
                            unreadCount = chatState.unreadCount,
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
                        Column(verticalArrangement = Arrangement.Top) {
                            TopBar(
                                modifier = Modifier.testTag(MEETING_ROOM_TOP_BAR),
                                roomName = uiState.roomName,
                                actions = actions,
                                onToggleAudioDeviceSelector = {
                                    showAudioDeviceSelector = showAudioDeviceSelector.toggle()
                                },
                            )
                            MeetingRoomContent(
                                modifier = Modifier.testTag(MEETING_ROOM_CONTENT),
                                participants = participants,
                                audioLevel = audioLevel,
                                showParticipants = showParticipants,
                                onDismissParticipants = { showParticipants = false },
                                participantsSheetState = participantsSheetState,
                                audioDeviceSelectorSheetState = audioDeviceSelectorSheetState,
                                showAudioDeviceSelector = showAudioDeviceSelector,
                                onDismissAudioDeviceSelector = { showAudioDeviceSelector = false },
                            )
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

        is MeetingRoomUiState.Loading -> GenericLoading()

        is MeetingRoomUiState.SessionError -> {
            BasicAlertDialog(
                text = stringResource(R.string.meeting_screen_session_creation_error),
                acceptLabel = stringResource(R.string.generic_retry),
                onAccept = actions.onRetry,
                onCancel = actions.onBack,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldPaneScope.ExtraPane(
    chatState: ChatState,
    actions: MeetingRoomActions,
    onCloseChat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedPane(
        modifier = modifier
            .safeContentPadding()
    ) {
        ChatPanel(
            messages = chatState.messages,
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
            uiState = MeetingRoomUiState.Loading,
            actions = MeetingRoomActions(),
            audioLevel = 0.5f,
        )
    }
}

@PreviewLightDark
@Composable
internal fun MeetingRoomScreenSessionErrorPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState.SessionError,
            actions = MeetingRoomActions(),
            audioLevel = 0.5f,
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun MeetingRoomScreenSessionPreview() {
    VonageVideoTheme {
        MeetingRoomScreen(
            uiState = MeetingRoomUiState.Content(
                roomName = "sample-room-name",
                call = buildCallWithParticipants(1),
            ),
            actions = MeetingRoomActions(),
            audioLevel = 0.5f,
        )
    }
}
