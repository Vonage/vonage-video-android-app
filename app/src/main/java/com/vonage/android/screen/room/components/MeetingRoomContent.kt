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
import com.vonage.android.audio.ui.AudioDevices
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.util.preview.buildParticipants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun MeetingRoomContent(
    participants: ImmutableList<Participant>,
    audioLevel: Float,
    participantsSheetState: SheetState,
    audioDeviceSelectorSheetState: SheetState,
    showParticipants: Boolean,
    showAudioDeviceSelector: Boolean,
    onDismissParticipants: () -> Unit,
    onDismissAudioDeviceSelector: () -> Unit,
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
                .testTag(MEETING_ROOM_PARTICIPANTS_GRID),
            audioLevel = audioLevel,
        )
        if (showParticipants) {
            ModalBottomSheet(
                onDismissRequest = onDismissParticipants,
                sheetState = participantsSheetState,
            ) {
                ParticipantsList(participants = participants)
            }
        }
        if (showAudioDeviceSelector) {
            ModalBottomSheet(
                onDismissRequest = onDismissAudioDeviceSelector,
                sheetState = audioDeviceSelectorSheetState,
            ) {
                AudioDevices(
                    onDismissRequest = onDismissAudioDeviceSelector,
                )
            }
        }
    }
}

object MeetingRoomContentTestTags {
    const val MEETING_ROOM_PARTICIPANTS_GRID = "meeting_room_participants_grid"
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
internal fun MeetingRoomContentPreview() {
    VonageVideoTheme {
        val sheetState = rememberModalBottomSheetState()
        MeetingRoomContent(
            participants = buildParticipants(5).toImmutableList(),
            showParticipants = false,
            onDismissParticipants = { },
            participantsSheetState = sheetState,
            audioDeviceSelectorSheetState = sheetState,
            showAudioDeviceSelector = false,
            onDismissAudioDeviceSelector = {},
            audioLevel = 0.5f,
        )
    }
}
