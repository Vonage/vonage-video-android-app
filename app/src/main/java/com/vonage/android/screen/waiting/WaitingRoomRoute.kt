package com.vonage.android.screen.waiting

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.screen.components.permissions.CallPermissionHandler
import com.vonage.android.util.pip.pipEffect

@Composable
fun WaitingRoomRoute(
    roomName: String,
    navigateToRoom: (String, PublisherSettings) -> Unit,
    navigateToPermissions: () -> Unit,
    navigateToSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WaitingRoomViewModel = hiltViewModel<WaitingRoomViewModel, WaitingRoomViewModelFactory> { factory ->
        factory.create(roomName)
    },
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var permissionsAlreadyGranted by rememberSaveable { mutableStateOf(true) }
    val pipModifier = pipEffect(shouldEnterPipMode = false)

    val actions = remember {
        WaitingRoomActions(
            onUserNameChange = viewModel::updateUserName,
            onMicToggle = viewModel::onMicToggle,
            onCameraToggle = viewModel::onCameraToggle,
            onJoinRoom = { userName -> viewModel.joinRoom(userName) },
            onCameraSwitch = viewModel::onCameraSwitch,
            onApplyVideoEffect = viewModel::applyVideoEffect,
            onAddBackground = viewModel::addBackground,
            onDeleteBackground = viewModel::deleteBackground,
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

    CallPermissionHandler(
        onGrantPermissions = {
            if (permissionsAlreadyGranted) {
                viewModel.init(context)
            }
            permissionsAlreadyGranted = false
        },
        navigateToPermissions = navigateToPermissions,
    )

    WaitingRoomScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier.then(pipModifier),
        navigateToRoom = navigateToRoom,
        navigateToSettings = navigateToSettings,
    )
}

object WaitingRoomTestTags {
    const val WAITING_ROOM_SCREEN_TAG = "waiting-room-screen"
    const val JOIN_BUTTON_TAG = "join-meeting-button"
    const val PREPARE_TO_JOIN_TEXT_TAG = "waiting-room-prepare-to-join-text"
    const val ROOM_NAME_TEXT_TAG = "waiting-room-room-name-text"
    const val WHATS_YOU_NAME_TEXT_TAG = "waiting-room-whats-your-name-text"
    const val USER_NAME_INPUT_TAG = "username-input"
    const val USER_NAME_INPUT_ERROR_TAG = "waiting-room-user-name-input-error"
    const val MIC_BUTTON_TAG = "waiting-room-mic"
    const val VOLUME_INDICATOR_TAG = "waiting-room-volume-indicator"
    const val CAMERA_BUTTON_TAG = "waiting-room-camera"
    const val CAMERA_BLUR_BUTTON_TAG = "waiting-room-camera-blur-button"
    const val USER_INITIALS_TAG = "user-initials-view"
}

@Stable
data class WaitingRoomActions(
    val onUserNameChange: (String) -> Unit = {},
    val onJoinRoom: (String) -> Unit = {},
    val onMicToggle: () -> Unit = {},
    val onCameraToggle: () -> Unit = {},
    val onOpenVideoEffects: () -> Unit = {},
    val onApplyVideoEffect: (VideoEffect) -> Unit = {},
    val onAddBackground: (List<Uri>) -> Unit = {},
    val onDeleteBackground: (VideoBackgroundItem) -> Unit = {},
    val onCameraSwitch: () -> Unit = {},
    val onBack: () -> Unit = {},
)
