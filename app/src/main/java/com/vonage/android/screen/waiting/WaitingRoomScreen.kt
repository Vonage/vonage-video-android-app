package com.vonage.android.screen.waiting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.vonage.android.audio.AudioDevicesHandler
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.waiting.components.WaitingRoomBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingRoomScreen(
    uiState: WaitingRoomUiState,
    actions: WaitingRoomActions,
    audioLevel: Float,
    modifier: Modifier = Modifier,
    navigateToRoom: (String) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState()
    var showAudioDeviceSelector by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = { TopBanner(onBack = actions.onBack) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VonageVideoTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            if (showAudioDeviceSelector) {
                ModalBottomSheet(
                    onDismissRequest = { showAudioDeviceSelector = false },
                    sheetState = sheetState,
                ) {
                    AudioDevicesHandler { showAudioDeviceSelector = false }
                }
            }

            when (uiState) {
                is WaitingRoomUiState.Content -> {
                    WaitingRoomBody(
                        uiState = uiState,
                        actions = actions,
                        audioLevel = audioLevel,
                        onMicDeviceSelect = {
                            showAudioDeviceSelector = true
                            actions.onAudioSwitch()
                        }
                    )
                }

                is WaitingRoomUiState.Idle -> {
                    CircularProgressIndicator(
                        modifier = Modifier.testTag("initializing_indicator")
                    )
                }

                is WaitingRoomUiState.Success -> {
                    navigateToRoom(uiState.roomName)
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun WaitingRoomScreenPreview() {
    VonageVideoTheme {
        WaitingRoomScreen(
            uiState = WaitingRoomUiState.Content(
                roomName = "test-room-name",
                userName = "User Name",
                isMicEnabled = true,
                isCameraEnabled = false,
                blurLevel = BlurLevel.NONE,
                view = previewCamera(),
            ),
            audioLevel = 0.6f,
            actions = WaitingRoomActions(),
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun WaitingRoomScreenWithVideoPreview() {
    VonageVideoTheme {
        WaitingRoomScreen(
            uiState = WaitingRoomUiState.Content(
                roomName = "test-room-name",
                userName = "John Doe",
                isMicEnabled = false,
                isCameraEnabled = true,
                blurLevel = BlurLevel.NONE,
                view = previewCamera(),
            ),
            audioLevel = 0.6f,
            actions = WaitingRoomActions(),
        )
    }
}
