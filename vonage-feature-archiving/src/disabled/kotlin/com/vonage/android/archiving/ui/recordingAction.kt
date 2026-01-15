package com.vonage.android.archiving.ui

import androidx.compose.runtime.Composable
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.compose.components.bottombar.BottomBarAction

@Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
@Composable
fun recordingAction(
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    startRecordingLabel: String,
    stopRecordingLabel: String,
    archivingUiState: ArchivingUiState,
): BottomBarAction? = null
