package com.vonage.android.compose.layout

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.compose.components.ParticipantsPlaceholders
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal const val ASPECT_RATIO_16_9 = 16f / 9f

@Composable
fun ActiveSpeakerLayout(
    participants: ImmutableList<Participant>,
    call: CallFacade,
    participantContent: @Composable (Participant, Modifier) -> Unit,
    modifier: Modifier = Modifier,
    spotlightWeight: Float = 0.7f,
    otherParticipantsWeight: Float = 0.3f,
    otherParticipantsSize: Dp = 200.dp,
) {
    val mainParticipant by call.activeSpeaker.collectAsStateWithLifecycle()
    val nonMainParticipant by remember(mainParticipant) {
        derivedStateOf { participants.filterNot { it.id == mainParticipant?.id } }
    }

    val filmstripTakeCount = remember(nonMainParticipant.size) {
        when {
            nonMainParticipant.size <= MAX_FILMSTRIP_TILES -> nonMainParticipant.size
            else -> (MAX_FILMSTRIP_TILES - 1).coerceAtLeast(1)
        }
    }
    val visibleFilmstripItems = nonMainParticipant.take(filmstripTakeCount).toImmutableList()
    val overflowFilmstripNames = if (nonMainParticipant.size > filmstripTakeCount) {
        nonMainParticipant
            .takeLast(nonMainParticipant.size - filmstripTakeCount)
            .map { it.name }
            .toImmutableList()
    } else {
        persistentListOf()
    }

//    val listState = lazyStateVisibilityTracker(call = call, lazyState = rememberLazyListState())
    val listState = rememberLazyListState()
    //val movableParticipantContent = remember(participantContent) { movableContentOf(participantContent) }
    val movableParticipantContent = remember(participantContent) { participantContent }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                ActiveSpeakerHorizontalLayout(
                    mainParticipant = mainParticipant,
                    participantContent = movableParticipantContent,
                    spotlightWeight = spotlightWeight,
                    listState = listState,
                    otherParticipantsWeight = otherParticipantsWeight,
                    visibleFilmstripItems = visibleFilmstripItems,
                    overflowFilmstripNames = overflowFilmstripNames,
                    otherParticipantsSize = otherParticipantsSize,
                )
            }

            else -> {
                ActiveSpeakerVerticalLayout(
                    mainParticipant = mainParticipant,
                    participantContent = movableParticipantContent,
                    spotlightWeight = spotlightWeight,
                    listState = listState,
                    otherParticipantsWeight = otherParticipantsWeight,
                    visibleFilmstripItems = visibleFilmstripItems,
                    overflowFilmstripNames = overflowFilmstripNames,
                    otherParticipantsSize = otherParticipantsSize,
                )
            }
        }
    }
}

@Suppress("LongParameterList", "ContentSlotReused")
@Composable
private fun ActiveSpeakerVerticalLayout(
    mainParticipant: Participant?,
    participantContent: @Composable (Participant, Modifier) -> Unit,
    spotlightWeight: Float,
    listState: LazyListState,
    otherParticipantsWeight: Float,
    visibleFilmstripItems: ImmutableList<Participant>,
    overflowFilmstripNames: ImmutableList<String>,
    otherParticipantsSize: Dp,
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
    ) {
        mainParticipant?.let {
            participantContent(
                it,
                Modifier
                    .weight(spotlightWeight)
                    .fillMaxSize()
                    .padding(VonageVideoTheme.dimens.paddingSmall),
            )
        } ?: Spacer(modifier = Modifier.weight(spotlightWeight))
        LazyRow(
            state = listState,
            modifier = Modifier.weight(otherParticipantsWeight),
            horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            userScrollEnabled = false,
            overscrollEffect = null,
        ) {
            items(
                items = visibleFilmstripItems,
                key = { it.id },
            ) { participant ->
                participantContent(
                    participant,
                    Modifier
                        .width(otherParticipantsSize)
                        .height(otherParticipantsSize),
                )
            }
            if (overflowFilmstripNames.isNotEmpty()) {
                item(key = "placeholder") {
                    ParticipantsPlaceholders(
                        modifier = Modifier
                            .width(otherParticipantsSize)
                            .height(otherParticipantsSize),
                        participantNames = overflowFilmstripNames,
                    )
                }
            }
        }
    }
}

@Suppress("LongParameterList", "ContentSlotReused")
@Composable
private fun ActiveSpeakerHorizontalLayout(
    mainParticipant: Participant?,
    participantContent: @Composable (Participant, Modifier) -> Unit,
    spotlightWeight: Float,
    listState: LazyListState,
    otherParticipantsWeight: Float,
    visibleFilmstripItems: ImmutableList<Participant>,
    overflowFilmstripNames: ImmutableList<String>,
    otherParticipantsSize: Dp,
) {
    Row(
        horizontalArrangement = Arrangement.End,
    ) {
        mainParticipant?.let {
            participantContent(
                it,
                Modifier
                    .weight(spotlightWeight)
                    .fillMaxSize()
                    .padding(VonageVideoTheme.dimens.paddingSmall),
            )
        } ?: Spacer(modifier = Modifier.weight(spotlightWeight))
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(otherParticipantsWeight),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
            userScrollEnabled = false,
            overscrollEffect = null,
        ) {
            items(
                items = visibleFilmstripItems,
                key = { it.id },
            ) { participant ->
                participantContent(
                    participant,
                    Modifier
                        .height(otherParticipantsSize)
                        .aspectRatio(ASPECT_RATIO_16_9),
                )
            }
            if (overflowFilmstripNames.isNotEmpty()) {
                item(key = "placeholder") {
                    ParticipantsPlaceholders(
                        modifier = Modifier
                            .height(otherParticipantsSize)
                            .aspectRatio(ASPECT_RATIO_16_9),
                        participantNames = overflowFilmstripNames,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun ActiveSpeakerLayoutNoSpeakerPreview() {
    VonageVideoTheme {
        ActiveSpeakerLayout(
            participants = buildParticipants(3).toImmutableList(),
            call = buildCallWithParticipants(3),
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
@PreviewScreenSizes
@Composable
internal fun ActiveSpeakerLayoutFilledSpotlightPreview() {
    val participants = buildParticipants(3).toImmutableList()
    VonageVideoTheme {
        ActiveSpeakerLayout(
            participants = participants,
            call = buildCallWithParticipants(3, activeSpeaker = participants.first()),
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
internal fun ActiveSpeakerLayoutFilmstripOverflowPreview() {
    val participants = buildParticipants(4).toImmutableList()
    VonageVideoTheme {
        ActiveSpeakerLayout(
            participants = participants,
            call = buildCallWithParticipants(4, activeSpeaker = participants.first()),
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
