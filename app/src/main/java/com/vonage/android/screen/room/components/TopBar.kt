package com.vonage.android.screen.room.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.room.MeetingRoomActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    roomName: String,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        title = {
            Text(
                text = roomName,
                color = MaterialTheme.colorScheme.inverseSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        },
        actions = {
            IconButton(
                onClick = {
                    actions.onShare(roomName)
                }
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share room link",
                    tint = MaterialTheme.colorScheme.inverseSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

@PreviewLightDark
@Composable
internal fun TopBarPreview() {
    VonageVideoTheme {
        TopBar(
            roomName = "sample-name",
            actions = MeetingRoomActions(
                onShare = { },
                onToggleMic = { },
                onToggleCamera = { },
                onToggleParticipants = { },
                onEndCall = { },
            )
        )
    }
}
