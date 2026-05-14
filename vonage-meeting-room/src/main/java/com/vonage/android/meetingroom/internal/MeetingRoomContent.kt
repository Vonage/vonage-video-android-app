package com.vonage.android.meetingroom.internal

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
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.api.MeetingRoomSDKAction
import com.vonage.android.meetingroom.internal.screen.MeetingRoomActions
import com.vonage.android.meetingroom.internal.screen.MeetingRoomScreen
import com.vonage.android.meetingroom.internal.screen.PipMeetingRoomScreen
import com.vonage.android.meetingroom.internal.util.pip.pipEffect
import com.vonage.android.meetingroom.internal.util.pip.rememberIsInPipMode
import com.vonage.android.meetingroom.internal.viewmodel.MeetingRoomViewModel
import com.vonage.android.meetingroom.internal.viewmodel.MeetingRoomViewModelFactory
import kotlinx.coroutines.launch

/**
 * Internal Composable entry point for the meeting room.
 *
 * Applies the theme from [prebuilt], creates and wires the ViewModel, and delegates
 * navigation to [MeetingRoomPrebuilt.onAction]. This is only ever called from
 * [MeetingRoomPrebuilt.content] or [MeetingRoomActivity].
 *
 * @param prebuilt        The fully configured meeting room prebuilt instance.
 * @param onActivityFinish When non-null, called after [MeetingRoomSDKAction.CallDidEnd] or
 *                         [MeetingRoomSDKAction.GoBack] to allow the host [MeetingRoomActivity]
 *                         to finish itself. Not needed for the embedded composable path.
 */
@Composable
@Suppress("LongMethod")
internal fun MeetingRoomContent(
    prebuilt: MeetingRoomPrebuilt,
    onActivityFinish: (() -> Unit)? = null,
) {
    VonageVideoTheme(
        lightColors = prebuilt.theme.lightColors,
        darkColors = prebuilt.theme.darkColors,
    ) {
        MeetingRoomContentInner(prebuilt = prebuilt, onActivityFinish = onActivityFinish)
    }
}

@Composable
@Suppress("LongMethod")
private fun MeetingRoomContentInner(
    prebuilt: MeetingRoomPrebuilt,
    modifier: Modifier = Modifier,
    onActivityFinish: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    @Suppress("ViewModelInjection")
    val viewModel: MeetingRoomViewModel = viewModel(
        factory = MeetingRoomViewModelFactory(
            applicationContext = context.applicationContext,
            prebuilt = prebuilt,
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
            onApplyVideoEffect = viewModel::applyVideoEffect,
            onEndCall = {
                viewModel.endCall()
                if (!inPipMode) {
                    prebuilt.onAction(MeetingRoomSDKAction.CallDidEnd)
                    onActivityFinish?.invoke()
                }
            },
            onShare = { prebuilt.onAction(MeetingRoomSDKAction.ShareRoom(prebuilt.roomName)) },
            onRetry = { viewModel.setup(context) },
            onBack = {
                viewModel.endCall()
                prebuilt.onAction(MeetingRoomSDKAction.GoBack(prebuilt.roomName))
                onActivityFinish?.invoke()
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
            onSettings = { prebuilt.onAction(MeetingRoomSDKAction.NavigateToSettings) },
            onTogglePinParticipant = viewModel::onTogglePinParticipant,
            onForceMuteParticipant = viewModel::forceMuteParticipant,
            onAddBackground = viewModel::addBackgrounds,
            onDeleteBackground = viewModel::deleteBackground,
        )
    }

    BackHandler {
        viewModel.endCall()
        prebuilt.onAction(MeetingRoomSDKAction.GoBack(prebuilt.roomName))
        onActivityFinish?.invoke()
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
            reportingContent = prebuilt.reportingContent,
        )
    }
}
