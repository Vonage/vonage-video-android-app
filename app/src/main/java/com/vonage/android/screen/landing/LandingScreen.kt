package com.vonage.android.screen.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.layout.TwoPaneScaffold
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.landing.LandingScreenTestTags.GITHUB_REPO_BUTTON_TAG
import com.vonage.android.screen.landing.LandingScreenTestTags.LANDING_SCREEN_TAG
import com.vonage.android.screen.landing.components.LandingScreenContent
import com.vonage.android.screen.landing.components.LandingScreenHeader

@Composable
fun LandingScreen(
    uiState: LandingScreenUiState,
    actions: LandingScreenActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (LandingScreenRouteParams) -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current

    when (uiState) {
        is LandingScreenUiState.Content -> {
            Box(modifier = modifier.fillMaxSize()) {
                TwoPaneScaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(LANDING_SCREEN_TAG),
                    topBar = { TopBanner() },
                    firstPane = {
                        LandingScreenHeader(
                            modifier = Modifier
                                .padding(VonageVideoTheme.dimens.paddingLarge)
                                .widthIn(0.dp, MAX_PANE_WIDTH.dp),
                        )
                    },
                    secondPane = {
                        LandingScreenContent(
                            modifier = Modifier
                                .background(VonageVideoTheme.colors.surface, VonageVideoTheme.shapes.small)
                                .padding(VonageVideoTheme.dimens.paddingLarge)
                                .widthIn(0.dp, MAX_PANE_WIDTH.dp),
                            roomName = uiState.roomName,
                            isRoomNameWrong = uiState.isRoomNameWrong,
                            actions = actions,
                        )
                    }
                )

                IconButton(
                    onClick = { uriHandler.openUri(GITHUB_REPO_URL) },
                    modifier = Modifier
                        .testTag(GITHUB_REPO_BUTTON_TAG)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_github),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = VonageVideoTheme.colors.onBackground,
                    )
                }
            }
        }

        is LandingScreenUiState.Success -> {
            LaunchedEffect(uiState) {
                navigateToRoom(LandingScreenRouteParams(roomName = uiState.roomName))
            }
        }
    }
}

private const val MAX_PANE_WIDTH = 550
private const val GITHUB_REPO_URL = "https://github.com/Vonage/vonage-video-android-app"

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun LandingScreenPreview() {
    VonageVideoTheme {
        LandingScreen(
            uiState = LandingScreenUiState.Content(
                roomName = "hithere",
                isRoomNameWrong = false,
            ),
            actions = LandingScreenActions(),
        )
    }
}
