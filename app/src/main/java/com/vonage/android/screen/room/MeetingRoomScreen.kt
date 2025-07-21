package com.vonage.android.screen.room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.components.AdaptiveGrid
import com.vonage.android.screen.room.components.BottomBar
import com.vonage.android.screen.room.components.ParticipantsList
import com.vonage.android.screen.room.components.TopBar
import kotlinx.collections.immutable.ImmutableList

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
            val participants by uiState.call.participantsStateFlow.collectAsStateWithLifecycle()
            val publisher = participants.filterIsInstance<VeraPublisher>().firstOrNull()

            Scaffold(
                modifier = modifier,
                topBar = {
                    TopBar(
                        roomName = uiState.roomName,
                        actions = actions,
                    )
                },
                bottomBar = {
                    BottomBar(
                        actions = actions,
                        onToggleParticipants = { showBottomSheet = !showBottomSheet },
                        isMicEnabled = publisher?.isMicEnabled ?: false,
                        isCameraEnabled = publisher?.isCameraEnabled ?: false,
                        participantsCount = participants.size,
                    )
                }
            ) { contentPadding ->
                MeetingRoomContent(
                    contentPadding = contentPadding,
                    participants = participants,
                    sheetState = sheetState,
                    showBottomSheet = showBottomSheet,
                    onDismissRequest = { showBottomSheet = false },
                )
            }
        }

        is RoomUiState.Loading -> {
            MeetingRoomLoading()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeetingRoomContent(
    contentPadding: PaddingValues,
    participants: ImmutableList<Participant>,
    sheetState: SheetState,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
) {
    Box(
        modifier = Modifier
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
                onDismissRequest = onDismissRequest,
                sheetState = sheetState,
            ) {
                ParticipantsList(participants = participants)
            }
        }
    }
}

@Composable
private fun MeetingRoomLoading(
    modifier: Modifier = Modifier,
) {
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
