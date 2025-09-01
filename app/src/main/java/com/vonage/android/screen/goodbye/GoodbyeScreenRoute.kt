package com.vonage.android.screen.goodbye

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.vonage.android.util.pip.pipEffect

@Composable
fun GoodbyeScreenRoute(
    roomName: String,
    navigateToMeeting: (String) -> Unit,
    navigateToLanding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pipModifier = pipEffect(false)

    val actions = remember {
        GoodbyeScreenActions(
            onReEnter = { navigateToMeeting(roomName) },
            onGoHome = navigateToLanding,
            onBack = { navigateToMeeting(roomName) },
        )
    }

    BackHandler {
        navigateToMeeting(roomName)
    }

    GoodbyeScreen(
        modifier = modifier.then(pipModifier),
        actions = actions,
    )
}

@Stable
data class GoodbyeScreenActions(
    val onReEnter: () -> Unit = {},
    val onGoHome: () -> Unit = {},
    val onBack: () -> Unit = {},
)
