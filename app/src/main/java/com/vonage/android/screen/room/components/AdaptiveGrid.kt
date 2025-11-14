package com.vonage.android.screen.room.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import com.vonage.android.compose.modifier.recomposeHighlighter
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.noOpCallFacade
import com.vonage.android.util.lazyStateWithVisibilityNotification
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.kotlin.model.PublisherState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("UnusedBoxWithConstraintsScope")
//@TraceRecomposition
@Composable
fun AdaptiveGrid(
    participants: List<Participant>,
    call: CallFacade,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 220.dp,
    minItemWidth: Dp = 120.dp,
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

        val listState = lazyStateWithVisibilityNotification(call = call, lazyState = rememberLazyGridState())

        LazyVerticalGrid(
            columns = GridCells.Fixed(itemsPerRow),
            state = listState,
            contentPadding = PaddingValues(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(
                items = visibleItems,
                key = { participant -> participant.id },
                contentType = { "ParticipantVideoCard" },
            ) { participant ->
                ParticipantVideoCard(
                    modifier = Modifier
                        .recomposeHighlighter()
                        .fillMaxWidth()
                        .height(itemHeight),
                    participant = participant,
                )
            }

            if (participants.size > takeCount) {
                item(
                    key = "placeholder",
                ) {
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
            call = noOpCallFacade,
        )
    }
}
