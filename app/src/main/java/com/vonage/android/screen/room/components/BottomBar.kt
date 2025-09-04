package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_CHAT_BADGE
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_CHAT_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_END_CALL_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_MIC_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BADGE
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BUTTON

@Stable
data class BottomBarState(
    val onToggleParticipants: () -> Unit,
    val onShowChat: () -> Unit,
    val onToggleMoreActions: () -> Unit,
    val isMicEnabled: Boolean,
    val isCameraEnabled: Boolean,
    val isChatShow: Boolean,
    val participantsCount: Int,
    val unreadCount: Int,
)

@Suppress("LongParameterList")
@Composable
fun BottomBar(
    actions: MeetingRoomActions,
    bottomBarState: BottomBarState,
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
                icon = if (bottomBarState.isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                isActive = bottomBarState.isMicEnabled,
            )

            ControlButton(
                modifier = Modifier
                    .testTag(BOTTOM_BAR_CAMERA_BUTTON),
                onClick = actions.onToggleCamera,
                icon = if (bottomBarState.isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                isActive = bottomBarState.isCameraEnabled,
            )

            ParticipantsBadgeButton(
                participantsCount = bottomBarState.participantsCount,
                onToggleParticipants = bottomBarState.onToggleParticipants,
            )

            ChatBadgeButton(
                unreadCount = bottomBarState.unreadCount,
                onShowChat = bottomBarState.onShowChat,
                isChatShow = bottomBarState.isChatShow,
            )

            VerticalDivider(
                modifier = Modifier
                    .size(height = 36.dp, width = 1.dp),
                thickness = 1.dp,
            )

            ControlButton(
                modifier = Modifier,
                onClick = bottomBarState.onToggleMoreActions,
                icon = Icons.Default.MoreVert,
                isActive = false,
            )

            VerticalDivider(
                modifier = Modifier
                    .size(height = 36.dp, width = 1.dp),
                thickness = 1.dp,
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
fun ChatBadgeButton(
    unreadCount: Int,
    onShowChat: () -> Unit,
    isChatShow: Boolean,
    modifier: Modifier = Modifier,
) {
    val badgeVisible = unreadCount > 0
    BadgedBox(
        modifier = modifier,
        badge = {
            if (badgeVisible) {
                Badge(
                    containerColor = VonageVideoTheme.colors.primary,
                    contentColor = Color.White,
                ) {
                    Text(
                        modifier = Modifier
                            .testTag(BOTTOM_BAR_CHAT_BADGE),
                        text = "$unreadCount",
                    )
                }
            }
        },
    ) {
        ControlButton(
            modifier = Modifier
                .testTag(BOTTOM_BAR_CHAT_BUTTON),
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
    const val BOTTOM_BAR_CHAT_BUTTON = "bottom_bar_chat_button"
    const val BOTTOM_BAR_CHAT_BADGE = "bottom_bar_chat_badge"
    const val BOTTOM_BAR_CAMERA_BUTTON = "bottom_bar_camera_button"
    const val BOTTOM_BAR_MIC_BUTTON = "bottom_bar_mic_button"
}

@PreviewLightDark
@Composable
internal fun BottomBarPreview() {
    VonageVideoTheme {
        BottomBar(
            actions = MeetingRoomActions(),
            bottomBarState = BottomBarState(
                onToggleParticipants = {},
                onShowChat = {},
                onToggleMoreActions = {},
                isMicEnabled = false,
                isCameraEnabled = true,
                isChatShow = false,
                participantsCount = 25,
                unreadCount = 10,
            ),
        )
    }
}
