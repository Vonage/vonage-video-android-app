package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.ControlButton

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
            )

            ControlButton(
                onClick = onToggleCamera,
                icon = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                isActive = isCameraEnabled,
            )

            ControlButton(
                onClick = {},
                icon = Icons.Default.Window,
                isActive = false,
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
                )
            }

            ControlButton(
                modifier = Modifier
                    .background(Color.Red, CircleShape),
                onClick = onEndCall,
                icon = Icons.Default.CallEnd,
                isActive = true,
            )
        }
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
