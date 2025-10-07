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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.util.pip.findActivity
import com.vonage.android.util.pip.pipEffect
import com.vonage.android.util.pip.rememberIsInPipMode
import kotlinx.coroutines.launch

@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeetingRoomScreenViewModel =
        hiltViewModel<MeetingRoomScreenViewModel, MeetingRoomViewModelFactory>(key = roomName) { factory ->
            factory.create(roomName)
        },
) {
    val currentActivity = LocalContext.current.findActivity()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inPipMode = rememberIsInPipMode()
    val pipModifier = pipEffect()

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        if (!currentActivity.isInPictureInPictureMode) {
            viewModel.onPause()
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (!currentActivity.isInPictureInPictureMode) {
            viewModel.onResume()
        }
    }

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
            }
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
    val onAudioSwitch: () -> Unit = {},
    val onMessageSent: (String) -> Unit = {},
    val onListenUnread: (Boolean) -> Unit = {},
    val onToggleRecording: (Boolean) -> Unit = {},
    val onToggleCaptions: (Boolean) -> Unit = {},
    val onToggleScreenSharing: (Boolean) -> Unit = {},
    val onShowFeedbackScreen: () -> Unit = {},
)
