package com.vonage.android.screen.room.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import com.vonage.android.compose.components.VideoRenderer
import com.vonage.android.compose.components.VideoRenderer2
import com.vonage.android.compose.modifier.recomposeHighlighter
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.noOpCallFacade
import com.vonage.android.util.lazyStateWithVisibilityNotification
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.VideoSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SimpleGrid(
    participants: ImmutableList<Participant>,
    call: CallFacade,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp
) {
    val listState = lazyStateWithVisibilityNotification(call = call, lazyState = rememberLazyGridState())

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
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
            GridItemCard(
                participant = participant,
                modifier = modifier
                    .recomposeHighlighter(),
            )
        }
    }
}

@Composable
private fun GridItemCard(
    participant: Participant,
    modifier: Modifier = Modifier,
) {
    val isCameraEnabled by participant.isCameraEnabled.collectAsStateWithLifecycle()
    val isMicEnabled by participant.isMicEnabled.collectAsStateWithLifecycle()
    val isSpeaking by participant.isTalking.collectAsStateWithLifecycle()
    
    val shape = remember { RoundedCornerShape(8.dp) }
    
    Card(
        modifier = modifier,
        shape = shape,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (isCameraEnabled) {
                VideoRenderer2(
                    modifier = Modifier.height(222.dp),
                    participant = participant,
                )
            } else {
                Box(
                    modifier = Modifier.height(222.dp)
                )
            }

            if (participant.videoSource == VideoSource.CAMERA) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MicrophoneIcon(
                        modifier = Modifier,
                        isMicEnabled = isMicEnabled,
                    )
                    if (isSpeaking) {
                        Text("Speaking...")
                    }
                }
            }

            if (participant.id.isNotBlank()) {
                ParticipantLabel(participant.id)
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun SimpleGridPreview() {
    VonageVideoTheme {
        SimpleGrid(
            participants = buildParticipants(10).toImmutableList(),
            call = noOpCallFacade,
        )
    }
}
