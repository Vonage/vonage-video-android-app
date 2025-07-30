package com.vonage.android.screen.room

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeetingRoomScreenViewModel =
        hiltViewModel<MeetingRoomScreenViewModel, MeetingRoomViewModelFactory> { factory ->
            factory.create(roomName)
        },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.onPause()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onResume()
    }

    val actions = remember {
        MeetingRoomActions(
            onToggleMic = viewModel::onToggleMic,
            onToggleCamera = viewModel::onToggleCamera,
            onCameraSwitch = viewModel::onSwitchCamera,
            onEndCall = {
                viewModel.endCall()
                navigateToGoodBye()
            },
            onShare = navigateToShare,
            onRetry = {
                viewModel.init()
            },
            onBack = {
                viewModel.endCall()
                onBack()
            }
        )
    }

    BackHandler {
        viewModel.endCall()
        onBack()
    }

    MeetingRoomScreen(
        modifier = modifier,
        actions = actions,
        uiState = uiState,
    )
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
    val onToggleMic: () -> Unit = {},
    val onToggleCamera: () -> Unit = {},
    val onEndCall: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onCameraSwitch: () -> Unit = {},
    val onAudioSwitch: () -> Unit = {},
)
