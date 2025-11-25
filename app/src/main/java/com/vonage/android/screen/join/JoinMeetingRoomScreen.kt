package com.vonage.android.screen.join

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.join.components.JoinMeetingRoomContent
import com.vonage.android.screen.join.components.JoinMeetingRoomHeader

@Composable
fun JoinMeetingRoomScreen(
    uiState: JoinMeetingRoomUiState,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (JoinMeetingRoomRouteParams) -> Unit = {},
) {
    val context = LocalContext.current
    val errorMessage = stringResource(R.string.landing_room_generic_error_message)

    LaunchedEffect(uiState.isError, uiState.isSuccess) {
        if (uiState.isError) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
        if (uiState.isSuccess) {
            navigateToRoom(JoinMeetingRoomRouteParams(roomName = uiState.roomName))
        }
    }

    TwoPaneScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopBanner() },
        firstPane = {
            JoinMeetingRoomHeader(
                modifier = Modifier
                    .padding(VonageVideoTheme.dimens.paddingLarge)
                    .widthIn(0.dp, 550.dp),
            )
        },
        secondPane = {
            JoinMeetingRoomContent(
                modifier = Modifier
                    .background(VonageVideoTheme.colors.surface, VonageVideoTheme.shapes.small)
                    .padding(VonageVideoTheme.dimens.paddingLarge)
                    .widthIn(0.dp, 550.dp),
                roomName = uiState.roomName,
                isRoomNameWrong = uiState.isRoomNameWrong,
                actions = actions,
            )
        }
    )
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun PreviewJoinRoomScreen() {
    VonageVideoTheme {
        JoinMeetingRoomScreen(
            uiState = JoinMeetingRoomUiState(
                roomName = "hithere",
                isRoomNameWrong = false,
            ),
            actions = JoinMeetingRoomActions(
                onJoinRoomClick = {},
                onCreateRoomClick = {},
                onRoomNameChange = {},
            ),
        )
    }
}
