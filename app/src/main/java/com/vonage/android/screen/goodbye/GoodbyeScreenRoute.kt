package com.vonage.android.screen.goodbye

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vonage.android.util.pip.pipEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.archiving.Archive

@Composable
fun GoodbyeScreenRoute(
    roomName: String,
    navigateToMeeting: (String) -> Unit,
    navigateToLanding: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoodbyeScreenViewModel =
        hiltViewModel<GoodbyeScreenViewModel, GoodbyeScreenViewModelFactory> { factory ->
            factory.create(roomName)
        },
) {
    val pipModifier = pipEffect(shouldEnterPipMode = false)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember {
        GoodbyeScreenActions(
            onReEnter = { navigateToMeeting(roomName) },
            onGoHome = navigateToLanding,
            onBack = { navigateToMeeting(roomName) },
            onDownloadArchive = { archive ->
                viewModel.downloadArchive(archive)
            }
        )
    }

    BackHandler {
        navigateToMeeting(roomName)
    }

    GoodbyeScreen(
        uiState = uiState,
        modifier = modifier.then(pipModifier),
        actions = actions,
    )
}

@Stable
data class GoodbyeScreenActions(
    val onReEnter: () -> Unit = {},
    val onGoHome: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onDownloadArchive: (Archive) -> Unit = {},
)
