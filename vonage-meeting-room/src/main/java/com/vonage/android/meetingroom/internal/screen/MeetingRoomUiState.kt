package com.vonage.android.meetingroom.internal.screen

import androidx.compose.runtime.Immutable
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherParticipant
import com.vonage.android.meetingroom.api.MeetingRoomFeature
import com.vonage.android.meetingroom.internal.screen.audio.AudioDevicesState
import com.vonage.android.screensharing.ScreenSharingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class MeetingRoomUiState(
    val roomName: String,
    val archivingUiState: ArchivingUiState = ArchivingUiState.IDLE,
    val captionsUiState: CaptionsUiState = CaptionsUiState.IDLE,
    val screenSharingState: ScreenSharingState = ScreenSharingState.IDLE,
    val audioDevicesState: AudioDevicesState? = null,
    val call: CallFacade? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isEndCall: Boolean = false,
    val layoutType: CallLayoutType = CallLayoutType.GRID,
    val allowMicrophoneControl: Boolean = true,
    val allowCameraControl: Boolean = true,
    val allowShowParticipantList: Boolean = true,
    val backgrounds: ImmutableList<VideoBackgroundItem> = persistentListOf(),
    /** Runtime feature set — applied on top of compile-time flavor toggles. */
    val enabledFeatures: Set<MeetingRoomFeature> = MeetingRoomFeature.all,
    /**
     * Non-null while the effects-selection sheet is open. This publisher is isolated from
     * the live session — effects applied to it are not transmitted to other participants.
     * Set to null when the sheet is dismissed (Cancel) or confirmed (Apply).
     */
    val effectsPreviewPublisher: PublisherParticipant? = null,
)

internal enum class CallLayoutType {
    GRID,
    SPEAKER_LAYOUT,
    ADAPTIVE_GRID,
}
