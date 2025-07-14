package com.vonage.android.screen.waiting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WaitingRoomRoute(
    roomName: String,
    modifier: Modifier = Modifier,
    viewModel: WaitingRoomViewModel = hiltViewModel(),
    navigateToRoom: (String) -> Unit,
    navigateToPermissions: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember {
        WaitingRoomActions(
            onUserNameChange = viewModel::updateUserName,
            onMicToggle = viewModel::onMicToggle,
            onCameraToggle = viewModel::onCameraToggle,
            onJoinRoom = { viewModel.joinRoom(roomName) },
        )
    }

    WaitingRoomScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier,
        roomName = roomName,
        navigateToRoom = navigateToRoom,
        navigateToPermissions = navigateToPermissions,
        onGrantPermissions = { viewModel.init(context) },
    )
}

data class WaitingRoomActions(
    val onUserNameChange: (String) -> Unit = {},
    val onJoinRoom: () -> Unit = {},
    val onMicToggle: () -> Unit = {},
    val onCameraToggle: () -> Unit = {},
)
