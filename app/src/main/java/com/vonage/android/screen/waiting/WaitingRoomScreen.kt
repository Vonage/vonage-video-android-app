package com.vonage.android.screen.waiting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.vonage.android.audio.ui.AudioDevices
import com.vonage.android.audio.ui.AudioDevicesEffect
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.waiting.components.WaitingRoomBody
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingRoomScreen(
    uiState: WaitingRoomUiState,
    actions: WaitingRoomActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (String) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState()
    var showAudioDeviceSelector by remember { mutableStateOf(false) }

    AudioDevicesEffect()

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
                    AudioDevices(
                        onDismissRequest = { showAudioDeviceSelector = false },
                    )
                }
            }

            when {
                uiState.isSuccess -> {
                    navigateToRoom(uiState.roomName)
                }

                else -> {
                    WaitingRoomBody(
                        uiState = uiState,
                        actions = actions,
                        onMicDeviceSelect = {
                            showAudioDeviceSelector = true
                            actions.onAudioSwitch()
                        }
                    )
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
            uiState = WaitingRoomUiState(
                roomName = "test-room-name",
                userName = "User Name",
                isMicEnabled = true,
                isCameraEnabled = false,
                blurLevel = BlurLevel.NONE,
                view = previewCamera(),
            ),
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
            uiState = WaitingRoomUiState(
                roomName = "test-room-name",
                userName = "John Doe",
                isMicEnabled = false,
                isCameraEnabled = true,
                blurLevel = BlurLevel.NONE,
                view = previewCamera(),
            ),
            actions = WaitingRoomActions(),
        )
    }
}
