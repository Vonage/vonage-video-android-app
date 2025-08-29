package com.vonage.android.screen.goodbye

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.util.pip.rememberIsInPipMode

@Composable
fun GoodbyeScreenRoute(
    roomName: String,
    navigateToMeeting: (String) -> Unit,
    navigateToLanding: () -> Unit,
    modifier: Modifier = Modifier,
) {
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

    val inPipMode = rememberIsInPipMode()
    if (inPipMode) {
        Box(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            GoodbyeScreenHeader()
        }
    } else {
        GoodbyeScreen(
            modifier = modifier,
            actions = actions,
        )
    }
}

@Stable
data class GoodbyeScreenActions(
    val onReEnter: () -> Unit = {},
    val onGoHome: () -> Unit = {},
    val onBack: () -> Unit = {},
)
