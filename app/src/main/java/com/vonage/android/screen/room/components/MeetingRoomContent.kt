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
import com.vonage.android.audio.AudioDevicesHandler
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.screen.room.components.emoji.EmojiSelector
import com.vonage.android.util.preview.buildParticipants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun MeetingRoomContent(
    participants: ImmutableList<Participant>,
    actions: MeetingRoomActions,
    isRecording: Boolean,
    audioLevel: Float,
    participantsSheetState: SheetState,
    audioDeviceSelectorSheetState: SheetState,
    moreActionsSheetState: SheetState,
    showParticipants: Boolean,
    showAudioDeviceSelector: Boolean,
    showMoreActions: Boolean,
    onDismissParticipants: () -> Unit,
    onDismissAudioDeviceSelector: () -> Unit,
    onDismissMoreActions: () -> Unit,
    onEmojiClick: (String) -> Unit,
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
                AudioDevicesHandler(
                    onDismissRequest = onDismissAudioDeviceSelector,
                )
            }
        }
        if (showMoreActions) {
            ModalBottomSheet(
                onDismissRequest = onDismissMoreActions,
                sheetState = moreActionsSheetState,
            ) {
                EmojiSelector(
                    onEmojiClick = {
                        onEmojiClick(it)
                    },
                )
                MoreActionsGrid(
                    isRecording = isRecording,
                    actions = actions,
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
            audioLevel = 0.5f,
            participantsSheetState = sheetState,
            audioDeviceSelectorSheetState = sheetState,
            moreActionsSheetState = sheetState,
            showParticipants = false,
            showAudioDeviceSelector = false,
            showMoreActions = false,
            onDismissParticipants = {},
            onDismissAudioDeviceSelector = {},
            onDismissMoreActions = {},
            onEmojiClick = {},
            isRecording = false,
            actions = MeetingRoomActions()
        )
    }
}
