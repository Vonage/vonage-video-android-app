package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.ControlButton
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_CAMERA_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_END_CALL_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_MIC_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BADGE
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BUTTON

@Suppress("LongParameterList")
@Composable
fun BottomBar(
    actions: MeetingRoomActions,
    onToggleParticipants: () -> Unit,
    onShowChat: () -> Unit,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    isChatShow: Boolean,
    participantsCount: Int,
    unreadCount: Int,
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
                modifier = Modifier
                    .testTag(BOTTOM_BAR_MIC_BUTTON),
                onClick = actions.onToggleMic,
                icon = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                isActive = isMicEnabled,
            )

            ControlButton(
                modifier = Modifier
                    .testTag(BOTTOM_BAR_CAMERA_BUTTON),
                onClick = actions.onToggleCamera,
                icon = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                isActive = isCameraEnabled,
            )

            ParticipantsBadgeButton(
                participantsCount = participantsCount,
                onToggleParticipants = onToggleParticipants,
            )

            ChatBadgeButton(
                unreadCount = unreadCount,
                onShowChat = onShowChat,
                isChatShow = isChatShow,
            )

            ControlButton(
                modifier = Modifier
                    .background(Color.Red, CircleShape)
                    .testTag(BOTTOM_BAR_END_CALL_BUTTON),
                onClick = actions.onEndCall,
                icon = Icons.Default.CallEnd,
                isActive = true,
            )
        }
    }
}

@Composable
private fun ChatBadgeButton(
    unreadCount: Int,
    onShowChat: () -> Unit,
    isChatShow: Boolean,
) {
    val badgeVisible = unreadCount > 0
    BadgedBox(
        badge = {
            if (badgeVisible) {
                Badge(
                    containerColor = VonageVideoTheme.colors.primary,
                    contentColor = Color.White,
                ) {
                    Text(
                        modifier = Modifier,
                        text = "$unreadCount",
                    )
                }
            }
        },
    ) {
        ControlButton(
            onClick = onShowChat,
            icon = Icons.AutoMirrored.Default.Chat,
            isActive = isChatShow,
        )
    }
}

@Composable
private fun ParticipantsBadgeButton(
    participantsCount: Int,
    onToggleParticipants: () -> Unit,
) {
    BadgedBox(
        badge = {
            Badge(
                containerColor = Color.Gray,
                contentColor = Color.White,
            ) {
                Text(
                    modifier = Modifier
                        .testTag(BOTTOM_BAR_PARTICIPANTS_BADGE),
                    text = "$participantsCount",
                )
            }
        }
    ) {
        ControlButton(
            modifier = Modifier
                .testTag(BOTTOM_BAR_PARTICIPANTS_BUTTON),
            onClick = onToggleParticipants,
            icon = Icons.Default.Group,
            isActive = false,
        )
    }
}

object BottomBarTestTags {
    const val BOTTOM_BAR_PARTICIPANTS_BUTTON = "bottom_bar_participants_button"
    const val BOTTOM_BAR_PARTICIPANTS_BADGE = "bottom_bar_participants_badge"
    const val BOTTOM_BAR_END_CALL_BUTTON = "bottom_bar_end_call_button"
    const val BOTTOM_BAR_GRID_BUTTON = "bottom_bar_grid_button"
    const val BOTTOM_BAR_CAMERA_BUTTON = "bottom_bar_camera_button"
    const val BOTTOM_BAR_MIC_BUTTON = "bottom_bar_mic_button"
}

@PreviewLightDark
@Composable
internal fun BottomBarPreview() {
    VonageVideoTheme {
        BottomBar(
            actions = MeetingRoomActions(),
            onToggleParticipants = {},
            onShowChat = {},
            isMicEnabled = false,
            isCameraEnabled = true,
            isChatShow = false,
            participantsCount = 25,
            unreadCount = 10,
        )
    }
}
