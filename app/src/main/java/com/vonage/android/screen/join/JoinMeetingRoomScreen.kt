package com.vonage.android.screen.join

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.R
import com.vonage.android.compose.icons.KeyboardIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.OrSeparator
import com.vonage.android.screen.components.VonageIcon
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.CREATE_ROOM_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.PROGRESS_INDICATOR_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.ROOM_INPUT_ERROR_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.ROOM_INPUT_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.SUBTITLE_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.TITLE_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.VONAGE_ICON_TAG

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        JoinMeetingRoomHeader()

        when (uiState) {
            is JoinMeetingRoomUiState.Content -> {
                LaunchedEffect(uiState.isError) {
                    if (uiState.isError) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                JoinMeetingRoomContent(
                    roomName = uiState.roomName,
                    isRoomNameWrong = uiState.isRoomNameWrong,
                    actions = actions,
                )
            }

            is JoinMeetingRoomUiState.Success -> {
                LaunchedEffect(uiState) {
                    navigateToRoom(
                        JoinMeetingRoomRouteParams(
                            apiKey = uiState.apiKey,
                            sessionId = uiState.sessionId,
                            token = uiState.token,
                        )
                    )
                }
            }

            is JoinMeetingRoomUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(64.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.testTag(PROGRESS_INDICATOR_TAG)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Stable
@Composable
fun JoinMeetingRoomHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        VonageIcon(
            modifier = Modifier.testTag(VONAGE_ICON_TAG),
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            modifier = Modifier.testTag(TITLE_TAG),
            text = stringResource(R.string.landing_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 46.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
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
    ) {
        Text(
            modifier = Modifier.testTag(SUBTITLE_TAG),
            text = stringResource(R.string.landing_subtitle),
            fontSize = 16.sp,
            color = VonageVideoTheme.colors.textPrimaryDisabled,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = actions.onCreateRoomClick,
            shape = RoundedCornerShape(corner = CornerSize(6.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag(CREATE_ROOM_BUTTON_TAG),
            colors = ButtonDefaults.buttonColors(
                containerColor = VonageVideoTheme.colors.buttonPrimary,
            )
        ) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.landing_create_room),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OrSeparator()

        Spacer(modifier = Modifier.height(32.dp))

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
    ) {
        OutlinedTextField(
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
            leadingIcon = {
                KeyboardIcon()
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VonageVideoTheme.colors.buttonPrimary,
                unfocusedBorderColor = VonageVideoTheme.colors.buttonPrimaryDisabled,
            ),
            singleLine = true,
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

        Spacer(modifier = Modifier.width(8.dp))

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
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@PreviewScreenSizes
@Preview
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
