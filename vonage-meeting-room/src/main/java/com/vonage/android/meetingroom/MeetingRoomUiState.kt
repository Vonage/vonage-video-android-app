package com.vonage.android.meetingroom

import androidx.compose.runtime.Immutable
import com.vonage.android.compose.preview.noOpCall
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.audioselector.AudioDevicesState

/**
 * Core UI state for the meeting room screen.
 *
 * Feature-specific states (archiving, captions, screensharing, etc.) have been
 * removed. Each feature contributes its own UI state through [MeetingRoomUiPlugin]
 * implementations, keeping this class focused on the fundamental call lifecycle.
 */
@Immutable
data class MeetingRoomUiState(
    val roomName: String,
    val call: CallFacade = noOpCall,
    val audioDevicesState: AudioDevicesState? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isEndCall: Boolean = false,
    val layoutType: CallLayoutType = CallLayoutType.GRID,
    val allowMicrophoneControl: Boolean = true,
    val allowCameraControl: Boolean = true,
    val allowShowParticipantList: Boolean = true,
)
