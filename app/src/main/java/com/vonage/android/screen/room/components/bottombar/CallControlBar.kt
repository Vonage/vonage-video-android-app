package com.vonage.android.screen.room.components.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.components.bottombar.ControlButton
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.EndCall
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Microphone2
import com.vonage.android.compose.vivid.icons.solid.MoreVertical
import com.vonage.android.compose.vivid.icons.solid.Video
import com.vonage.android.compose.vivid.icons.solid.VideoOff
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.components.bottombar.BottomBarTestTags.BOTTOM_BAR_CAMERA_BUTTON
import com.vonage.android.screen.room.components.bottombar.BottomBarTestTags.BOTTOM_BAR_END_CALL_BUTTON
import com.vonage.android.screen.room.components.bottombar.BottomBarTestTags.BOTTOM_BAR_MIC_BUTTON
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun CallControlBar(
    publisher: Participant?,
    roomActions: MeetingRoomActions,
    onShowMore: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val isMicEnabled by remember(publisher) {
        publisher?.let { publisher.isMicEnabled } ?: MutableStateFlow(false)
    }.collectAsStateWithLifecycle()
    val isCameraEnabled by remember(publisher) {
        publisher?.let { publisher.isCameraEnabled } ?: MutableStateFlow(false)
    }.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.8f), VonageVideoTheme.shapes.large)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ControlButton(
            modifier = Modifier
                .testTag(BOTTOM_BAR_MIC_BUTTON),
            onClick = roomActions.onToggleMic,
            icon = if (isMicEnabled) VividIcons.Solid.Microphone2 else VividIcons.Solid.MicMute,
            isActive = isMicEnabled,
        )

        ControlButton(
            modifier = Modifier
                .testTag(BOTTOM_BAR_CAMERA_BUTTON),
            onClick = roomActions.onToggleCamera,
            icon = if (isCameraEnabled) VividIcons.Solid.Video else VividIcons.Solid.VideoOff,
            isActive = isCameraEnabled,
        )

        content()

        ControlButton(
            onClick = onShowMore,
            icon = VividIcons.Solid.MoreVertical,
        )

        ControlButton(
            modifier = Modifier
                .background(Color.Red, CircleShape)
                .testTag(BOTTOM_BAR_END_CALL_BUTTON),
            onClick = roomActions.onEndCall,
            icon = VividIcons.Solid.EndCall,
            isActive = true,
        )
    }
}

@Preview
@Composable
internal fun CallControlBarPreview() {
    VonageVideoTheme {
        CallControlBar(
            publisher = buildParticipants(1).first(),
            roomActions = MeetingRoomActions(),
            onShowMore = {},
        ) { }
    }
}
