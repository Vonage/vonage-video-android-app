package com.vonage.android.meetingroom.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Pin2
import com.vonage.android.compose.vivid.icons.solid.Pin2Off
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.R

@Composable
fun ParticipantContextualActions(
    participant: Participant,
    actions: MeetingRoomActions,
    isPinned: Boolean,
    modifier: Modifier = Modifier,
) {
    val isMicEnabled by participant.isMicEnabled.collectAsStateWithLifecycle()
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(
                start = VonageVideoTheme.dimens.paddingSmall,
                bottom = VonageVideoTheme.dimens.paddingSmall,
            ),
            text = participant.name,
            color = VonageVideoTheme.colors.textSecondary,
            style = VonageVideoTheme.typography.heading2,
        )
        HorizontalDivider()
        ActionRow(
            onClick = { actions.onTogglePinParticipant(participant.id) },
            icon = if (isPinned) VividIcons.Solid.Pin2Off else VividIcons.Solid.Pin2,
            label = if (isPinned) {
                stringResource(R.string.meeting_room_unpin_participant)
            } else {
                stringResource(R.string.meeting_room_pin_participant)
            },
        )
        if (isMicEnabled) {
            ActionRow(
                onClick = { actions.onForceMuteParticipant(participant.id) },
                icon = VividIcons.Solid.MicMute,
                label = stringResource(R.string.meeting_room_mute_participant),
            )
        }
    }
}

@Composable
private fun ActionRow(onClick: () -> Unit, icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick)
            .padding(VonageVideoTheme.dimens.paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VonageVideoTheme.colors.onSurface,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
        )
        Text(
            text = label,
            color = VonageVideoTheme.colors.onSurface,
            style = VonageVideoTheme.typography.bodyBase,
        )
    }
}
