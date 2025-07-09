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
    navigateToRoom: (String, String, String) -> Unit,
) {
    val mainUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember {
        JoinMeetingRoomActions(
            onJoinRoomClick = viewModel::joinRoom,
            onCreateRoomClick = viewModel::createRoom,
            onRoomNameChange = viewModel::updateName
        )
    }

    JoinMeetingRoomScreen(
        mainUiState = mainUiState,
        actions = actions,
        modifier = modifier,
        navigateToRoom = navigateToRoom,
    )
}

object JoinMeetingRoomTestTags {
    const val SUBTITLE_TAG = "join_meeting_room_screen_subtitle"
    const val CREATE_ROOM_BUTTON_TAG = "join_meeting_room_screen_create_room_button"
}

@Stable
data class JoinMeetingRoomActions(
    val onJoinRoomClick: (String) -> Unit,
    val onCreateRoomClick: () -> Unit,
    val onRoomNameChange: (String) -> Unit,
)
