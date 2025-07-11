package com.vonage.android.screen.waiting

import android.content.Context
import android.view.View
import android.widget.ImageView
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
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
import com.vonage.android.kotlin.Participant
import com.vonage.android.screen.components.AvatarInitials
import com.vonage.android.screen.components.CircularControlButton
import com.vonage.android.screen.components.TopBanner

@Composable
fun WaitingRoomScreen(
    participant: Participant,
    roomName: String,
    modifier: Modifier = Modifier,
    onUsernameChange: (String) -> Unit = {},
    onJoinRoom: () -> Unit = {},
    onMicToggle: () -> Unit = {},
    onCameraToggle: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBanner()

        VideoPreviewContainer(
            participant = participant,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            onMicToggle = onMicToggle,
            onCameraToggle = onCameraToggle,
            isMicEnabled = participant.isMicEnabled,
            isCameraEnabled = participant.isCameraEnabled,
        )

        Spacer(modifier = Modifier.height(24.dp))

//        DeviceSelectionPanel(
//            onMicDeviceSelect = onMicDeviceSelect,
//            onCameraDeviceSelect = onCameraDeviceSelect,
//            onSpeakerDeviceSelect = onSpeakerDeviceSelect,
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))

        JoinRoomSection(
            roomName = roomName,
            username = participant.getName(),
            onUsernameChange = onUsernameChange,
            onJoinRoom = onJoinRoom,
        )
    }
}

@Composable
fun VideoPreviewContainer(
    participant: Participant,
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
        if (isCameraEnabled) {
            if (LocalInspectionMode.current) {
                Image(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "Video Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                VideoRenderer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clipToBounds(),
                    renderer = participant.getView(),
                )
            }
        } else {
            AvatarInitials(
                modifier = Modifier.align(Alignment.Center),
                userName = participant.getName(),
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
            roomName = "test-room-name",
            participant = PreviewPublisher(
                context = LocalContext.current,
                isVideoEnabled = false,
            )
        )
    }
}

@PreviewLightDark
@Composable
internal fun WaitingRoomScreenWithVideoPreview() {
    VonageVideoTheme {
        WaitingRoomScreen(
            roomName = "test-room-name",
            participant = PreviewPublisher(LocalContext.current)
        )
    }
}

private class PreviewPublisher(
    val context: Context,
    val userName: String = "Vera User",
    val isAudioEnabled: Boolean = true,
    val isVideoEnabled: Boolean = true,
) : Participant {

    override val isMicEnabled: Boolean = isAudioEnabled
    override val isCameraEnabled: Boolean = isVideoEnabled

    override fun getView(): View =
        ImageView(context)
            .apply {
                setImageResource(R.drawable.person)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

    override fun getName(): String = userName
}
