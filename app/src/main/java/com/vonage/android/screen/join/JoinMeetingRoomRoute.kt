package com.vonage.android.screen.join

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun JoinMeetingRoomRoute(
    modifier: Modifier = Modifier,
    viewModel: JoinMeetingRoomViewModel = hiltViewModel(),
    navigateToRoom: (JoinMeetingRoomRouteParams) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember {
        JoinMeetingRoomActions(
            onJoinRoomClick = viewModel::joinRoom,
            onCreateRoomClick = viewModel::createRoom,
            onRoomNameChange = viewModel::updateName
        )
    }

    JoinMeetingRoomScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier,
        navigateToRoom = navigateToRoom,
    )
}

object JoinMeetingRoomTestTags {
    const val TITLE_TAG = "join_meeting_room_screen_title"
    const val VONAGE_ICON_TAG = "join_meeting_room_screen_icon"
    const val SUBTITLE_TAG = "join_meeting_room_screen_subtitle"
    const val CREATE_ROOM_BUTTON_TAG = "join_meeting_room_screen_create_room_button"
    const val JOIN_BUTTON_TAG = "join_meeting_room_screen_join_button"
    const val ROOM_INPUT_TAG = "join_meeting_room_screen_room_input"
    const val ROOM_INPUT_ERROR_TAG = "join_meeting_room_screen_room_error_label"
    const val PROGRESS_INDICATOR_TAG = "join_meeting_room_screen_progress_indicator"
}

@Stable
data class JoinMeetingRoomActions(
    val onJoinRoomClick: (String) -> Unit,
    val onCreateRoomClick: () -> Unit,
    val onRoomNameChange: (String) -> Unit,
)

data class JoinMeetingRoomRouteParams(
    val roomName: String,
    val apiKey: String,
    val sessionId: String,
    val token: String,
)
