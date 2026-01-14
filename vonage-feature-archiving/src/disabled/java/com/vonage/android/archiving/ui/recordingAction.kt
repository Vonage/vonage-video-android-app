package com.vonage.android.archiving.ui

import androidx.compose.runtime.Composable
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Error

@Suppress("UnusedParameter", "EmptyFunctionBlock")
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
        icon = VividIcons.Solid.Error,
        label = "Disabled",
        isSelected = false,
        onClick = {},
    )
