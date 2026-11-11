package com.vonage.android.archiving.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Rec
import com.vonage.android.compose.vivid.icons.solid.VoicemailTranscript

@Composable
fun recordingAction(
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    startRecordingLabel: String,
    stopRecordingLabel: String,
    archivingUiState: ArchivingUiState,
): BottomBarAction? = archiveAction(
    type = BottomBarActionType.RECORD_SESSION,
    icon = VividIcons.Solid.Rec,
    onStart = onStartRecording,
    onStop = onStopRecording,
    startLabel = startRecordingLabel,
    stopLabel = stopRecordingLabel,
    archivingUiState = archivingUiState,
)

/** Start/stop a post-call transcription: an archive that runs alongside any recording. */
@Composable
fun transcriptionAction(
    onStartTranscription: () -> Unit,
    onStopTranscription: () -> Unit,
    startTranscriptionLabel: String,
    stopTranscriptionLabel: String,
    transcriptionUiState: ArchivingUiState,
): BottomBarAction? = archiveAction(
    type = BottomBarActionType.POST_CALL_TRANSCRIPTION,
    icon = VividIcons.Solid.VoicemailTranscript,
    onStart = onStartTranscription,
    onStop = onStopTranscription,
    startLabel = startTranscriptionLabel,
    stopLabel = stopTranscriptionLabel,
    archivingUiState = transcriptionUiState,
)

@Suppress("LongParameterList")
@Composable
private fun archiveAction(
    type: BottomBarActionType,
    icon: ImageVector,
    onStart: () -> Unit,
    onStop: () -> Unit,
    startLabel: String,
    stopLabel: String,
    archivingUiState: ArchivingUiState,
): BottomBarAction =
    BottomBarAction(
        type = type,
        icon = icon,
        label = when (archivingUiState) {
            ArchivingUiState.IDLE,
            ArchivingUiState.STARTING,
            ArchivingUiState.STOPPING -> startLabel

            ArchivingUiState.RECORDING -> stopLabel
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
                    ArchivingUiState.IDLE -> onStart()
                    ArchivingUiState.RECORDING -> onStop()
                    ArchivingUiState.STARTING,
                    ArchivingUiState.STOPPING -> Unit
                }
            }
        },
    )
