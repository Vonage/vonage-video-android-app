package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.audio.ui.AudioDevices
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.CaptionsState
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.RecordingState
import com.vonage.android.screen.room.ScreenSharingState
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.screen.room.components.emoji.EmojiSelector
import com.vonage.android.screen.reporting.ReportIssueScreen
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT
import com.vonage.android.util.preview.buildParticipants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun MeetingRoomContent(
    participants: ImmutableList<Participant>,
    actions: MeetingRoomActions,
    recordingState: RecordingState,
    screenSharingState: ScreenSharingState,
    captionsState: CaptionsState,
    call: CallFacade,
    participantsSheetState: SheetState,
    audioDeviceSelectorSheetState: SheetState,
    moreActionsSheetState: SheetState,
    showParticipants: Boolean,
    showAudioDeviceSelector: Boolean,
    showMoreActions: Boolean,
    showReporting: Boolean,
    reportSheetState: SheetState,
    onShowReporting: () -> Unit,
    onDismissReporting: () -> Unit,
    onDismissParticipants: () -> Unit,
    onDismissAudioDeviceSelector: () -> Unit,
    onDismissMoreActions: () -> Unit,
    onEmojiClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    layoutType: CallLayoutType = CallLayoutType.SPEAKER_LAYOUT,
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        when (layoutType) {
            CallLayoutType.GRID -> {
                AdaptiveGrid(
                    participants = participants,
                    call = call,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(MEETING_ROOM_PARTICIPANTS_GRID),
                )
            }

            CallLayoutType.SPEAKER_LAYOUT -> {
                AdaptiveSpeakerLayout(
                    participants = participants,
                    call = call,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT),
                )
            }
        }

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
}

enum class CallLayoutType {
    GRID,
    SPEAKER_LAYOUT
}

object MeetingRoomContentTestTags {
    const val MEETING_ROOM_PARTICIPANTS_GRID = "meeting_room_participants_grid"
    const val MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT = "meeting_room_participants_speaker_layout"
}

//@OptIn(ExperimentalMaterial3Api::class)
//@PreviewLightDark
//@Composable
//internal fun MeetingRoomContentPreview() {
//    VonageVideoTheme {
//        val sheetState = rememberModalBottomSheetState()
//        MeetingRoomContent(
//            participants = buildParticipants(5).toImmutableList(),
//            actions = MeetingRoomActions(),
//            recordingState = RecordingState.IDLE,
//            screenSharingState = ScreenSharingState.IDLE,
//            captionsState = CaptionsState.IDLE,
//            participantsSheetState = sheetState,
//            audioDeviceSelectorSheetState = sheetState,
//            moreActionsSheetState = sheetState,
//            showParticipants = false,
//            showAudioDeviceSelector = false,
//            showMoreActions = false,
//            showReporting = false,
//            reportSheetState = sheetState,
//            onDismissParticipants = {},
//            onDismissAudioDeviceSelector = {},
//            onDismissMoreActions = {},
//            onShowReporting = {},
//            onEmojiClick = {},
//            onDismissReporting = {},
//        )
//    }
//}
