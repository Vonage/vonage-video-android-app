package com.vonage.android.screen.waiting

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
    onBack: () -> Unit,
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
            onCameraSwitch = viewModel::onCameraSwitch,
            onCameraBlur = viewModel::setBlur,
            onBack = {
                viewModel.onStop()
                onBack()
            },
        )
    }

    BackHandler {
        viewModel.onStop()
        onBack()
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

object WaitingRoomTestTags {
    const val JOIN_BUTTON_TAG = "waiting_room_join_button"
    const val PREPARE_TO_JOIN_TEXT_TAG = "waiting_room_prepare_to_join_text"
    const val ROOM_NAME_TEXT_TAG = "waiting_room_room_name_text"
    const val WHATS_YOU_NAME_TEXT_TAG = "waiting_room_whats_you_name_text"
    const val USER_NAME_INPUT_TAG = "waiting_room_user_name_input"
    const val MIC_BUTTON_TAG = "waiting_room_mic_button"
    const val CAMERA_BUTTON_TAG = "waiting_room_camera_button"
    const val USER_INITIALS_TAG = "user_initials_view"
}

@Stable
data class WaitingRoomActions(
    val onUserNameChange: (String) -> Unit = {},
    val onJoinRoom: (String) -> Unit = {},
    val onMicToggle: () -> Unit = {},
    val onCameraToggle: () -> Unit = {},
    val onCameraBlur: () -> Unit = {},
    val onCameraSwitch: () -> Unit = {},
    val onAudioSwitch: () -> Unit = {},
    val onBack: () -> Unit = {},
)
