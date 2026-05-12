package com.vonage.android.screen.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vonage.android.config.AppConfig
import com.vonage.android.meetingroom.api.MeetingRoomBuilder
import com.vonage.android.meetingroom.api.MeetingRoomConfiguration
import com.vonage.android.meetingroom.api.MeetingRoomSDKAction
import com.vonage.android.screen.reporting.ReportIssueScreen

/**
 * App-level navigation wrapper for the meeting room.
 *
 * Delegates all meeting room logic to [MeetingRoomBuilder] from the `vonage-meeting-room` module.
 * The app is responsible for wiring navigation callbacks (goodbye screen, settings, share).
 */
@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    navigateToSettings: () -> Unit,
) {
    val prebuilt = remember(roomName) {
        MeetingRoomBuilder(
            baseUrl = com.vonage.android.BuildConfig.BASE_API_URL,
            roomName = roomName,
        )
            .configuration(
                MeetingRoomConfiguration(
                    allowCameraControl = AppConfig.VideoSettings.ALLOW_CAMERA_CONTROL,
                    allowMicrophoneControl = AppConfig.AudioSettings.ALLOW_MICROPHONE_CONTROL,
                    allowShowParticipantList = AppConfig.MeetingRoomSettings.SHOW_PARTICIPANT_LIST,
                )
            )
            .onAction { action ->
                when (action) {
                    is MeetingRoomSDKAction.CallDidEnd -> navigateToGoodBye()
                    is MeetingRoomSDKAction.GoBack -> navigateToGoodBye()
                    is MeetingRoomSDKAction.ShareRoom -> navigateToShare(action.roomName)
                    is MeetingRoomSDKAction.NavigateToSettings -> navigateToSettings()
                }
            }
            .isDebug(com.vonage.android.BuildConfig.DEBUG)
            .reportingContent { onDismiss -> ReportIssueScreen(onClose = onDismiss) }
            .build()
    }

    prebuilt.content()
}

object MeetingRoomScreenTestTags {
    const val MEETING_ROOM_TOP_BAR = "meeting_room_top_bar"
    const val MEETING_ROOM_CONTENT = "meeting_room_content"
    const val MEETING_ROOM_BOTTOM_BAR = "meeting_room_bottom_bar"
}
