package com.vonage.android.screen.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeetingRoomScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init(roomName)
    }

    val actions = remember {
        MeetingRoomActions(
            onToggleMic = viewModel::onToggleMic,
            onToggleCamera = viewModel::onToggleCamera,
            onToggleParticipants = viewModel::onToggleParticipants,
            onEndCall = {
                viewModel.endCall()
                navigateToGoodBye()
            },
            onShare = navigateToShare,
        )
    }

    MeetingRoomScreen(
        modifier = modifier,
        actions = actions,
        uiState = uiState,
    )
}

@Stable
data class MeetingRoomActions(
    val onShare: (String) -> Unit,
    val onToggleMic: () -> Unit,
    val onToggleCamera: () -> Unit,
    val onToggleParticipants: () -> Unit,
    val onEndCall: () -> Unit,
)
