package com.vonage.android.screen.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.vonage.android.BuildConfig
import com.vonage.android.config.AppConfig
import com.vonage.android.meetingroom.api.MeetingRoomBuilder
import com.vonage.android.meetingroom.api.MeetingRoomConfiguration
import com.vonage.android.meetingroom.api.MeetingRoomSDKAction
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.screen.components.permissions.CallPermissionHandler
import com.vonage.android.screen.reporting.ReportIssueScreen
import com.vonage.android.util.navigateToSystemPermissions

/**
 * App-level navigation wrapper for the meeting room.
 *
 * Delegates all meeting room logic to [MeetingRoomBuilder] from the `vonage-meeting-room` module.
 * The app is responsible for wiring navigation callbacks (goodbye screen, settings, share).
 *
 * A custom [permissionContent] is provided to the builder so the app's existing Accompanist-
 * based permission UI is used instead of the SDK default. The SDK-computed `requiredPermissions`
 * list is forwarded to [CallPermissionHandler] so both remain in sync. In the normal flow
 * (navigated from [com.vonage.android.screen.waiting.WaitingRoomRoute]), permissions are already
 * granted and [CallPermissionHandler] calls `onGranted` immediately without displaying any dialog.
 */
@Composable
fun MeetingRoomScreenRoute(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    navigateToSettings: () -> Unit,
    initialPublisherSettings: PublisherSettings = PublisherSettings(),
) {
    val prebuilt = remember(roomName, initialPublisherSettings) {
        MeetingRoomBuilder(
            baseUrl = BuildConfig.BASE_API_URL,
            roomName = roomName,
        )
            .publisherSettings(initialPublisherSettings)
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
            .isDebug(BuildConfig.DEBUG)
            .foregroundServiceEnabled(false)
            .reportingContent { onDismiss -> ReportIssueScreen(onClose = onDismiss) }
            .permissionContent { requiredPermissions, onGranted ->
                // Read context inside the @Composable slot lambda so it always reflects the
                // current Activity after configuration changes, rather than closing over the
                // context captured at remember {} time.
                val context = LocalContext.current
                CallPermissionHandler(
                    permissions = requiredPermissions,
                    onGrantPermissions = onGranted,
                    navigateToPermissions = { context.navigateToSystemPermissions() },
                )
            }
            .build()
    }

    prebuilt.content()
}
