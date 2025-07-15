package com.vonage.android.screen.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RoomScreenRoute(
    roomName: String,
    modifier: Modifier = Modifier,
    viewModel: RoomScreenViewModel = hiltViewModel(),
    navigateToGoodBye: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init(context, roomName)
    }

    VideoCallScreen(
        modifier = modifier,
        uiState = uiState,
        onEndCall = {
            viewModel.endCall()
            navigateToGoodBye()
        }
    )
}

@Stable
data class RoomActions(
    val onUserNameChange: (String) -> Unit = {},
    val onJoinRoom: (String) -> Unit = {},
    val onMicToggle: () -> Unit = {},
    val onCameraToggle: () -> Unit = {},
)
