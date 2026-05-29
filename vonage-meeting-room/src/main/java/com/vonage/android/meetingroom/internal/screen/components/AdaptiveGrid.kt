package com.vonage.android.meetingroom.internal.screen.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.meetingroom.internal.screen.MeetingRoomActions
import com.vonage.android.meetingroom.internal.util.MAX_GRID_TILES
import com.vonage.android.meetingroom.internal.util.gridLayoutFor
import com.vonage.android.meetingroom.internal.util.lazyStateVisibilityTracker
import com.vonage.android.meetingroom.internal.util.noOpCall
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun AdaptiveGrid(
    participants: ImmutableList<Participant>,
    call: CallFacade,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val dimensions = remember(participants.size, isLandscape) {
        gridLayoutFor(participantCount = minOf(participants.size, MAX_GRID_TILES), isLandscape = isLandscape)
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val maxVisibleItems = dimensions.columns * dimensions.rows
        val takeCount = remember(participants.size, dimensions) {
            when {
                maxVisibleItems >= participants.size -> maxVisibleItems
                else -> (maxVisibleItems - 1).coerceAtLeast(1)
            }
        }
        val visibleItems = participants.take(takeCount)

        val itemHeight = with(LocalDensity.current) {
            (constraints.maxHeight / dimensions.rows).toDp() - spacing
        }
        val itemWidth = with(LocalDensity.current) {
            (constraints.maxWidth / dimensions.columns).toDp() - spacing
        }

        val listState = lazyStateVisibilityTracker(call = call, lazyState = rememberLazyGridState())
        val pinnedIds by call.pinnedParticipantIds.collectAsStateWithLifecycle()

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(dimensions.columns),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            userScrollEnabled = false,
            overscrollEffect = null,
        ) {
            items(
                items = visibleItems,
                key = { participant -> participant.id },
                contentType = { "ParticipantVideoCard" },
            ) { participant ->
                ParticipantVideoCard(
                    modifier = Modifier
                        .width(itemWidth)
                        .height(itemHeight),
                    participant = participant,
                    actions = actions,
                    isPinned = participant.id in pinnedIds,
                )
            }
            if (participants.size > takeCount) {
                item(key = "placeholder") {
                    ParticipantsPlaceholders(
                        modifier = Modifier
                            .height(itemHeight)
                            .width(itemWidth),
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
            call = noOpCall,
            actions = MeetingRoomActions(),
        )
    }
}
