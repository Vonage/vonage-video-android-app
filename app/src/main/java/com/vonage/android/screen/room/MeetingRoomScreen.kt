package com.vonage.android.screen.room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.Participant
import com.vonage.android.kotlin.VeraPublisher
import com.vonage.android.screen.room.components.BottomBar
import com.vonage.android.screen.room.components.ParticipantVideoCard
import com.vonage.android.screen.room.components.ParticipantsList
import com.vonage.android.screen.room.components.TopBar
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingRoomScreen(
    uiState: RoomUiState,
    modifier: Modifier = Modifier,
    onEndCall: () -> Unit = {},
    onToggleMic: () -> Unit = {},
    onToggleCamera: () -> Unit = {},
    onToggleParticipants: () -> Unit = {},
) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    when (uiState) {
        is RoomUiState.Content -> {
            val participants by uiState.call.participantsStateFlow.collectAsState()
            val publisher = participants.filterIsInstance<VeraPublisher>().firstOrNull()

            Scaffold(
                topBar = {
                    TopBar(
                        roomName = uiState.roomName,
                    )
                },
            ) { contentPadding ->
                Box(
                    modifier = modifier
                        .padding(top = 24.dp)
                        .consumeWindowInsets(contentPadding)
                        .fillMaxSize()
                ) {
                    VideoContent(
                        participants = participants,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 64.dp)
                    )

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            sheetState = sheetState,
                        ) {
                            ParticipantsList(
                                participants = participants,
                            )
                        }
                    }

                    BottomBar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        onToggleMic = {
                            publisher?.toggleAudio()
                        },
                        onToggleCamera = {
                            publisher?.toggleVideo()
                        },
                        onToggleParticipants = {
                            showBottomSheet = !showBottomSheet
                        },
                        onEndCall = onEndCall,
                        isMicEnabled = publisher?.isMicEnabled ?: false,
                        isCameraEnabled = publisher?.isCameraEnabled ?: false,
                        participantsCount = participants.size,
                    )
                }
            }
        }

        is RoomUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(VonageVideoTheme.colors.background)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun VideoContent(
    participants: ImmutableList<Participant>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 200.dp),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = participants,
            key = { participant -> participant.id },
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
//    object : Participant {
//        override var name: String = "John Doe"
//        override val isMicEnabled: Boolean = true
//        override val isCameraEnabled: Boolean = false
//        override val view: View = previewCamera()
//        override fun toggleAudio(): Boolean = true
//        override fun toggleVideo(): Boolean = true
//    }
    VonageVideoTheme {
//        VideoCallScreen(
//            uiState = RoomUiState.Content(
//                roomName = "sample-name",
//                call =
//            )
//        )
    }
}
