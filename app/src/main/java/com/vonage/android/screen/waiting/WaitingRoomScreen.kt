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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.layout.TwoPaneScaffold
import com.vonage.android.compose.preview.buildPublisher
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.fx.VideoEffect
import com.vonage.android.fx.data.BackgroundEffectsRepository
import com.vonage.android.fx.ui.VideoEffectCategory
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
    navigateToRoom: (String) -> Unit = {},
    navigateToSettings: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showAudioDeviceSelector by remember { mutableStateOf(false) }

    var showVideoEffects by remember { mutableStateOf(false) }
    var selectedEffectCategory by remember { mutableStateOf<VideoEffectCategory>(VideoEffectCategory.None) }
    var selectedBackgroundId by remember { mutableStateOf<String?>(null) }
    var selectedBackgroundPath by remember { mutableStateOf<String?>(null) }
    var previousEffect by remember { mutableStateOf<VideoEffect>(VideoEffect.None) }
    val context = LocalContext.current
    val backgroundsRepository = remember { BackgroundEffectsRepository(context) }
    val backgrounds = remember { backgroundsRepository.getBackgrounds() }

    val effectsActions = remember(actions) {
        actions.copy(
            onOpenVideoEffects = {
                previousEffect = when (selectedEffectCategory) {
                    VideoEffectCategory.None -> VideoEffect.None
                    VideoEffectCategory.BlurLow -> VideoEffect.BlurLow
                    VideoEffectCategory.BlurHigh -> VideoEffect.BlurHigh
                    VideoEffectCategory.VirtualBackground -> {
                        selectedBackgroundPath?.let { path ->
                            VideoEffect.BackgroundImage(
                                id = selectedBackgroundId.orEmpty(),
                                imagePath = path,
                            )
                        } ?: VideoEffect.None
                    }
                }
                showVideoEffects = true
            },
        )
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navigateToRoom(uiState.roomName)
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
                    key(showVideoEffects) {
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
        VideoEffectsScreen(
            publisher = uiState.publisher,
            isCameraEnabled = uiState.publisher?.isCameraEnabled?.value ?: false,
            backgrounds = backgrounds,
            selectedCategory = selectedEffectCategory,
            selectedBackgroundId = selectedBackgroundId,
            onDismiss = {
                actions.onApplyVideoEffect(previousEffect)
                selectedEffectCategory = when (previousEffect) {
                    is VideoEffect.None -> VideoEffectCategory.None
                    is VideoEffect.BlurLow -> VideoEffectCategory.BlurLow
                    is VideoEffect.BlurHigh -> VideoEffectCategory.BlurHigh
                    is VideoEffect.BackgroundImage -> VideoEffectCategory.VirtualBackground
                }
                selectedBackgroundId = (previousEffect as? VideoEffect.BackgroundImage)?.id
                selectedBackgroundPath = (previousEffect as? VideoEffect.BackgroundImage)?.imagePath
                showVideoEffects = false
            },
            onApply = { showVideoEffects = false },
            onCategorySelected = { category ->
                selectedEffectCategory = category
                val effect = when (category) {
                    VideoEffectCategory.None -> VideoEffect.None
                    VideoEffectCategory.BlurLow -> VideoEffect.BlurLow
                    VideoEffectCategory.BlurHigh -> VideoEffect.BlurHigh
                    VideoEffectCategory.VirtualBackground -> {
                        val path = selectedBackgroundPath ?: return@VideoEffectsScreen
                        VideoEffect.BackgroundImage(
                            id = selectedBackgroundId.orEmpty(),
                            imagePath = path,
                        )
                    }
                }
                actions.onApplyVideoEffect(effect)
            },
            onBackgroundSelected = { item ->
                selectedBackgroundId = item.id
                selectedBackgroundPath = item.imagePath
                val path = item.imagePath ?: return@VideoEffectsScreen
                actions.onApplyVideoEffect(
                    VideoEffect.BackgroundImage(id = item.id, imagePath = path),
                )
            },
        )
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
