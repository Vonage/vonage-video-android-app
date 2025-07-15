package com.vonage.android.screen.room

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.Participant
import com.vonage.android.kotlin.VeraPublisher
import com.vonage.android.screen.room.components.BottomBar
import com.vonage.android.screen.room.components.ParticipantVideoCard
import com.vonage.android.screen.room.components.TopBar
import com.vonage.android.screen.waiting.previewCamera

@Composable
fun VideoCallScreen(
    uiState: RoomUiState,
    modifier: Modifier = Modifier,
    onEndCall: () -> Unit = {},
    onToggleMic: () -> Unit = {},
    onToggleCamera: () -> Unit = {},
    onToggleChat: () -> Unit = {},
    onToggleParticipants: () -> Unit = {},
    isMicEnabled: Boolean = false,
    isCameraEnabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.background) // Dark background
    ) {

        when (uiState) {
            is RoomUiState.Content -> {
                TopBar(
                    roomName = uiState.roomName,
                    modifier = Modifier.align(Alignment.TopStart)
                )

                val participants by uiState.call.participantsStateFlow.collectAsState()
                val publisher = participants.filterIsInstance<VeraPublisher>().firstOrNull()

                VideoContent(
                    participants = participants,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 64.dp)
                )

                BottomBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onToggleMic = {
                        participants
                            .filterIsInstance<VeraPublisher>()
                            .firstOrNull { it.toggleAudio() }
                    },
                    onToggleCamera = {
                        participants
                            .filterIsInstance<VeraPublisher>()
                            .firstOrNull { it.toggleVideo() }
                    },
                    onToggleChat = onToggleChat,
                    onToggleParticipants = onToggleParticipants,
                    onEndCall = onEndCall,
                    isMicEnabled = publisher?.isMicEnabled ?: false,
                    isCameraEnabled = publisher?.isCameraEnabled ?: false,
                    participantsCount = participants.size,
                )
            }

            is RoomUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun VideoContent(
    participants: List<Participant>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 250.dp),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = participants,
            key = { participant -> participant.name },
        ) { participant ->
            ParticipantVideoCard(
                name = participant.name,
                isCameraEnabled = participant.isCameraEnabled,
                isMicEnabled = participant.isMicEnabled,
                view = participant.view,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun VideoCallScreenPreview() {
    object : Participant {
        override var name: String = "John Doe"
        override val isMicEnabled: Boolean = true
        override val isCameraEnabled: Boolean = false
        override val view: View = previewCamera()
        override fun toggleAudio(): Boolean = true
        override fun toggleVideo(): Boolean = true
    }
    VonageVideoTheme {
//        VideoCallScreen(
//            uiState = RoomUiState.Content(
//                roomName = "sample-name",
//                call =
//            )
//        )
    }
}
