package com.vonage.android.screen.landing

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.icons.CameraSwitchIcon
import com.vonage.android.compose.layout.TwoPaneScaffold
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.CameraSwitch
import com.vonage.android.compose.vivid.icons.solid.Gear
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.landing.components.LandingScreenContent
import com.vonage.android.screen.landing.components.LandingScreenHeader
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_CAMERA_SWITCH_ACTION

@Composable
fun LandingScreen(
    uiState: LandingScreenUiState,
    actions: LandingScreenActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (LandingScreenRouteParams) -> Unit = {},
    navigateToSettings: () -> Unit = {},
) {
    val context = LocalContext.current
    val errorMessage = stringResource(R.string.landing_room_generic_error_message)

    when (uiState) {
        is LandingScreenUiState.Content -> {
            LaunchedEffect(uiState.isError) {
                if (uiState.isError) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            TwoPaneScaffold(
                modifier = modifier.fillMaxSize(),
                topBar = { TopBanner {
                    IconButton(
                        modifier = Modifier,
                        onClick = navigateToSettings,
                    ) {
                        Icon(
                            imageVector = VividIcons.Solid.Gear,
                            contentDescription = null,
                            tint = VonageVideoTheme.colors.onSurface,
                            modifier = modifier.size(VonageVideoTheme.dimens.iconSizeDefault)
                        )
                    }
                } },
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
        }

        is LandingScreenUiState.Success -> {
            LaunchedEffect(uiState) {
                navigateToRoom(LandingScreenRouteParams(roomName = uiState.roomName))
            }
        }
    }
}

private const val MAX_PANE_WIDTH = 550

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
