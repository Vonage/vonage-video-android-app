package com.vonage.android.screen.waiting.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.screen.components.DeviceSelectionPanel
import com.vonage.android.screen.waiting.WaitingRoomActions
import com.vonage.android.screen.waiting.WaitingRoomUiState
import kotlinx.coroutines.flow.flowOf

@Composable
fun WaitingRoomBody(
    uiState: WaitingRoomUiState.Content,
    actions: WaitingRoomActions,
    audioLevel: Float,
    onMicDeviceSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Column(
            modifier = Modifier
                .widthIn(0.dp, 380.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VideoPreviewContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(225.dp),
                view = uiState.view,
                name = uiState.userName,
                isMicEnabled = uiState.isMicEnabled,
                isCameraEnabled = uiState.isCameraEnabled,
                blurLevel = uiState.blurLevel,
                audioLevels = audioLevel,
                actions = actions,
            )
            DeviceSelectionPanel(
                onMicDeviceSelect = onMicDeviceSelect,
                onCameraDeviceSelect = actions.onCameraSwitch,
            )
        }

        JoinRoomSection(
            modifier = Modifier
                .widthIn(0.dp, 320.dp),
            roomName = uiState.roomName,
            username = uiState.userName,
            onUsernameChange = actions.onUserNameChange,
            onJoinRoom = actions.onJoinRoom,
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun WaitingRoomBodyPreview() {
    VonageVideoTheme {
        WaitingRoomBody(
            uiState = WaitingRoomUiState.Content(
                roomName = "test-room-name",
                userName = "User Name",
                isMicEnabled = true,
                isCameraEnabled = false,
                blurLevel = BlurLevel.NONE,
                audioLevel = flowOf(0.5f),
                view = previewCamera(),
            ),
            audioLevel = 0.5f,
            actions = WaitingRoomActions(),
            onMicDeviceSelect = {},
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun WaitingRoomBodyWithVideoPreview() {
    VonageVideoTheme {
        WaitingRoomBody(
            uiState = WaitingRoomUiState.Content(
                roomName = "test-room-name",
                userName = "John Doe",
                isMicEnabled = false,
                isCameraEnabled = true,
                blurLevel = BlurLevel.NONE,
                audioLevel = flowOf(0.5f),
                view = previewCamera(),
            ),
            audioLevel = 0.5f,
            actions = WaitingRoomActions(),
            onMicDeviceSelect = {},
        )
    }
}
