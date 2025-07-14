package com.vonage.android.screen.waiting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WaitingRoomRoute(
    roomName: String,
    navigateToRoom: (String) -> Unit,
    navigateToPermissions: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WaitingRoomViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember {
        WaitingRoomActions(
            onUserNameChange = viewModel::updateUserName,
            onMicToggle = viewModel::onMicToggle,
            onCameraToggle = viewModel::onCameraToggle,
            onJoinRoom = { userName -> viewModel.joinRoom(roomName, userName) },
        )
    }

    WaitingRoomScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier,
        navigateToRoom = navigateToRoom,
        navigateToPermissions = navigateToPermissions,
        onGrantPermissions = { viewModel.init(roomName) },
    )
}

data class WaitingRoomActions(
    val onUserNameChange: (String) -> Unit = {},
    val onJoinRoom: (String) -> Unit = {},
    val onMicToggle: () -> Unit = {},
    val onCameraToggle: () -> Unit = {},
)
