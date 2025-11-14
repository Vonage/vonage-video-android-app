package com.vonage.android.screen.room.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.noOpCallFacade
import com.vonage.android.util.lazyStateVisibilityTracker
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ActiveSpeakerLayout(
    participants: ImmutableList<Participant>,
    call: CallFacade,
    modifier: Modifier = Modifier,
) {
    val mainParticipant by call.activeSpeaker.collectAsStateWithLifecycle()
    val nonMainParticipant by remember(mainParticipant) {
        derivedStateOf { participants.filterNot { it.id == mainParticipant?.id } }
    }
    val listState = lazyStateVisibilityTracker(call = call, lazyState = rememberLazyListState())

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
        ) {
            mainParticipant?.let {
                SpotlightSpeaker(
                    modifier = Modifier.weight(0.7f),
                    participant = it,
                )
            } ?: Spacer(modifier = Modifier.weight(0.7f))
            LazyRow(
                state = listState,
                modifier = Modifier
                    .weight(0.3f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(
                    items = nonMainParticipant,
                    key = { it.id }
                ) { participant ->
                    ParticipantVideoCard(
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp),
                        participant = participant,
                    )
                }
            }
        }
    }
}

@Composable
fun SpotlightSpeaker(
    participant: Participant,
    modifier: Modifier = Modifier,
) {
    ParticipantVideoCard(
        modifier = modifier
            .padding(8.dp),
        participant = participant,
    )
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun ActiveSpeakerLayoutPreview() {
    VonageVideoTheme {
        ActiveSpeakerLayout(
            participants = buildParticipants(10).toImmutableList(),
            call = noOpCallFacade,
        )
    }
}
