package com.vonage.android.screen.room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.components.AdaptiveGrid
import com.vonage.android.screen.room.components.BottomBar
import com.vonage.android.screen.room.components.ParticipantsList
import com.vonage.android.screen.room.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingRoomScreen(
    uiState: RoomUiState,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
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
                        actions = actions,
                    )
                },
                bottomBar = {
                    BottomBar(
                        onToggleMic = actions.onToggleMic,
                        onToggleCamera = actions.onToggleCamera,
                        onToggleParticipants = { showBottomSheet = !showBottomSheet },
                        onEndCall = actions.onEndCall,
                        isMicEnabled = publisher?.isMicEnabled ?: false,
                        isCameraEnabled = publisher?.isCameraEnabled ?: false,
                        participantsCount = participants.size,
                    )
                }
            ) { contentPadding ->
                Box(
                    modifier = modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                ) {
                    AdaptiveGrid(
                        participants = participants,
                        modifier = Modifier
                            .fillMaxSize()
                    )

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState,
                        ) {
                            ParticipantsList(participants = participants)
                        }
                    }
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
