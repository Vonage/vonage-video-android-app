package com.vonage.android.screen.room.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.util.preview.buildParticipants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AdaptiveGrid(
    participants: ImmutableList<Participant>,
    audioLevel: Float,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 225.dp,
    minItemWidth: Dp = 300.dp,
    spacing: Dp = 8.dp
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val availableWidth = maxWidth - spacing
        val itemsPerRow = ((availableWidth + spacing) / (minItemWidth + spacing)).toInt().coerceAtLeast(1)
        val availableHeight = maxHeight - spacing
        val rowsVisible = ((availableHeight + spacing) / (itemHeight + spacing)).toInt().coerceAtLeast(1)

        val maxVisibleItems = itemsPerRow * rowsVisible
        val takeCount = when {
            maxVisibleItems >= participants.size -> maxVisibleItems
            else -> (maxVisibleItems - 1).coerceAtLeast(1)
        }
        val visibleItems = participants.take(takeCount)

        LazyVerticalGrid(
            columns = GridCells.Fixed(itemsPerRow),
            contentPadding = PaddingValues(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(visibleItems) { participant ->
                ParticipantVideoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    name = participant.name,
                    isCameraEnabled = participant.isCameraEnabled,
                    isMicEnabled = participant.isMicEnabled,
                    view = participant.view,
                    audioLevel = audioLevel,
                    isSpeaking = participant.isSpeaking,
                )
            }

            if (participants.size > takeCount) {
                item {
                    ParticipantsPlaceholders(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth(),
                        participantNames = participants
                            .takeLast(participants.size - takeCount)
                            .map { it.name }
                            .toImmutableList(),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun AdaptiveGridPreview() {
    VonageVideoTheme {
        AdaptiveGrid(
            participants = buildParticipants(10).toImmutableList(),
            audioLevel = 0.6f,
        )
    }
}
