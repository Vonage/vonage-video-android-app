package com.vonage.android.meetingroom

import android.app.Activity
import android.media.projection.MediaProjectionManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vonage.android.meetingroom.pip.pipEffect
import com.vonage.android.meetingroom.pip.rememberIsInPipMode
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    viewModelFactory: ViewModelProvider.Factory,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    navigateToSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeetingRoomScreenViewModel = viewModel(key = roomName, factory = viewModelFactory),
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
        MeetingRoomActions(
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
                viewModel.changeLayout(layoutType)
            },
            onSettings = navigateToSettings,
            onTogglePinParticipant = viewModel::onTogglePinParticipant,
            onForceMuteParticipant = viewModel::forceMuteParticipant,
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
            uiState = uiState,
        )
    } else {
        MeetingRoomScreen(
            modifier = modifier.then(pipModifier),
            actions = actions,
            uiState = uiState,
        )
    }
}

object MeetingRoomScreenTestTags {
    const val MEETING_ROOM_TOP_BAR = "meeting_room_top_bar"
    const val MEETING_ROOM_CONTENT = "meeting_room_content"
    const val MEETING_ROOM_BOTTOM_BAR = "meeting_room_bottom_bar"
}

