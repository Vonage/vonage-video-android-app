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

@Stable
data class JoinMeetingRoomActions(
    val onJoinRoomClick: (String) -> Unit,
    val onCreateRoomClick: () -> Unit,
    val onRoomNameChange: (String) -> Unit,
)
