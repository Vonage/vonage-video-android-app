package com.vonage.android.screen.room.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.vonage.android.audio.ui.AudioDevices
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.reporting.ReportIssueScreen
import com.vonage.android.screen.room.CaptionsState
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.RecordingState
import com.vonage.android.screen.room.ScreenSharingState
import com.vonage.android.screen.room.components.emoji.EmojiSelector
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun CallModals(
    participants: ImmutableList<Participant>,
    actions: MeetingRoomActions,
    showParticipants: Boolean,
    participantsSheetState: SheetState,
    recordingState: RecordingState,
    screenSharingState: ScreenSharingState,
    captionsState: CaptionsState,
    audioDeviceSelectorSheetState: SheetState,
    moreActionsSheetState: SheetState,
    showAudioDeviceSelector: Boolean,
    showMoreActions: Boolean,
    showReporting: Boolean,
    reportSheetState: SheetState,
    onShowReporting: () -> Unit,
    onDismissReporting: () -> Unit,
    onDismissAudioDeviceSelector: () -> Unit,
    onDismissMoreActions: () -> Unit,
    onDismissParticipants: () -> Unit,
    onEmojiClick: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
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
    if (showMoreActions) {
        ModalBottomSheet(
            onDismissRequest = onDismissMoreActions,
            sheetState = moreActionsSheetState,
        ) {
            EmojiSelector(
                onEmojiClick = { emoji -> onEmojiClick(emoji) },
            )
            MoreActionsGrid(
                recordingState = recordingState,
                screenSharingState = screenSharingState,
                captionsState = captionsState,
                onShowReporting = {
                    onDismissMoreActions()
                    onShowReporting()
                },
                actions = actions,
            )
        }
    }
    if (showReporting) {
        ModalBottomSheet(
            onDismissRequest = onDismissReporting,
            sheetState = reportSheetState,
        ) {
            ReportIssueScreen(
                onClose = {
                    scope.launch {
                        reportSheetState.hide()
                        onDismissReporting()
                    }
                },
            )
        }
    }
}
