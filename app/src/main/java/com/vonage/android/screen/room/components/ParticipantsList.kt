package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Microphone2
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ParticipantsList(
    participants: ImmutableList<Participant>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .fillMaxWidth(),
    ) {
        item {
            Text(
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                text = stringResource(R.string.meeting_room_participants_list_title),
                color = VonageVideoTheme.colors.onSurface,
                style = VonageVideoTheme.typography.heading1,
            )
        }
        items(
            items = participants,
            key = { it.id },
        ) { participantState ->
            ParticipantRow(participantState)
        }
    }
}

@Composable
private fun ParticipantRow(participant: Participant) {
    val isMicEnabled by participant.isMicEnabled.collectAsStateWithLifecycle()
    Row(
        modifier = Modifier
            .padding(bottom = 4.dp, end = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        AvatarInitials(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp),
            size = 24.dp,
            userName = participant.name,
            textStyle = VonageVideoTheme.typography.caption,
        )
        Text(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
            text = participant.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = VonageVideoTheme.colors.onSurface,
            style = VonageVideoTheme.typography.bodyExtended,
        )
        if (!isMicEnabled) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = VividIcons.Solid.MicMute,
                contentDescription = null,
                tint = VonageVideoTheme.colors.onSurface,
            )
        } else {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = VividIcons.Solid.Microphone2,
                contentDescription = null,
                tint = VonageVideoTheme.colors.onSurface,
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun ParticipantsListPreview() {
    val sampleParticipant = buildParticipants(5)
    VonageVideoTheme {
        ParticipantsList(
            modifier = Modifier.background(VonageVideoTheme.colors.surface),
            participants = sampleParticipant.toImmutableList(),
        )
    }
}
