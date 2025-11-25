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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.audio.ui.AudioDevices
import com.vonage.android.audio.ui.AudioDevicesEffect
import com.vonage.android.compose.layout.TwoPaneScaffold
import com.vonage.android.compose.preview.buildPublisher
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.waiting.components.DeviceSelectionPanel
import com.vonage.android.screen.waiting.components.JoinRoomSection
import com.vonage.android.screen.waiting.components.VideoPreviewContainer

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

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navigateToRoom(uiState.roomName)
        }
    }

    AudioDevicesEffect()
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

    TwoPaneScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopBanner(onBack = actions.onBack) },
        firstPane = {
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .widthIn(0.dp, 380.dp),
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
                        actions = actions,
                    )
                }
                DeviceSelectionPanel(
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
                onUsernameChange = actions.onUserNameChange,
                onJoinRoom = actions.onJoinRoom,
            )
        }
    )
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
