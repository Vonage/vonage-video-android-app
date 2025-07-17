package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun BottomBar(
    onToggleMic: () -> Unit,
    onToggleCamera: () -> Unit,
    onToggleParticipants: () -> Unit,
    onEndCall: () -> Unit,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    participantsCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(
                onClick = onToggleMic,
                icon = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                isActive = isMicEnabled,
                contentDescription = if (isMicEnabled) "Mute" else "Unmute"
            )

            ControlButton(
                onClick = onToggleCamera,
                icon = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                isActive = isCameraEnabled,
                contentDescription = if (isCameraEnabled) "Turn off camera" else "Turn on camera"
            )

            BadgedBox(
                badge = {
                    Badge(
                        containerColor = Color.Gray,
                        contentColor = Color.White,
                    ) {
                        Text("$participantsCount")
                    }
                }
            ) {
                ControlButton(
                    onClick = onToggleParticipants,
                    icon = Icons.Default.Group,
                    isActive = true,
                    contentDescription = "Participants"
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Red, CircleShape)
                    .clickable { onEndCall() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CallEnd,
                    contentDescription = "End call",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ControlButton(
    onClick: () -> Unit,
    icon: ImageVector,
    isActive: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isActive) Color.White else Color.Gray

    Box(
        modifier = modifier
            .size(48.dp)
            .conditional(
                isActive,
                ifTrue = { background(Color.White.copy(alpha = 0.2f), CircleShape) },
                ifFalse = { background(Color.Transparent, CircleShape) },
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(24.dp),
        )
    }
}

@PreviewLightDark
@Composable
fun BottomBarPreview() {
    VonageVideoTheme {
        BottomBar(
            onToggleMic = {},
            onToggleCamera = {},
            onToggleParticipants = {},
            onEndCall = {},
            isMicEnabled = false,
            isCameraEnabled = true,
            participantsCount = 25,
        )
    }
}
