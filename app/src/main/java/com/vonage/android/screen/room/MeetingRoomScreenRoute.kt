package com.vonage.android.screen.room

import android.app.Activity
import android.media.projection.MediaProjectionManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.meetingroom.CallLayoutType as MeetingCallLayoutType
import com.vonage.android.meetingroom.MeetingRoomActions as NewMeetingRoomActions
import com.vonage.android.meetingroom.MeetingRoomScreen
import com.vonage.android.meetingroom.MeetingRoomUiPlugin
import com.vonage.android.meetingroom.MeetingRoomUiState as NewMeetingRoomUiState
import com.vonage.android.meetingroom.PipMeetingRoomScreen
import com.vonage.android.plugins.ArchivingUiPlugin
import com.vonage.android.plugins.CaptionsUiPlugin
import com.vonage.android.plugins.ChatUiPlugin
import com.vonage.android.plugins.ReactionsUiPlugin
import com.vonage.android.plugins.ReportingUiPlugin
import com.vonage.android.plugins.ScreenSharingUiPlugin
import com.vonage.android.plugins.SettingsUiPlugin
import com.vonage.android.screen.components.audio.AudioDevicesMenu
import com.vonage.android.util.pip.pipEffect
import com.vonage.android.util.pip.rememberIsInPipMode
import com.vonage.android.util.rememberNoiseSuppression
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Suppress("LongMethod")
@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    navigateToSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeetingRoomScreenViewModel =
        hiltViewModel<MeetingRoomScreenViewModel, MeetingRoomViewModelFactory>(key = roomName) { factory ->
            factory.create(roomName)
        },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inPipMode = rememberIsInPipMode()
    val pipModifier = pipEffect()

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val screenSharePermissionResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { data -> viewModel.startScreenSharing(data) }
            }
        },
    )

    LaunchedEffect(Unit) {
        viewModel.setup(context)
    }

    val actions = remember {
        NewMeetingRoomActions(
            onToggleMic = viewModel::onToggleMic,
            onToggleCamera = viewModel::onToggleCamera,
            onCameraSwitch = viewModel::onSwitchCamera,
            onCycleCameraBlur = viewModel::onCycleLocalCameraBlur,
            onEndCall = {
                viewModel.endCall()
                if (!inPipMode) {
                    navigateToGoodBye()
                }
            },
            onShare = navigateToShare,
            onRetry = {
                viewModel.setup(context)
            },
            onBack = {
                viewModel.endCall()
                onBack()
            },
            onMessageSent = {
                viewModel.sendMessage(it)
            },
            onListenUnread = {
                viewModel.listenUnread(it)
            },
            onEmojiSent = {
                viewModel.sendEmoji(it)
            },
            onToggleRecording = { enable ->
                viewModel.archiveCall(enable)
            },
            onToggleCaptions = { enable ->
                viewModel.captions(enable)
            },
            onToggleScreenSharing = { enable ->
                if (enable) {
                    scope.launch {
                        val mediaProjectionManager = context.getSystemService(MediaProjectionManager::class.java)
                        screenSharePermissionResult.launch(mediaProjectionManager.createScreenCaptureIntent())
                    }
                } else {
                    viewModel.stopScreenSharing()
                }
            },
            onChangeLayout = { layoutType ->
                viewModel.changeLayout(
                    when (layoutType) {
                        MeetingCallLayoutType.GRID -> CallLayoutType.GRID
                        MeetingCallLayoutType.SPEAKER_LAYOUT -> CallLayoutType.SPEAKER_LAYOUT
                        MeetingCallLayoutType.ADAPTIVE_GRID -> CallLayoutType.ADAPTIVE_GRID
                    }
                )
            },
            onSettings = navigateToSettings,
            onTogglePinParticipant = viewModel::onTogglePinParticipant,
            onForceMuteParticipant = viewModel::forceMuteParticipant,
        )
    }

    val newUiState = remember(uiState) {
        NewMeetingRoomUiState(
            roomName = uiState.roomName,
            call = uiState.call,
            audioDevicesState = uiState.audioDevicesState,
            isLoading = uiState.isLoading,
            isError = uiState.isError,
            errorMessage = uiState.errorMessage,
            isEndCall = uiState.isEndCall,
            layoutType = when (uiState.layoutType) {
                CallLayoutType.GRID -> MeetingCallLayoutType.GRID
                CallLayoutType.SPEAKER_LAYOUT -> MeetingCallLayoutType.SPEAKER_LAYOUT
                CallLayoutType.ADAPTIVE_GRID -> MeetingCallLayoutType.ADAPTIVE_GRID
            },
            allowMicrophoneControl = uiState.allowMicrophoneControl,
            allowCameraControl = uiState.allowCameraControl,
            allowShowParticipantList = uiState.allowShowParticipantList,
        )
    }

    val plugins = remember {
        persistentListOf<MeetingRoomUiPlugin>(
            ArchivingUiPlugin(viewModel.archivingUiState),
            CaptionsUiPlugin(viewModel.captionsUiState),
            ChatUiPlugin(viewModel.chatSignalState),
            ReactionsUiPlugin(),
            ScreenSharingUiPlugin(viewModel.screenSharingState),
            SettingsUiPlugin(),
            ReportingUiPlugin(),
        )
    }

    BackHandler {
        viewModel.endCall()
        onBack()
    }

    if (inPipMode) {
        PipMeetingRoomScreen(
            modifier = modifier.then(pipModifier),
            actions = actions,
            uiState = newUiState,
        )
    } else {
        MeetingRoomScreen(
            modifier = modifier.then(pipModifier),
            actions = actions,
            uiState = newUiState,
            plugins = plugins,
            audioDeviceSheetContent = uiState.audioDevicesState?.let { audioDevicesState ->
                { onDismiss ->
                    val publisher by uiState.call.publisher.collectAsStateWithLifecycle()
                    val noiseSuppression by rememberNoiseSuppression(publisher)
                        .collectAsStateWithLifecycle()
                    AudioDevicesMenu(
                        audioDevicesState = audioDevicesState,
                        onDismissRequest = onDismiss,
                        noiseSuppressionEnabled = noiseSuppression.isEnabled(),
                        onNoiseSuppressorToggle = { publisher?.toggleNoiseSuppression() },
                    )
                }
            },
        )
    }
}

object MeetingRoomScreenTestTags {
    const val MEETING_ROOM_TOP_BAR = "meeting_room_top_bar"
    const val MEETING_ROOM_CONTENT = "meeting_room_content"
    const val MEETING_ROOM_BOTTOM_BAR = "meeting_room_bottom_bar"
}

@Stable
data class MeetingRoomActions(
    val onShare: (String) -> Unit = {},
    val onRetry: () -> Unit = {},
    val onEmojiSent: (String) -> Unit = {},
    val onToggleMic: () -> Unit = {},
    val onToggleCamera: () -> Unit = {},
    val onEndCall: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onCameraSwitch: () -> Unit = {},
    val onCycleCameraBlur: () -> Unit = {},
    val onAudioSwitch: () -> Unit = {},
    val onMessageSent: (String) -> Unit = {},
    val onListenUnread: (Boolean) -> Unit = {},
    val onToggleRecording: (Boolean) -> Unit = {},
    val onToggleCaptions: (Boolean) -> Unit = {},
    val onToggleScreenSharing: (Boolean) -> Unit = {},
    val onShowFeedbackScreen: () -> Unit = {},
    val onChangeLayout: (CallLayoutType) -> Unit = {},
    val onSettings: () -> Unit = {},
    val onTogglePinParticipant: (String) -> Unit = {},
    val onForceMuteParticipant: (String) -> Unit = {},
)
