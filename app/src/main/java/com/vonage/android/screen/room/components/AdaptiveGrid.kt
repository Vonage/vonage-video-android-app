package com.vonage.android.screen.room.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
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
    columns: Int = 2,
    rows: Int = 3,
    spacing: Dp = 8.dp
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val maxVisibleItems = remember { columns * rows }
        val takeCount = remember(participants.size) {
            when {
                maxVisibleItems >= participants.size -> maxVisibleItems
                else -> (maxVisibleItems - 1).coerceAtLeast(1)
            }
        }
        val visibleItems = participants.take(takeCount)

        val itemHeight = maxHeight / rows
        val itemWidth = with(LocalDensity.current) {
            (constraints.maxWidth / columns).toDp() - spacing
        }

        val listState = lazyStateWithVisibilityNotification(call = call, lazyState = rememberLazyGridState())

//        Column(
//            modifier = Modifier.fillMaxSize(),
//        ) {
//            visibleItems.forEach { participant ->
//                ParticipantVideoCard(
//                    modifier = Modifier
//                        .recomposeHighlighter()
//                        .width(itemWidth)
//                        .height(itemHeight),
//                    participant = participant,
//                )
//            }
//        }

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(columns),
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
                )
            }
            if (participants.size > takeCount) {
                item(
                    key = "placeholder",
                ) {

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
            call = noOpCallFacade,
        )
    }
}
