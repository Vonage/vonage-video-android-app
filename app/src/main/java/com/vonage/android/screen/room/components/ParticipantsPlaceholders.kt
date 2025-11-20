package com.vonage.android.screen.room.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.compose.theme.VonageVideoTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

const val DEFAULT_OVERLAP_SPACE = -8

@Composable
fun ParticipantsPlaceholders(
    participantNames: ImmutableList<String>,
    modifier: Modifier = Modifier,
    maxVisiblePlaceholders: Int = 2,
    spacedBy: Dp = DEFAULT_OVERLAP_SPACE.dp,
) {
    val visiblePlaceholders = participantNames.take(maxVisiblePlaceholders)
    val additionalCount = participantNames.size - maxVisiblePlaceholders

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(8.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(spacedBy), // Overlap
            verticalAlignment = Alignment.CenterVertically
        ) {
            visiblePlaceholders.forEachIndexed { index, participant ->
                AvatarInitials(
                    userName = participant,
                    textStyle = MaterialTheme.typography.displaySmall,
                    size = 64.dp,
                    modifier = Modifier
                        .zIndex((visiblePlaceholders.size - index).toFloat())
                        .border(2.dp, Color.DarkGray, CircleShape)
                )
            }

            if (additionalCount > 0) {
                AdditionalParticipantsAvatar(
                    count = additionalCount,
                    size = 64.dp,
                    zIndex = 0f,
                    textStyle = MaterialTheme.typography.displaySmall,
                )
            }
        }
    }
}

@Composable
fun AdditionalParticipantsAvatar(
    count: Int,
    modifier: Modifier = Modifier,
    zIndex: Float = 0f,
    size: Dp = 96.dp, // move to theme
    textStyle: TextStyle = MaterialTheme.typography.displayMedium,
) {
    Box(
        modifier = modifier
            .zIndex(zIndex)
            .size(size)
            .background(Color.Gray, CircleShape)
            .border(BorderStroke(2.dp, Color.White), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            color = Color.White,
            style = textStyle,
        )
    }
}

@PreviewLightDark
@Composable
internal fun ParticipantsPlaceholdersPreview() {
    VonageVideoTheme {
        ParticipantsPlaceholders(
            participantNames = persistentListOf(
                "Anand",
                "Chetan",
                "Daniel",
                "Goncalo",
            ),
        )
    }
}
