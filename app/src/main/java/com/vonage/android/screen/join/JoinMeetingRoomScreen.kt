package com.vonage.android.screen.join

import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.BuildConfig
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.icons.KeyboardIcon
import com.vonage.android.compose.icons.VideoCameraIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.OrSeparator
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.CREATE_ROOM_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.ROOM_INPUT_ERROR_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.ROOM_INPUT_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.SUBTITLE_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.TITLE_TAG

@Stable
@Composable
fun JoinMeetingRoomScreen(
    uiState: JoinMeetingRoomUiState,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (JoinMeetingRoomRouteParams) -> Unit = {},
) {
    val context = LocalContext.current
    val errorMessage = stringResource(R.string.landing_room_generic_error_message)

    Scaffold(
        modifier = modifier,
        topBar = { TopBanner() },
    ) { contentPadding ->
        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxSize()
                .background(VonageVideoTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .consumeWindowInsets(contentPadding)
                .padding(horizontal = 24.dp),
        ) {

            JoinMeetingRoomHeader(
                modifier = Modifier
                    .padding(top = 80.dp)
                    .widthIn(0.dp, 380.dp)
            )

            when (uiState) {
                is JoinMeetingRoomUiState.Content -> {
                    LaunchedEffect(uiState.isError) {
                        if (uiState.isError) {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                    LaunchedEffect(uiState.isSuccess) {
                        if (uiState.isSuccess) {
                            navigateToRoom(
                                JoinMeetingRoomRouteParams(roomName = uiState.roomName)
                            )
                        }
                    }
                    JoinMeetingRoomContent(
                        modifier = Modifier
                            .padding(top = 48.dp)
                            .widthIn(0.dp, 320.dp),
                        roomName = uiState.roomName,
                        isRoomNameWrong = uiState.isRoomNameWrong,
                        actions = actions,
                    )
                }
            }
        }
    }
}

@Stable
@Composable
fun JoinMeetingRoomHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            modifier = Modifier.testTag(TITLE_TAG),
            text = stringResource(R.string.landing_title),
            style = VonageVideoTheme.typography.titleLarge,
            color = VonageVideoTheme.colors.inverseSurface,
            textAlign = TextAlign.Start,
        )

        Text(
            modifier = Modifier.testTag(SUBTITLE_TAG),
            text = stringResource(R.string.landing_subtitle),
            style = VonageVideoTheme.typography.body,
            color = VonageVideoTheme.colors.textPrimaryDisabled,
            textAlign = TextAlign.Start,
        )
    }
}

@Stable
@Composable
fun JoinMeetingRoomContent(
    roomName: String,
    isRoomNameWrong: Boolean,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        VonageButton(
            text = stringResource(R.string.landing_create_room),
            modifier = Modifier
                .testTag(CREATE_ROOM_BUTTON_TAG),
            onClick = actions.onCreateRoomClick,
            leadingIcon = { VideoCameraIcon() },
        )
        OrSeparator()
        RoomInput(
            roomName = roomName,
            isRoomNameWrong = isRoomNameWrong,
            actions = actions,
        )
    }
}

@Stable
@Composable
fun RoomInput(
    roomName: String,
    isRoomNameWrong: Boolean,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        VonageTextField(
            modifier = Modifier
                .weight(1f)
                .testTag(ROOM_INPUT_TAG),
            value = roomName,
            onValueChange = actions.onRoomNameChange,
            isError = isRoomNameWrong,
            placeholder = {
                Text(
                    text = stringResource(R.string.landing_enter_room_name),
                    color = VonageVideoTheme.colors.textPrimaryDisabled,
                )
            },
            leadingIcon = { KeyboardIcon() },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            supportingText = {
                if (isRoomNameWrong) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .testTag(ROOM_INPUT_ERROR_TAG),
                        text = stringResource(R.string.landing_room_name_error_message),
                        color = VonageVideoTheme.colors.textError,
                    )
                }
            }
        )

        TextButton(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .testTag(JOIN_BUTTON_TAG),
            onClick = { actions.onJoinRoomClick(roomName) },
            enabled = isRoomNameWrong.not() && roomName.isNotEmpty(),
        ) {
            Text(
                text = stringResource(R.string.landing_join),
                color = if (isRoomNameWrong.not()) {
                    VonageVideoTheme.colors.textPrimary
                } else VonageVideoTheme.colors.textPrimaryDisabled,
                style = VonageVideoTheme.typography.body,
            )
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun PreviewJoinRoomScreen() {
    VonageVideoTheme {
        JoinMeetingRoomScreen(
            uiState = JoinMeetingRoomUiState.Content(
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
