package com.vonage.android.screen.room.components.recording

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.vonage.android.R
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.RecordingState
import com.vonage.android.screen.room.components.ExtraAction

@Composable
fun recordingAction(
    actions: MeetingRoomActions,
    recordingState: RecordingState,
): ExtraAction =
    ExtraAction(
        id = 1,
        icon = Icons.Default.Archive,
        label = when (recordingState) {
            RecordingState.IDLE,
            RecordingState.STARTING,
            RecordingState.STOPPING -> stringResource(R.string.recording_start_recording)

            RecordingState.RECORDING -> stringResource(R.string.recording_stop_recording)
        },
        isSelected = when (recordingState) {
            RecordingState.IDLE,
            RecordingState.STOPPING -> false

            RecordingState.STARTING,
            RecordingState.RECORDING -> true
        },
        onClick = remember {
            {
                when (recordingState) {
                    RecordingState.IDLE -> actions.onToggleRecording(true)
                    RecordingState.RECORDING -> actions.onToggleRecording(false)
                    RecordingState.STARTING,
                    RecordingState.STOPPING -> null
                }
            }
        },
    )
