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
import androidx.compose.material3.Scaffold
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
import com.vonage.android.screen.components.CircularControlButton
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.components.permissions.CallPermissionHandler
import com.vonage.android.screen.waiting.WaitingRoomTestTags.CAMERA_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.MIC_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.PREPARE_TO_JOIN_TEXT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.ROOM_NAME_TEXT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.USER_INITIALS_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.USER_NAME_INPUT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.WHATS_YOU_NAME_TEXT_TAG
import com.vonage.android.util.buildTestTag

@Composable
fun WaitingRoomScreen(
    uiState: WaitingRoomUiState,
    actions: WaitingRoomActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (String) -> Unit = {},
    onGrantPermissions: () -> Unit = {},
    navigateToPermissions: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBanner(
                onBack = actions.onBack,
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(VonageVideoTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

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
                        roomName = uiState.roomName,
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
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag(USER_INITIALS_TAG),
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
            modifier = Modifier
                .conditional(
                    isMicEnabled,
                    ifTrue = { background(Color.Unspecified) },
                    ifFalse = { background(Color.Red.copy(alpha = 0.7f)) }
                )
                .testTag(MIC_BUTTON_TAG.buildTestTag(isMicEnabled)),
            onClick = onMicToggle,
            icon = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
        )

        CircularControlButton(
            modifier = Modifier
                .conditional(
                    isCameraEnabled,
                    ifTrue = { background(Color.Unspecified) },
                    ifFalse = { background(Color.Red.copy(alpha = 0.7f)) }
                )
                .testTag(CAMERA_BUTTON_TAG.buildTestTag(isCameraEnabled)),
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
    onJoinRoom: (String) -> Unit,
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
            modifier = Modifier.testTag(PREPARE_TO_JOIN_TEXT_TAG),
            text = stringResource(R.string.waiting_room_prepare_to_join),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.inverseSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier.testTag(ROOM_NAME_TEXT_TAG),
            text = roomName,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier.testTag(WHATS_YOU_NAME_TEXT_TAG),
            text = stringResource(R.string.waiting_room_whats_your_name),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.inverseSurface,
            textAlign = TextAlign.Center,
        )

        VonageTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(USER_NAME_INPUT_TAG),
            value = username,
            onValueChange = onUsernameChange,
            leadingIcon = {
                PersonIcon()
            },
        )

        VonageButton(
            modifier = Modifier.testTag(JOIN_BUTTON_TAG),
            text = stringResource(R.string.waiting_room_join),
            onClick = { onJoinRoom(username) },
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
                roomName = "test-room-name",
                userName = "User Name",
                isMicEnabled = true,
                isCameraEnabled = false,
                view = previewCamera(),
            ),
            actions = WaitingRoomActions(),
        )
    }
}

@PreviewLightDark
@Composable
internal fun WaitingRoomScreenWithVideoPreview() {
    VonageVideoTheme {
        WaitingRoomScreen(
            uiState = WaitingRoomUiState.Content(
                roomName = "test-room-name",
                userName = "John Doe",
                isMicEnabled = false,
                isCameraEnabled = true,
                view = previewCamera(),
            ),
            actions = WaitingRoomActions(),
        )
    }
}

@Composable
fun previewCamera(): View = ImageView(LocalContext.current)
    .apply {
        setImageResource(R.drawable.person)
        scaleType = ImageView.ScaleType.CENTER_CROP
    }
