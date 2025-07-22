package com.vonage.android.screen.room.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.util.preview.buildParticipants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingRoomContent(
    participants: ImmutableList<Participant>,
    sheetState: SheetState,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        AdaptiveGrid(
            participants = participants,
            modifier = Modifier
                .fillMaxSize()
                .testTag(MEETING_ROOM_PARTICIPANTS_GRID)
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

object MeetingRoomContentTestTags {
    const val MEETING_ROOM_PARTICIPANTS_GRID = "meeting_room_participants_grid"
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("MagicNumber")
@PreviewLightDark
@Composable
internal fun MeetingRoomContentPreview() {
    VonageVideoTheme {
        val sheetState = rememberModalBottomSheetState()
        MeetingRoomContent(
            participants = buildParticipants(5).toImmutableList(),
            sheetState = sheetState,
            showBottomSheet = false,
            onDismissRequest = { },
        )
    }
}
