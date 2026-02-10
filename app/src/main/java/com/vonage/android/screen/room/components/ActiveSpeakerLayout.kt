package com.vonage.android.screen.room.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.util.lazyStateVisibilityTracker
import com.vonage.android.util.noOpCall
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ActiveSpeakerLayout(
    participants: ImmutableList<Participant>,
    call: CallFacade,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
    spotlightWeight: Float = 0.7f,
    otherParticipantsWeight: Float = 0.3f,
    otherParticipantsSize: Dp = 200.dp
) {
    val mainParticipant by call.activeSpeaker.collectAsStateWithLifecycle()
    val nonMainParticipant by remember(mainParticipant) {
        derivedStateOf { participants.filterNot { it.id == mainParticipant?.id } }
    }
    val listState = lazyStateVisibilityTracker(call = call, lazyState = rememberLazyListState())

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                ActiveSpeakerHorizontalLayout(
                    mainParticipant = mainParticipant,
                    actions = actions,
                    spotlightWeight = spotlightWeight,
                    listState = listState,
                    otherParticipantsWeight = otherParticipantsWeight,
                    nonMainParticipant = nonMainParticipant.toImmutableList(),
                    otherParticipantsSize = otherParticipantsSize,
                )
            }

            else -> {
                ActiveSpeakerVerticalLayout(
                    mainParticipant = mainParticipant,
                    actions = actions,
                    spotlightWeight = spotlightWeight,
                    listState = listState,
                    otherParticipantsWeight = otherParticipantsWeight,
                    nonMainParticipant = nonMainParticipant.toImmutableList(),
                    otherParticipantsSize = otherParticipantsSize,
                )
            }
        }
    }
}

@Composable
private fun ActiveSpeakerVerticalLayout(
    mainParticipant: Participant?,
    actions: MeetingRoomActions,
    spotlightWeight: Float,
    listState: LazyListState,
    otherParticipantsWeight: Float,
    nonMainParticipant: ImmutableList<Participant>,
    otherParticipantsSize: Dp
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
    ) {
        mainParticipant?.let {
            SpotlightSpeaker(
                modifier = Modifier.weight(spotlightWeight),
                participant = it,
                actions = actions,
            )
        } ?: Spacer(modifier = Modifier.weight(spotlightWeight))
        LazyRow(
            state = listState,
            modifier = Modifier
                .weight(otherParticipantsWeight),
            horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(
                items = nonMainParticipant,
                key = { it.id }
            ) { participant ->
                ParticipantVideoCard(
                    modifier = Modifier
                        .width(otherParticipantsSize)
                        .height(otherParticipantsSize),
                    participant = participant,
                    actions = actions,
                )
            }
        }
    }
}


@Composable
private fun ActiveSpeakerHorizontalLayout(
    mainParticipant: Participant?,
    actions: MeetingRoomActions,
    spotlightWeight: Float,
    listState: LazyListState,
    otherParticipantsWeight: Float,
    nonMainParticipant: ImmutableList<Participant>,
    otherParticipantsSize: Dp
) {
    Row(
        horizontalArrangement = Arrangement.End,
    ) {
        mainParticipant?.let {
            SpotlightSpeaker(
                modifier = Modifier.weight(spotlightWeight),
                participant = it,
                actions = actions,
            )
        } ?: Spacer(modifier = Modifier.weight(spotlightWeight))
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(otherParticipantsWeight),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
        ) {
            items(
                items = nonMainParticipant,
                key = { it.id }
            ) { participant ->
                ParticipantVideoCard(
                    modifier = Modifier
                        .height(otherParticipantsSize)
                        .aspectRatio(ASPECT_RATIO_16_9),
                    participant = participant,
                    actions = actions,
                )
            }
        }
    }
}

const val ASPECT_RATIO_16_9 = 16f / 9f

@Composable
private fun SpotlightSpeaker(
    participant: Participant,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    ParticipantVideoCard(
        modifier = modifier
            .padding(VonageVideoTheme.dimens.paddingSmall),
        participant = participant,
        actions = actions,
    )
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun ActiveSpeakerLayoutPreview() {
    VonageVideoTheme {
        ActiveSpeakerLayout(
            participants = buildParticipants(10).toImmutableList(),
            call = noOpCall,
            actions = MeetingRoomActions(),
        )
    }
}
