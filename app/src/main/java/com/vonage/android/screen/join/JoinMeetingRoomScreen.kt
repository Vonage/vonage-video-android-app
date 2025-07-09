package com.vonage.android.screen.join

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
import androidx.compose.material.icons.filled.Keyboard
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.R

@Stable
@Composable
fun JoinMeetingRoomScreen(
    mainUiState: MainUiState,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (String, String, String) -> Unit = { _, _, _ -> },
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        JoinMeetingRoomHeader()

        when (mainUiState) {
            is MainUiState.Content -> {
                JoinMeetingRoomContent(
                    roomName = mainUiState.roomName,
                    isRoomNameWrong = mainUiState.isRoomNameWrong,
                    actions = actions,
                )
            }

            is MainUiState.Success -> {
                LaunchedEffect(mainUiState) {
                    navigateToRoom(
                        mainUiState.apiKey,
                        mainUiState.sessionId,
                        mainUiState.token
                    )
                }
            }

            is MainUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(64.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }

            is MainUiState.Error -> {
                Column(
                    modifier = Modifier
                        .padding(64.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("ERROR")
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

        Icon(
            painter = painterResource(R.drawable.ic_vonage),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.landing_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
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
            modifier = Modifier.testTag("join_meeting_room_screen_subtitle"),
            text = stringResource(R.string.landing_subtitle),
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = actions.onCreateRoomClick,
            shape = RoundedCornerShape(corner = CornerSize(6.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("join_meeting_room_screen_create_room_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB) // Blue color
            )
        ) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
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

        Text(
            text = stringResource(R.string.landing_or),
            color = Color.Gray,
            fontSize = 14.sp
        )

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
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        OutlinedTextField(
            value = roomName,
            onValueChange = actions.onRoomNameChange,
            modifier = Modifier.weight(1f),
            isError = isRoomNameWrong,
            placeholder = {
                Text(
                    text = stringResource(R.string.landing_enter_room_name),
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            supportingText = {
                if (isRoomNameWrong) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = stringResource(R.string.landing_room_name_error_message),
                        color = Color.Red
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        TextButton(
            modifier = Modifier.padding(vertical = 6.dp),
            onClick = { actions.onJoinRoomClick(roomName) },
            enabled = isRoomNameWrong.not(),
        ) {
            Text(
                text = stringResource(R.string.landing_join),
                color = if (isRoomNameWrong.not()) Color(0xFF2563EB) else Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
