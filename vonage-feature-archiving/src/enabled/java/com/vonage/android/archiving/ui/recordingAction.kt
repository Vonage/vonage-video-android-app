package com.vonage.android.archiving.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vonage.android.archiving.RecordingState
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Inbox3

@Composable
fun recordingAction(
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    startRecordingLabel: String,
    stopRecordingLabel: String,
    recordingState: RecordingState,
): BottomBarAction =
    BottomBarAction(
        type = BottomBarActionType.RECORD_SESSION,
        icon = VividIcons.Solid.Inbox3,
        label = when (recordingState) {
            RecordingState.IDLE,
            RecordingState.STARTING,
            RecordingState.STOPPING -> startRecordingLabel

            RecordingState.RECORDING -> stopRecordingLabel
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
                    RecordingState.IDLE -> onStartRecording()
                    RecordingState.RECORDING -> onStopRecording()
                    RecordingState.STARTING,
                    RecordingState.STOPPING -> null
                }
            }
        },
    )
