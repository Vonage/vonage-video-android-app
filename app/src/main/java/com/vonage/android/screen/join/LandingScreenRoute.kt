package com.vonage.android.screen.join

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.util.pip.pipEffect

@Composable
internal fun LandingScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: LandingScreenViewModel = hiltViewModel(),
    navigateToRoom: (JoinMeetingRoomRouteParams) -> Unit,
) {
    val pipModifier = pipEffect(shouldEnterPipMode = false)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember {
        JoinMeetingRoomActions(
            onJoinRoomClick = viewModel::joinRoom,
            onCreateRoomClick = viewModel::createRoom,
            onRoomNameChange = viewModel::updateName
        )
    }

    LandingScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier.then(pipModifier),
        navigateToRoom = navigateToRoom,
    )
}

object LandingScreenTestTags {
    const val TITLE_TAG = "landing_screen_title"
    const val VONAGE_ICON_TAG = "landing_screen_icon"
    const val SUBTITLE_TAG = "landing_screen_subtitle"
    const val CREATE_ROOM_BUTTON_TAG = "landing_screen_create_room_button"
    const val JOIN_BUTTON_TAG = "landing_screen_join_button"
    const val ROOM_INPUT_TAG = "landing_screen_room_input"
    const val ROOM_INPUT_ERROR_TAG = "landing_screen_room_error_label"
}

@Stable
data class JoinMeetingRoomActions(
    val onJoinRoomClick: (String) -> Unit = {},
    val onCreateRoomClick: () -> Unit = {},
    val onRoomNameChange: (String) -> Unit = {},
)

data class JoinMeetingRoomRouteParams(
    val roomName: String,
)
