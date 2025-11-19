package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.chat.ui.ChatBadgeButton
import com.vonage.android.compose.components.ControlButton
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.MoreVertical
import com.vonage.android.compose.vivid.icons.solid.EndCall
import com.vonage.android.compose.vivid.icons.solid.Group2
import com.vonage.android.compose.vivid.icons.solid.Layout2
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Microphone2
import com.vonage.android.compose.vivid.icons.solid.MoreVertical
import com.vonage.android.compose.vivid.icons.solid.VideoOff
import com.vonage.android.compose.vivid.icons.solid.Video
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Apps
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.CallLayoutType
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_ACTIVE_SPEAKER_LAYOUT_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_CAMERA_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_END_CALL_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_GRID_LAYOUT_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_MIC_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BADGE
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BUTTON
import com.vonage.android.screen.room.noOpCallFacade
import kotlinx.coroutines.flow.MutableStateFlow

@Stable
data class BottomBarState(
    val participant: Participant?,
    val onToggleParticipants: () -> Unit,
    val onShowChat: () -> Unit,
    val onToggleMoreActions: () -> Unit,
    val isChatShow: Boolean,
    val layoutType: CallLayoutType,
)

@Composable
fun BottomBar(
    actions: MeetingRoomActions,
    call: CallFacade,
    state: BottomBarState,
    modifier: Modifier = Modifier
) {
    val isMicEnabled by remember(state.participant) {
        state.participant?.let { state.participant.isMicEnabled } ?: MutableStateFlow(false)
    }.collectAsStateWithLifecycle()
    val isCameraEnabled by remember(state.participant) {
        state.participant?.let { state.participant.isCameraEnabled } ?: MutableStateFlow(false)
    }.collectAsStateWithLifecycle()
    val participantsCount by call.participantsCount.collectAsStateWithLifecycle()
    val chatState by call.chatSignalState().collectAsStateWithLifecycle()

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
                icon = if (isMicEnabled) VividIcons.Solid.Microphone2 else VividIcons.Solid.MicMute,
                isActive = isMicEnabled,
            )

            ControlButton(
                modifier = Modifier
                    .testTag(BOTTOM_BAR_CAMERA_BUTTON),
                onClick = actions.onToggleCamera,
                icon = if (isCameraEnabled) VividIcons.Solid.Video else VividIcons.Solid.VideoOff,
                isActive = isCameraEnabled,
            )

            ParticipantsBadgeButton(
                participantsCount = participantsCount,
                onToggleParticipants = state.onToggleParticipants,
            )

            ChatBadgeButton(
                unreadCount = chatState?.unreadCount ?: 0,
                onShowChat = state.onShowChat,
                isChatShow = state.isChatShow,
            )

            when (state.layoutType) {
                CallLayoutType.GRID -> {
                    ControlButton(
                        modifier = Modifier
                            .testTag(BOTTOM_BAR_GRID_LAYOUT_BUTTON),
                        onClick = { actions.onChangeLayout(CallLayoutType.SPEAKER_LAYOUT) },
                        icon = VividIcons.Solid.Apps,
                        isActive = false,
                    )
                }

                CallLayoutType.SPEAKER_LAYOUT -> {
                    ControlButton(
                        modifier = Modifier
                            .testTag(BOTTOM_BAR_ACTIVE_SPEAKER_LAYOUT_BUTTON),
                        onClick = { actions.onChangeLayout(CallLayoutType.GRID) },
                        icon = VividIcons.Solid.Layout2,
                        isActive = false,
                    )
                }

                else -> {}
            }

            ControlButton(
                modifier = Modifier,
                onClick = state.onToggleMoreActions,
                icon = VividIcons.Solid.MoreVertical,
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
                icon = VividIcons.Solid.EndCall,
                isActive = true,
            )
        }
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
            icon = VividIcons.Solid.Group2,
            isActive = false,
        )
    }
}

object BottomBarTestTags {
    const val BOTTOM_BAR_PARTICIPANTS_BUTTON = "bottom_bar_participants_button"
    const val BOTTOM_BAR_PARTICIPANTS_BADGE = "bottom_bar_participants_badge"
    const val BOTTOM_BAR_END_CALL_BUTTON = "bottom_bar_end_call_button"
    const val BOTTOM_BAR_CAMERA_BUTTON = "bottom_bar_camera_button"
    const val BOTTOM_BAR_MIC_BUTTON = "bottom_bar_mic_button"
    const val BOTTOM_BAR_GRID_LAYOUT_BUTTON = "bottom_bar_grid_layout_button"
    const val BOTTOM_BAR_ACTIVE_SPEAKER_LAYOUT_BUTTON = "bottom_bar_active_speaker_layout_button"
}

@PreviewLightDark
@Composable
internal fun BottomBarPreview() {
    VonageVideoTheme {
        BottomBar(
            actions = MeetingRoomActions(),
            call = noOpCallFacade,
            state = BottomBarState(
                participant = buildParticipants(25).first(),
                onToggleParticipants = {},
                onShowChat = {},
                onToggleMoreActions = {},
                isChatShow = false,
                layoutType = CallLayoutType.SPEAKER_LAYOUT,
            ),
        )
    }
}
