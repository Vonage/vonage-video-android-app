package com.vonage.android.screen.room.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.noOpCallFacade
import com.vonage.android.util.lazyStateWithVisibilityNotification
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AdaptiveSpeakerLayout(
    participants: List<Participant>,
    call: CallFacade,
    modifier: Modifier = Modifier,
    minItemWidth: Dp = 95.dp,
    spacing: Dp = 8.dp
) {
    val orientation = LocalConfiguration.current.orientation
    val mainParticipant by call.activeSpeaker.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
    ) {
        val itemsPerRow = (maxWidth / (minItemWidth + spacing)).toInt().coerceAtLeast(1)
        val itemsPerCol = (maxHeight / minItemWidth).toInt().coerceAtLeast(1)

        SpotlightSpeaker(
            audioLevel = call.localAudioLevel,
            activeSpeaker = call.activeSpeaker,
        )

        val takeCount by remember(participants) {
            derivedStateOf {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    when {
                        itemsPerRow >= participants.size -> itemsPerRow - 1
                        else -> (itemsPerRow - 2).coerceAtLeast(1)
                    }
                } else {
                    when {
                        itemsPerCol >= participants.size -> itemsPerCol
                        else -> (itemsPerCol - 2).coerceAtLeast(1)
                    }
                }
            }
        }
        val visibleItems by remember(participants) {
            derivedStateOf { participants.filter { it.id != mainParticipant?.id }.take(takeCount) }
        }

        if (visibleItems.isNotEmpty()) {
            val listState = lazyStateWithVisibilityNotification(call = call, lazyState = rememberLazyListState())

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LandscapeSpeakerLayout(
                    listState = listState,
                    visibleItems = visibleItems,
                    minItemWidth = minItemWidth,
                    audioLevel = call.localAudioLevel,
                    participants = participants,
                    takeCount = takeCount,
                )
            } else {
                PortraitSpeakerLayout(
                    listState = listState,
                    visibleItems = visibleItems,
                    audioLevel = call.localAudioLevel,
                    participants = participants,
                    takeCount = takeCount,
                )
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.PortraitSpeakerLayout(
    listState: LazyListState,
    visibleItems: List<Participant>,
    audioLevel: StateFlow<Float>,
    participants: List<Participant>,
    takeCount: Int
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        state = listState,
        modifier = Modifier
            .align(Alignment.BottomCenter),
    ) {
        items(
            items = visibleItems,
            key = { participant -> participant.id },
        ) { participant ->
            ParticipantVideoCard(
                modifier = Modifier
                    .width(maxWidth / 2)
                    .aspectRatio(ASPECT_RATIO_16_9)
                    .padding(8.dp),
                name = participant.name,
                isCameraEnabled = participant.isCameraEnabled,
                isMicEnabled = participant.isMicEnabled,
                view = participant.view,
                audioLevel = audioLevel,
                isSpeaking = participant.isTalking,
                isVolumeIndicatorVisible = participant is VeraPublisher,
                videoSource = participant.videoSource,
            )
        }
        if (participants.size > takeCount) {
            item(key = "placeholder") {
                ParticipantsPlaceholders(
                    modifier = Modifier
                        .width(maxWidth / 2)
                        .aspectRatio(ASPECT_RATIO_16_9)
                        .padding(8.dp),
                    participantNames = participants
                        .takeLast(participants.size - takeCount)
                        .map { it.name }
                        .toImmutableList(),
                )
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.LandscapeSpeakerLayout(
    listState: LazyListState,
    visibleItems: List<Participant>,
    minItemWidth: Dp,
    audioLevel: StateFlow<Float>,
    participants: List<Participant>,
    takeCount: Int
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .width(maxWidth / COLUMN_WIDTH_FACTOR)
    ) {
        items(
            items = visibleItems,
            key = { participant -> participant.id },
        ) { participant ->
            ParticipantVideoCard(
                modifier = Modifier
                    .height(minItemWidth)
                    .aspectRatio(ASPECT_RATIO_16_9)
                    .padding(8.dp),
                name = participant.name,
                isCameraEnabled = participant.isCameraEnabled,
                isMicEnabled = participant.isMicEnabled,
                view = participant.view,
                audioLevel = audioLevel,
                isSpeaking = participant.isTalking,
                isVolumeIndicatorVisible = participant is VeraPublisher,
                videoSource = participant.videoSource,
            )
        }
        if (participants.size > takeCount) {
            item(key = "placeholder") {
                ParticipantsPlaceholders(
                    modifier = Modifier
                        .height(minItemWidth)
                        .aspectRatio(ASPECT_RATIO_16_9),
                    participantNames = participants
                        .takeLast(participants.size - takeCount)
                        .map { it.name }
                        .toImmutableList(),
                )
            }
        }
    }
}

@Composable
fun BoxScope.SpotlightSpeaker(
    audioLevel: StateFlow<Float>,
    activeSpeaker: StateFlow<Participant?>,
    modifier: Modifier = Modifier,
) {
    val activeParticipant by activeSpeaker.collectAsStateWithLifecycle()

    activeParticipant?.let { participant ->
        ParticipantVideoCard(
            modifier = modifier
                .fillMaxHeight()
                .aspectRatio(ASPECT_RATIO_16_9)
                .padding(8.dp)
                .align(Alignment.Center),
            name = participant.name,
            isCameraEnabled = participant.isCameraEnabled,
            isMicEnabled = participant.isMicEnabled,
            view = participant.view,
            audioLevel = audioLevel,
            isSpeaking = participant.isTalking,
            isVolumeIndicatorVisible = activeParticipant is VeraPublisher,
            videoSource = participant.videoSource,
        )
    }
}

private const val ASPECT_RATIO_16_9 = 16f / 9f
private const val COLUMN_WIDTH_FACTOR = 4

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun AdaptiveSpeakerLayoutPreview() {
    VonageVideoTheme {
        AdaptiveSpeakerLayout(
            participants = buildParticipants(10).toImmutableList(),
            call = noOpCallFacade,
        )
    }
}
