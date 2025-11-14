package com.vonage.android.screen.room.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.noOpCallFacade
import com.vonage.android.util.lazyStateVisibilityTracker
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ParticipantsLazyVerticalGridLayout(
    participants: ImmutableList<Participant>,
    call: CallFacade,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    rows: Int = 3,
    spacing: Dp = 8.dp
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val itemHeight = with(LocalDensity.current) {
            remember { (constraints.maxHeight / rows).toDp() - spacing }
        }
        val itemWidth = with(LocalDensity.current) {
            remember { (constraints.maxWidth / columns).toDp() - spacing }
        }
        val listState = lazyStateVisibilityTracker(call = call, lazyState = rememberLazyGridState())

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = listState,
            contentPadding = PaddingValues(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(
                items = participants,
                key = { it.id },
                contentType = { "ParticipantVideoCard" },
            ) { participant ->
                ParticipantVideoCard(
                    participant = participant,
                    modifier = modifier
                        .height(itemHeight)
                        .width(itemWidth)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun ParticipantsLazyVerticalGridLayoutPreview() {
    VonageVideoTheme {
        ParticipantsLazyVerticalGridLayout(
            participants = buildParticipants(10).toImmutableList(),
            call = noOpCallFacade,
        )
    }
}
