package com.vonage.android.screen.waiting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.layout.TwoPaneScaffold
import com.vonage.android.compose.preview.buildPublisher
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.fx.ui.VideoEffectsScreen
import com.vonage.android.screen.components.audio.AudioDevicesMenu
import com.vonage.android.screen.waiting.components.DeviceSelectionPanel
import com.vonage.android.screen.waiting.components.JoinRoomSection
import com.vonage.android.screen.waiting.components.VideoControlPanel
import com.vonage.android.screen.waiting.components.VideoPreviewContainer
import com.vonage.android.screen.waiting.components.WaitingRoomTopBar
import com.vonage.android.util.rememberNoiseSuppression
import kotlinx.coroutines.launch

@Suppress("LongMethod", "CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingRoomScreen(
    uiState: WaitingRoomUiState,
    actions: WaitingRoomActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (String, VideoEffect) -> Unit = { _, _ -> },
    navigateToSettings: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val videoEffectsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAudioDeviceSelector by remember { mutableStateOf(false) }

    var showVideoEffects by remember { mutableStateOf(false) }
    var selectedEffect by remember { mutableStateOf<VideoEffect>(VideoEffect.None) }

    val effectsActions = remember(actions) {
        actions.copy(
            onOpenVideoEffects = { showVideoEffects = true },
        )
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navigateToRoom(uiState.roomName, uiState.joinEffect)
        }
    }

    if (showAudioDeviceSelector) {
        ModalBottomSheet(
            onDismissRequest = { showAudioDeviceSelector = false },
            sheetState = sheetState,
        ) {
            uiState.audioDevicesState?.let { audioDevicesState ->
                val noiseSuppression by rememberNoiseSuppression(uiState.publisher)
                    .collectAsStateWithLifecycle()

                AudioDevicesMenu(
                    audioDevicesState = audioDevicesState,
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                            showAudioDeviceSelector = false
                        }
                    },
                    noiseSuppressionEnabled = noiseSuppression.isEnabled(),
                    onNoiseSuppressorToggle = { _ ->
                        uiState.publisher?.toggleNoiseSuppression()
                    },
                )
            }
        }
    }

    TwoPaneScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            WaitingRoomTopBar(
                actions = actions,
                navigateToSettings = navigateToSettings,
            )
        },
        firstPane = {
            Column(
                modifier = Modifier
                    .padding(vertical = VonageVideoTheme.dimens.paddingDefault)
                    .widthIn(0.dp, MAX_PANE_WIDTH.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
            ) {
                uiState.publisher?.let {
                    VideoPreviewContainer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(245.dp),
                        publisher = uiState.publisher,
                        name = uiState.userName,
                    ) {
                        VideoControlPanel(
                            modifier = Modifier.padding(bottom = VonageVideoTheme.dimens.paddingSmall),
                            publisher = uiState.publisher,
                            allowMicrophoneControl = uiState.allowMicrophoneControl,
                            allowCameraControl = uiState.allowCameraControl,
                            actions = effectsActions,
                        )
                    }
                }
                DeviceSelectionPanel(
                    modifier = Modifier
                        .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
                    onMicDeviceSelect = { showAudioDeviceSelector = true },
                    onCameraDeviceSelect = actions.onCameraSwitch,
                )
            }
        },
        secondPane = {
            JoinRoomSection(
                modifier = Modifier
                    .widthIn(0.dp, MAX_PANE_WIDTH.dp),
                roomName = uiState.roomName,
                username = uiState.userName,
                isUserNameValid = uiState.isUserNameValid,
                onUsernameChange = actions.onUserNameChange,
                onJoinRoom = actions.onJoinRoom,
            )
        }
    )

    if (showVideoEffects) {
        ModalBottomSheet(
            onDismissRequest = { showVideoEffects = false },
            sheetState = videoEffectsSheetState,
        ) {
            VideoEffectsScreen(
                backgrounds = uiState.backgrounds,
                selectedEffect = selectedEffect,
                onEffectSelect = { effect ->
                    selectedEffect = effect
                    actions.onApplyVideoEffect(effect)
                },
            )
        }
    }
}

private const val MAX_PANE_WIDTH = 550

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun WaitingRoomScreenPreview() {
    VonageVideoTheme {
        WaitingRoomScreen(
            uiState = WaitingRoomUiState(
                roomName = "test-room-name",
                userName = "User Name",
                publisher = buildPublisher(),
                isSuccess = false,
            ),
            actions = WaitingRoomActions(),
        )
    }
}
