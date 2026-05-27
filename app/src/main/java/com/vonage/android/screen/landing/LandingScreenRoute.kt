package com.vonage.android.screen.landing

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
    navigateToRoom: (LandingScreenRouteParams) -> Unit,
) {
    val pipModifier = pipEffect(shouldEnterPipMode = false)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember {
        LandingScreenActions(
            onJoinRoomClick = viewModel::joinRoom,
            onCreateRoomClick = viewModel::createRoom,
            onRoomNameChange = viewModel::updateName,
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
    const val LANDING_SCREEN_TAG = "landing-screen"
    const val TITLE_TAG = "landing-screen-title"
    const val VONAGE_ICON_TAG = "landing-screen-icon"
    const val SUBTITLE_TAG = "landing-screen-subtitle"
    const val CREATE_ROOM_BUTTON_TAG = "landing-screen-create-room-button"
    const val JOIN_BUTTON_TAG = "join-waiting-room-button"
    const val ROOM_INPUT_TAG = "room-name-input"
    const val ROOM_INPUT_ERROR_TAG = "landing-screen-room-error-label"
    const val GITHUB_REPO_BUTTON_TAG = "github-repo-button"
}

@Stable
data class LandingScreenActions(
    val onJoinRoomClick: (String) -> Unit = {},
    val onCreateRoomClick: () -> Unit = {},
    val onRoomNameChange: (String) -> Unit = {},
)

data class LandingScreenRouteParams(
    val roomName: String,
)
