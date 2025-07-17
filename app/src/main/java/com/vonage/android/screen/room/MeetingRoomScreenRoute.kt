package com.vonage.android.screen.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    modifier: Modifier = Modifier,
    viewModel: MeetingRoomScreenViewModel = hiltViewModel(),
    navigateToGoodBye: () -> Unit,
) {
    val context = LocalContext.current
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
            }
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
    val onToggleMic: () -> Unit,
    val onToggleCamera: () -> Unit,
    val onToggleParticipants: () -> Unit,
    val onEndCall: () -> Unit,
)
