package com.vonage.android.screen.waiting

import android.view.View
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.R
import com.vonage.android.compose.components.VideoRenderer
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.icons.PersonIcon
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.AvatarInitials
import com.vonage.android.screen.components.CallPermissionHandler
import com.vonage.android.screen.components.CircularControlButton
import com.vonage.android.screen.components.TopBanner

@Composable
fun WaitingRoomScreen(
    uiState: WaitingRoomUiState,
    actions: WaitingRoomActions,
    roomName: String,
    modifier: Modifier = Modifier,
    navigateToRoom: (String) -> Unit = {},
    onGrantPermissions: () -> Unit = {},
    navigateToPermissions: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBanner()

        CallPermissionHandler(
            onGrantPermissions = onGrantPermissions,
            navigateToPermissions = navigateToPermissions,
        )

        when (uiState) {
            is WaitingRoomUiState.Content -> {
                VideoPreviewContainer(
                    view = uiState.view,
                    name = uiState.userName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    onMicToggle = actions.onMicToggle,
                    onCameraToggle = actions.onCameraToggle,
                    isMicEnabled = uiState.isMicEnabled,
                    isCameraEnabled = uiState.isCameraEnabled,
                )

                Spacer(modifier = Modifier.height(24.dp))

                JoinRoomSection(
                    roomName = roomName,
                    username = uiState.userName,
                    onUsernameChange = actions.onUserNameChange,
                    onJoinRoom = actions.onJoinRoom,
                )
            }

            is WaitingRoomUiState.Idle -> {
                CircularProgressIndicator(
                    modifier = Modifier.testTag("initializing_indicator")
                )
            }

            is WaitingRoomUiState.Success -> {
                navigateToRoom(uiState.roomName)
            }
        }
    }
}

@Composable
fun VideoPreviewContainer(
    view: View?,
    name: String,
    onMicToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(Color.DarkGray),
        contentAlignment = Alignment.BottomCenter,
    ) {
        if (isCameraEnabled && view != null) {
            VideoRenderer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clipToBounds(),
                view = view,
            )
        } else {
            AvatarInitials(
                modifier = Modifier.align(Alignment.Center),
                userName = name,
            )
        }
        VideoControlPanel(
            modifier = Modifier.padding(bottom = 16.dp),
            onMicToggle = onMicToggle,
            onCameraToggle = onCameraToggle,
            isMicEnabled = isMicEnabled,
            isCameraEnabled = isCameraEnabled,
        )
    }
}

@Composable
fun VideoControlPanel(
    onMicToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularControlButton(
            modifier = Modifier.conditional(
                isMicEnabled,
                ifTrue = { background(Color.Unspecified) },
                ifFalse = { background(Color.Red.copy(alpha = 0.7f)) }
            ),
            onClick = onMicToggle,
            icon = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
        )

        CircularControlButton(
            modifier = Modifier.conditional(
                isCameraEnabled,
                ifTrue = { background(Color.Unspecified) },
                ifFalse = { background(Color.Red.copy(alpha = 0.7f)) }
            ),
            onClick = onCameraToggle,
            icon = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
        )
    }
}

@Composable
fun JoinRoomSection(
    roomName: String,
    username: String,
    onUsernameChange: (String) -> Unit,
    onJoinRoom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.waiting_room_prepare_to_join),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.inverseSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = roomName,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.waiting_room_whats_your_name),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.inverseSurface,
            textAlign = TextAlign.Center,
        )

        VonageTextField(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                PersonIcon()
            },
        )

        VonageButton(
            text = stringResource(R.string.waiting_room_join),
            onClick = onJoinRoom,
            enabled = username.isNotEmpty(),
        )
    }
}

@PreviewLightDark
@Composable
internal fun WaitingRoomScreenPreview() {
    VonageVideoTheme {
        WaitingRoomScreen(
            uiState = WaitingRoomUiState.Content(
                userName = "User Name",
                isMicEnabled = true,
                isCameraEnabled = false,
                view = previewCamera(),
            ),
            actions = WaitingRoomActions(),
            roomName = "test-room-name",
        )
    }
}

@PreviewLightDark
@Composable
internal fun WaitingRoomScreenWithVideoPreview() {
    VonageVideoTheme {
        WaitingRoomScreen(
            uiState = WaitingRoomUiState.Content(
                userName = "John Doe",
                isMicEnabled = false,
                isCameraEnabled = true,
                view = previewCamera(),
            ),
            actions = WaitingRoomActions(),
            roomName = "test-room-name",
        )
    }
}

@Composable
private fun previewCamera(): View = ImageView(LocalContext.current)
    .apply {
        setImageResource(R.drawable.person)
        scaleType = ImageView.ScaleType.CENTER_CROP
    }
