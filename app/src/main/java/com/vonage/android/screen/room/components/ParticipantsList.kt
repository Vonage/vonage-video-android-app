package com.vonage.android.screen.room.components

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.Participant
import com.vonage.android.screen.components.AvatarInitials
import com.vonage.android.screen.waiting.previewCamera
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.random.Random

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
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Participants",
                color = MaterialTheme.colorScheme.inverseSurface,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        items(
            items = participants,
            key = { participant -> participant.id }
        ) { participant ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                AvatarInitials(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    size = 24.dp,
                    userName = participant.name,
                    textStyle = MaterialTheme.typography.labelSmall,
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    text = participant.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (!participant.isMicEnabled) {
                    Icon(
                        Icons.Default.MicOff,
                        contentDescription = "Muted",
                        tint = MaterialTheme.colorScheme.inverseSurface,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Muted",
                        tint = MaterialTheme.colorScheme.inverseSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun ParticipantsListPreview() {
    val p = object : Participant {
        override val id: String = Random(10).toString()
        override var name: String = "Name Sample"
        override val isMicEnabled: Boolean = false
        override val isCameraEnabled: Boolean = true
        override val view: View = previewCamera()
        override fun toggleAudio(): Boolean = true
        override fun toggleVideo(): Boolean = true
    }
    val p2 = object : Participant {
        override val id: String = Random(10).toString()
        override var name: String = "Name Sample Sample Sample Sample Sample Sample Sample Sample Sample Sample Sample"
        override val isMicEnabled: Boolean = true
        override val isCameraEnabled: Boolean = true
        override val view: View = previewCamera()
        override fun toggleAudio(): Boolean = true
        override fun toggleVideo(): Boolean = true
    }

    VonageVideoTheme {
        ParticipantsList(
            participants = persistentListOf(
                p,
                p2,
            )
        )
    }
}
