package com.vonage.android.screen.waiting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.permissions.LaunchVideoCallPermissions

@Composable
fun WaitingRoomRoute(
    modifier: Modifier = Modifier,
    viewModel: WaitingRoomViewModel = hiltViewModel(),
    roomName: String,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.init(context)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember {
        WaitingRoomActions(
            onUserNameChange = viewModel::updateUserName,
            onMicToggle = viewModel::onMicToggle,
            onCameraToggle = viewModel::onCameraToggle,
        )
    }

    LaunchVideoCallPermissions(Unit)

    WaitingRoomScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier,
        roomName = roomName,
    )
}

data class WaitingRoomActions(
    val onUserNameChange: (String) -> Unit = {},
    val onJoinRoom: () -> Unit = {},
    val onMicToggle: () -> Unit = {},
    val onCameraToggle: () -> Unit = {},
)
