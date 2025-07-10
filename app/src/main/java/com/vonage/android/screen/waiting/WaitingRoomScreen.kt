package com.vonage.android.screen.waiting

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurOff
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.R
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.CircularControlButton
import com.vonage.android.screen.components.DeviceSelectionPanel
import com.vonage.android.screen.components.TopBanner

@Composable
fun WaitingRoomScreen(
    roomName: String = "graceful-mouse",
    username: String = "",
    onUsernameChange: (String) -> Unit = {},
    onJoinRoom: () -> Unit = {},
    onMicToggle: () -> Unit = {},
    onCameraToggle: () -> Unit = {},
    onVideoEffectsToggle: () -> Unit = {},
    onMicDeviceSelect: () -> Unit = {},
    onCameraDeviceSelect: () -> Unit = {},
    onSpeakerDeviceSelect: () -> Unit = {},
    isMicEnabled: Boolean = true,
    isCameraEnabled: Boolean = false,
    isVideoEffectsEnabled: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBanner()

        VideoPreviewContainer(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .height(300.dp),
            onMicToggle = onMicToggle,
            onCameraToggle = onCameraToggle,
            onVideoEffectsToggle = onVideoEffectsToggle,
            isMicEnabled = isMicEnabled,
            isCameraEnabled = isCameraEnabled,
            isVideoEffectsEnabled = isVideoEffectsEnabled,
        )

        Spacer(modifier = Modifier.height(24.dp))

        DeviceSelectionPanel(
            onMicDeviceSelect = onMicDeviceSelect,
            onCameraDeviceSelect = onCameraDeviceSelect,
            onSpeakerDeviceSelect = onSpeakerDeviceSelect,
        )

        Spacer(modifier = Modifier.height(24.dp))

        JoinRoomSection(
            roomName = roomName,
            username = username,
            onUsernameChange = onUsernameChange,
            onJoinRoom = onJoinRoom,
        )
    }
}

@Composable
fun VideoPreviewContainer(
    modifier: Modifier = Modifier,
    onMicToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onVideoEffectsToggle: () -> Unit,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    isVideoEffectsEnabled: Boolean,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Video Preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        VideoControlPanel(
            modifier = Modifier.padding(bottom = 16.dp),
            onMicToggle = onMicToggle,
            onCameraToggle = onCameraToggle,
            onVideoEffectsToggle = onVideoEffectsToggle,
            isMicEnabled = isMicEnabled,
            isCameraEnabled = isCameraEnabled,
            isVideoEffectsEnabled = isVideoEffectsEnabled,
        )
    }
}

@Composable
fun VideoControlPanel(
    modifier: Modifier = Modifier,
    onMicToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onVideoEffectsToggle: () -> Unit,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    isVideoEffectsEnabled: Boolean,
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

        CircularControlButton(
            onClick = onVideoEffectsToggle,
            icon = if (isVideoEffectsEnabled) Icons.Default.BlurOn else Icons.Default.BlurOff,
        )
    }
}

@Composable
fun JoinRoomSection(
    roomName: String,
    username: String,
    onUsernameChange: (String) -> Unit,
    onJoinRoom: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
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
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                )
            },
        )

        Button(
            onClick = onJoinRoom,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            ),
            enabled = username.isNotEmpty()
        ) {
            Text(
                text = stringResource(R.string.waiting_room_join),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun WaitingRoomScreenPreview() {
    VonageVideoTheme {
        WaitingRoomScreen(
            username = "Vonage User",
            isMicEnabled = true,
            isCameraEnabled = false,
            isVideoEffectsEnabled = true,
        )
    }
}
