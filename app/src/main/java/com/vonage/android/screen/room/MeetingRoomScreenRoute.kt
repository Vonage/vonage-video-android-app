package com.vonage.android.screen.room

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.config.AppConfig
import com.vonage.android.meetingroom.api.MeetingRoomComponent
import com.vonage.android.meetingroom.api.MeetingRoomConfig
import com.vonage.android.screen.reporting.ReportIssueScreen

/**
 * App-level navigation wrapper for the meeting room.
 *
 * Delegates all meeting room logic to [MeetingRoomComponent] from the `vonage-meeting-room` module.
 * The app is responsible for wiring navigation callbacks (goodbye screen, settings, share).
 */
@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val config = MeetingRoomConfig(
        baseUrl = com.vonage.android.BuildConfig.BASE_API_URL,
        roomName = roomName,
        allowCameraControl = AppConfig.VideoSettings.ALLOW_CAMERA_CONTROL,
        allowMicrophoneControl = AppConfig.AudioSettings.ALLOW_MICROPHONE_CONTROL,
        allowShowParticipantList = AppConfig.MeetingRoomSettings.SHOW_PARTICIPANT_LIST,
    )

    MeetingRoomComponent(
        config = config,
        onCallEnd = navigateToGoodBye,
        onNavigateToSettings = navigateToSettings,
        onShare = navigateToShare,
        modifier = modifier,
        isDebug = com.vonage.android.BuildConfig.DEBUG,
        reportingContent = { onDismiss -> ReportIssueScreen(onClose = onDismiss) },
    )
}

object MeetingRoomScreenTestTags {
    const val MEETING_ROOM_TOP_BAR = "meeting_room_top_bar"
    const val MEETING_ROOM_CONTENT = "meeting_room_content"
    const val MEETING_ROOM_BOTTOM_BAR = "meeting_room_bottom_bar"
}
