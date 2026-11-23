package com.vonage.android.compose.layout

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.compose.components.ParticipantsPlaceholders
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.util.ReportVisibleParticipants
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Fixed grid of participant tiles sized to fill the available space.
 *
 * A single [Layout] with keyed children: tiles keep their identity (and native video view) when
 * they move between rows, and sizes are resolved during measurement instead of in a
 * subcomposition.
 */
@Composable
fun AdaptiveGrid(
    participants: ImmutableList<Participant>,
    call: CallFacade,
    participantContent: @Composable (Participant, Modifier) -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    onPlaceholderClick: () -> Unit = {},
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val dimensions = remember(participants.size, isLandscape) {
        gridLayoutFor(
            participantCount = minOf(participants.size, MAX_GRID_TILES),
            isLandscape = isLandscape,
        )
    }
    val maxVisibleItems = dimensions.columns * dimensions.rows
    val takeCount = when {
        maxVisibleItems >= participants.size -> maxVisibleItems
        else -> (maxVisibleItems - 1).coerceAtLeast(1)
    }
    val visibleItems = remember(participants, takeCount) { participants.take(takeCount) }
    val overflowNames = remember(participants, takeCount) {
        participants.drop(takeCount).map { it.name }.toImmutableList()
    }

    ReportVisibleParticipants(
        call = call,
        participantIds = remember(visibleItems) { visibleItems.map { it.id } },
    )

    Layout(
        modifier = modifier.fillMaxSize(),
        content = {
            // One Box per cell so each cell is exactly one measurable, whatever the content emits.
            visibleItems.forEach { participant ->
                key(participant.id) {
                    Box(propagateMinConstraints = true) {
                        participantContent(participant, Modifier.fillMaxSize())
                    }
                }
            }
            if (overflowNames.isNotEmpty()) {
                Box(propagateMinConstraints = true) {
                    ParticipantsPlaceholders(
                        modifier = Modifier.fillMaxSize(),
                        participantNames = overflowNames,
                        onClick = onPlaceholderClick,
                    )
                }
            }
        },
    ) { measurables, constraints ->
        val spacingPx = spacing.roundToPx()
        val cellWidth = ((constraints.maxWidth - spacingPx * (dimensions.columns - 1)) / dimensions.columns)
            .coerceAtLeast(0)
        val cellHeight = ((constraints.maxHeight - spacingPx * (dimensions.rows - 1)) / dimensions.rows)
            .coerceAtLeast(0)
        val cellConstraints = Constraints.fixed(cellWidth, cellHeight)
        val placeables = measurables.map { it.measure(cellConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val column = index % dimensions.columns
                val row = index / dimensions.columns
                placeable.placeRelative(
                    x = column * (cellWidth + spacingPx),
                    y = row * (cellHeight + spacingPx),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun AdaptiveGrid4ParticipantsPreview() {
    VonageVideoTheme {
        AdaptiveGrid(
            participants = buildParticipants(4).toImmutableList(),
            call = buildCallWithParticipants(4),
            participantContent = { participant, modifier ->
                Box(
                    modifier = modifier.background(VonageVideoTheme.colors.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    AvatarInitials(userName = participant.name)
                }
            },
        )
    }
}

@PreviewLightDark
@Composable
internal fun AdaptiveGrid7ParticipantsOverflowPreview() {
    VonageVideoTheme {
        AdaptiveGrid(
            participants = buildParticipants(7).toImmutableList(),
            call = buildCallWithParticipants(7),
            participantContent = { participant, modifier ->
                Box(
                    modifier = modifier.background(VonageVideoTheme.colors.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    AvatarInitials(userName = participant.name)
                }
            },
        )
    }
}
