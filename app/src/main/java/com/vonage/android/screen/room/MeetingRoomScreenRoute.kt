package com.vonage.android.screen.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.vonage.android.BuildConfig
import com.vonage.android.config.Config
import com.vonage.android.meetingroom.api.MeetingRoomAuthTokenProvider
import com.vonage.android.meetingroom.api.MeetingRoomBuilder
import com.vonage.android.meetingroom.api.MeetingRoomConfiguration
import com.vonage.android.meetingroom.api.MeetingRoomFeature
import com.vonage.android.meetingroom.api.MeetingRoomLayoutMode
import com.vonage.android.meetingroom.api.MeetingRoomSDKAction
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.screen.components.audio.TestSpeaker
import com.vonage.android.screen.components.permissions.CallPermissionHandler
import com.vonage.android.screen.reporting.ReportIssueScreen
import com.vonage.android.settings.CallSettingsHolder
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
    callSettingsHolder: CallSettingsHolder,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    navigateToSettings: () -> Unit,
    initialPublisherSettings: PublisherSettings = PublisherSettings(),
    authTokenProvider: MeetingRoomAuthTokenProvider? = null,
) {
    val config = remember { Config.fromAppConfig() }
    val prebuilt = remember(roomName, initialPublisherSettings, config) {
        MeetingRoomBuilder(
            baseUrl = BuildConfig.BASE_API_URL,
            roomName = roomName,
        )
            .enabledFeatures(configuredMeetingRoomFeatures(config))
            .publisherSettings(initialPublisherSettings)
            .callSettingsHolder(callSettingsHolder)
            .authTokenProvider(authTokenProvider)
            .configuration(
                MeetingRoomConfiguration(
                    allowCameraControl = config.allowCameraControl,
                    allowMicrophoneControl = config.allowMicrophoneControl,
                    allowShowParticipantList = config.allowShowParticipantList,
                    allowDeviceSelection = config.allowMeetingRoomDeviceSelection,
                    allowPictureInPicture = config.allowPictureInPicture,
                    defaultLayoutMode = MeetingRoomLayoutMode.fromConfigValue(
                        config.defaultLayoutMode,
                    ),
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
            .apply {
                if (config.allowFeedback) {
                    reportingContent { onDismiss -> ReportIssueScreen(onClose = onDismiss) }
                }
                if (config.allowAudioDiagnostics) {
                    testSpeakerContent { TestSpeaker() }
                }
            }
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

/**
 * Maps [Config] toggles to the runtime [MeetingRoomFeature] set. This is layered on top of
 * the compile-time Gradle flavors — a feature is only active when both are enabled.
 */
private fun configuredMeetingRoomFeatures(config: Config): Set<MeetingRoomFeature> = buildSet {
    if (config.allowChat) add(MeetingRoomFeature.CHAT)
    if (config.allowArchiving) add(MeetingRoomFeature.ARCHIVING)
    if (config.allowCaptions) add(MeetingRoomFeature.CAPTIONS)
    if (config.allowEmojis) add(MeetingRoomFeature.REACTIONS)
    if (config.allowScreenShare) add(MeetingRoomFeature.SCREEN_SHARE)
    if (config.allowBackgroundEffects) add(MeetingRoomFeature.BACKGROUND_EFFECTS)
    if (config.allowAdvancedNoiseSuppression) add(MeetingRoomFeature.AUDIO_EFFECTS)
    if (config.allowMeetingRoomSettings) add(MeetingRoomFeature.SETTINGS)
}
