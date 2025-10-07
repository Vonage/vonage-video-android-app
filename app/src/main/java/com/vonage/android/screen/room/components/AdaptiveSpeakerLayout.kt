package com.vonage.android.screen.room.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.noOpCallFacade
import com.vonage.android.util.preview.buildParticipants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AdaptiveSpeakerLayout(
    participants: ImmutableList<Participant>,
    call: CallFacade,
    modifier: Modifier = Modifier,
    minItemWidth: Dp = 95.dp,
    spacing: Dp = 8.dp
) {
    if (participants.isEmpty()) return

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
    ) {
        val mainParticipant by call.mainSpeaker.collectAsStateWithLifecycle()
        val availableWidth = maxWidth - spacing
        val itemsPerRow = ((availableWidth + spacing) / (minItemWidth + spacing)).toInt().coerceAtLeast(1)
        val itemsPerCol = (maxHeight / minItemWidth).toInt().coerceAtLeast(1)

        val orientation = LocalConfiguration.current.orientation

        SpotlightSpeaker(
            call = call,
        )

        val visibleItems by remember(participants) {
            derivedStateOf {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    participants.filter { it.id != mainParticipant?.id }.take(itemsPerRow)
                } else {
                    participants.filter { it.id != mainParticipant?.id }.take(itemsPerCol)
                }
            }
        }

        if (visibleItems.isNotEmpty()) {
            val listState =
                lazyStateWithVisibilityNotification(call = call, lazyState = rememberLazyListState())

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LazyColumn(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(maxWidth / COLUMN_WIDTH_FACTOR)
                ) {
                    items(
                        items = visibleItems,
                        key = { participant -> participant.id },
                        contentType = { "ParticipantVideoCard" },
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
                            call = call,
                            isSpeaking = participant.isSpeaking,
                            isShowVolumeIndicator = participant is VeraPublisher,
                            videoSource = participant.videoSource,
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    state = listState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                ) {
                    items(items = visibleItems, key = { p -> p.id }) { participant ->
                        ParticipantVideoCard(
                            modifier = Modifier
                                .width(maxWidth / 2)
                                .aspectRatio(ASPECT_RATIO_16_9)
                                .padding(8.dp),
                            name = participant.name,
                            isCameraEnabled = participant.isCameraEnabled,
                            isMicEnabled = participant.isMicEnabled,
                            view = participant.view,
                            call = call,
                            isSpeaking = participant.isSpeaking,
                            isShowVolumeIndicator = participant is VeraPublisher,
                            videoSource = participant.videoSource,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScope.SpotlightSpeaker(
    call: CallFacade,
    modifier: Modifier = Modifier,
) {
    val mainParticipant by call.mainSpeaker.collectAsStateWithLifecycle()

    mainParticipant?.let { participant ->
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
            call = call,
            isSpeaking = participant.isSpeaking,
            isShowVolumeIndicator = mainParticipant is VeraPublisher,
            videoSource = participant.videoSource,
        )
    }
}

@Composable
internal fun <T : ScrollableState> lazyStateWithVisibilityNotification(call: CallFacade, lazyState: T): T {
    val snapshotFlow: Flow<List<String>> = when (lazyState) {
        is LazyGridState -> {
            snapshotFlow {
                lazyState.layoutInfo.visibleItemsInfo.map { it.key as String }
            }
        }

        is LazyListState ->
            snapshotFlow {
                lazyState.layoutInfo.visibleItemsInfo.map { it.key as String }
            }

        else -> throw UnsupportedOperationException("Wrong initial state.") // Currently
    }
    DisposableEffect(call) {
        call.updateParticipantVisibilityFlow(snapshotFlow)
        onDispose {
            call.updateParticipantVisibilityFlow(flowOf())
        }
    }
    return lazyState
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
