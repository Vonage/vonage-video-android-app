package com.vonage.android.meetingroom.api

import android.app.Activity
import android.media.projection.MediaProjectionManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vonage.android.meetingroom.internal.screen.MeetingRoomActions
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreen
import com.vonage.android.meetingroom.internal.screen.PipMeetingRoomScreen
import com.vonage.android.meetingroom.internal.util.pip.pipEffect
import com.vonage.android.meetingroom.internal.util.pip.rememberIsInPipMode
import com.vonage.android.meetingroom.internal.viewmodel.MeetingRoomViewModel
import com.vonage.android.meetingroom.internal.viewmodel.MeetingRoomViewModelFactory
import kotlinx.coroutines.launch

private typealias ReportingContent = @Composable (() -> Unit) -> Unit

/**
 * Public Composable entry point for the meeting room.
 *
 * Embeds the full meeting room experience inside the caller's Compose hierarchy.
 * The caller is responsible for navigation callbacks ([onCallEnd], [onNavigateToSettings], [onShare]).
 *
 * @param config                Meeting room configuration (baseUrl, roomName, feature flags, etc.)
 * @param onCallEnd             Called when the local participant ends the call.
 * @param modifier              Optional modifier.
 * @param onNavigateToSettings  Optional callback for navigating to a settings screen.
 * @param onShare               Optional callback for sharing the room link; receives the roomName.
 * @param isDebug               Enables verbose HTTP logging when true. Defaults to false.
 * @param reportingContent      Optional composable shown inside the report-issue bottom sheet.
 *                              Receives an `onDismiss` callback; defaults to the built-in
 *                              placeholder (or nothing when the reporting feature is disabled).
 */
@Composable
@Suppress("LongParameterList")
fun MeetingRoomComponent(
    config: MeetingRoomConfig,
    onCallEnd: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit = {},
    onShare: (String) -> Unit = {},
    isDebug: Boolean = false,
    reportingContent: ReportingContent? = null,
) {
    val context = LocalContext.current
    @Suppress("ViewModelInjection")
    val viewModel: MeetingRoomViewModel = viewModel(
        factory = MeetingRoomViewModelFactory(
            roomName = config.roomName,
            applicationContext = context.applicationContext,
            config = config,
            isDebug = isDebug,
        ),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inPipMode = rememberIsInPipMode()
    val pipModifier = pipEffect()
    val scope = rememberCoroutineScope()

    val screenSharePermissionResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { data -> viewModel.startScreenSharing(data) }
            }
        },
    )

    LaunchedEffect(Unit) {
        viewModel.setup(context)
    }

    val actions = remember {
        MeetingRoomActions(
            onToggleMic = viewModel::onToggleMic,
            onToggleCamera = viewModel::onToggleCamera,
            onCameraSwitch = viewModel::onSwitchCamera,
            onCycleCameraBlur = viewModel::onCycleLocalCameraBlur,
            onEndCall = {
                viewModel.endCall()
                if (!inPipMode) {
                    onCallEnd()
                }
            },
            onShare = onShare,
            onRetry = { viewModel.setup(context) },
            onBack = {
                viewModel.endCall()
                onCallEnd()
            },
            onMessageSent = viewModel::sendMessage,
            onListenUnread = viewModel::listenUnread,
            onEmojiSent = viewModel::sendEmoji,
            onToggleRecording = viewModel::archiveCall,
            onToggleCaptions = viewModel::captions,
            onToggleScreenSharing = { enable ->
                if (enable) {
                    scope.launch {
                        val mediaProjectionManager =
                            context.getSystemService(MediaProjectionManager::class.java)
                        screenSharePermissionResult.launch(mediaProjectionManager.createScreenCaptureIntent())
                    }
                } else {
                    viewModel.stopScreenSharing()
                }
            },
            onChangeLayout = viewModel::changeLayout,
            onSettings = onNavigateToSettings,
            onTogglePinParticipant = viewModel::onTogglePinParticipant,
            onForceMuteParticipant = viewModel::forceMuteParticipant,
        )
    }

    BackHandler {
        viewModel.endCall()
        onCallEnd()
    }

    if (inPipMode) {
        PipMeetingRoomScreen(
            modifier = modifier.then(pipModifier),
            actions = actions,
            uiState = uiState,
        )
    } else {
        MeetingRoomScreen(
            modifier = modifier.then(pipModifier),
            actions = actions,
            uiState = uiState,
            reportingContent = reportingContent,
        )
    }
}
