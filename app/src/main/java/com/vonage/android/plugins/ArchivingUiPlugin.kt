package com.vonage.android.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.archiving.ui.RecordingIndicator
import com.vonage.android.archiving.ui.recordingAction
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.MeetingRoomUiPlugin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * [MeetingRoomUiPlugin] for the archiving/recording feature.
 *
 * Provides:
 * - Top-bar title decoration (recording dot / spinner).
 * - Bottom-bar action to start/stop recording.
 *
 * @param archivingUiState  Live archiving state exposed by the ViewModel.
 */
class ArchivingUiPlugin(
    private val archivingUiState: StateFlow<ArchivingUiState>,
) : MeetingRoomUiPlugin {

    @Composable
    override fun TopBarTitleDecoration() {
        val state by archivingUiState.collectAsStateWithLifecycle()
        when (state) {
            ArchivingUiState.IDLE -> Unit
            ArchivingUiState.STARTING,
            ArchivingUiState.STOPPING -> CircularProgressIndicator(
                color = Color.Red,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
            )
            ArchivingUiState.RECORDING -> RecordingIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
            )
        }
    }

    @Composable
    override fun bottomBarActions(
        actions: MeetingRoomActions,
        isPanelOpen: Boolean,
        onTogglePanel: () -> Unit,
    ): ImmutableList<BottomBarAction> {
        val state by archivingUiState.collectAsStateWithLifecycle()
        return listOfNotNull(
            recordingAction(
                onStartRecording = { actions.onToggleRecording(true) },
                onStopRecording = { actions.onToggleRecording(false) },
                startRecordingLabel = stringResource(R.string.recording_start_recording),
                stopRecordingLabel = stringResource(R.string.recording_stop_recording),
                archivingUiState = state,
            )
        ).toImmutableList()
    }
}
