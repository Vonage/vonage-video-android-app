package com.vonage.android.archiving.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vonage.android.archiving.ArchivingUiState
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
    archivingUiState: ArchivingUiState,
): BottomBarAction =
    BottomBarAction(
        type = BottomBarActionType.RECORD_SESSION,
        icon = VividIcons.Solid.Inbox3,
        label = when (archivingUiState) {
            ArchivingUiState.IDLE,
            ArchivingUiState.STARTING,
            ArchivingUiState.STOPPING -> startRecordingLabel

            ArchivingUiState.RECORDING -> stopRecordingLabel
        },
        isSelected = when (archivingUiState) {
            ArchivingUiState.IDLE,
            ArchivingUiState.STOPPING -> false

            ArchivingUiState.STARTING,
            ArchivingUiState.RECORDING -> true
        },
        onClick = remember(archivingUiState) {
            {
                when (archivingUiState) {
                    ArchivingUiState.IDLE -> onStartRecording()
                    ArchivingUiState.RECORDING -> onStopRecording()
                    ArchivingUiState.STARTING,
                    ArchivingUiState.STOPPING -> Unit
                }
            }
        },
    )
