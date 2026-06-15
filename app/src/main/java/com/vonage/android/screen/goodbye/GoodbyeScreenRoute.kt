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
    navigateToWaiting: (String) -> Unit,
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
            onReEnter = { navigateToWaiting(roomName) },
            onGoHome = navigateToLanding,
            onBack = { navigateToWaiting(roomName) },
            onDownloadArchive = { archive ->
                viewModel.downloadArchive(archive)
            }
        )
    }

    BackHandler {
        navigateToWaiting(roomName)
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

object GoodbyeScreenTestTags {
    const val GOODBYE_SCREEN_TAG = "goodbye-screen"
    const val GOODBYE_HEADER_TAG = "goodbye-header"
    const val GOODBYE_REJOIN_CONTAINER_TAG = "goodbye-rejoin-container"
    const val GOODBYE_ARCHIVES_CONTAINER_TAG = "goodbye-archives-container"
    const val GOODBYE_REJOIN_BUTTON_TAG = "goodbye-reenter-button"
    const val GOODBYE_GO_HOME_BUTTON_TAG = "goodbye-landing-page-button"
}
