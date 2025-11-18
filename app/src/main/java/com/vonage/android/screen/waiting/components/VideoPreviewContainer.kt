package com.vonage.android.screen.waiting.components

import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.audio.ui.AudioVolumeIndicator
import com.vonage.android.compose.components.VideoRenderer
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Microphone2
import com.vonage.android.compose.vivid.icons.solid.VideoOff
import com.vonage.android.compose.vivid.icons.solid.Video
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Blur
import com.vonage.android.compose.vivid.icons.line.BlurOff
import com.vonage.android.compose.vivid.icons.solid.Blur
import com.vonage.android.screen.components.CircularControlButton
import com.vonage.android.screen.waiting.WaitingRoomActions
import com.vonage.android.screen.waiting.WaitingRoomTestTags.CAMERA_BLUR_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.CAMERA_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.MIC_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.USER_INITIALS_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.VOLUME_INDICATOR_TAG
import com.vonage.android.util.buildTestTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("LongParameterList")
@Composable
fun VideoPreviewContainer(
    view: View?,
    name: String,
    audioLevels: StateFlow<Float>,
    actions: WaitingRoomActions,
    blurLevel: BlurLevel,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(Color.DarkGray),
        contentAlignment = Alignment.BottomCenter,
    ) {
        if (isCameraEnabled && view != null) {
            VideoRenderer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clipToBounds(),
                view = view,
            )
        } else {
            AvatarInitials(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag(USER_INITIALS_TAG),
                userName = name,
            )
        }
        VideoControlPanel(
            modifier = Modifier.padding(bottom = 16.dp),
            onMicToggle = actions.onMicToggle,
            onCameraToggle = actions.onCameraToggle,
            onCameraBlur = actions.onCameraBlur,
            isMicEnabled = isMicEnabled,
            blurLevel = blurLevel,
            isCameraEnabled = isCameraEnabled,
            audioLevelState = audioLevels,
        )
    }
}

@Suppress("LongParameterList")
@Composable
fun VideoControlPanel(
    audioLevelState: StateFlow<Float>,
    onMicToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onCameraBlur: () -> Unit,
    blurLevel: BlurLevel,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MicVolumeIndicator(
            isMicEnabled,
            audioLevelState,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularControlButton(
                modifier = Modifier
                    .conditional(
                        isMicEnabled,
                        ifTrue = { background(Color.Unspecified) },
                        ifFalse = { background(Color.Red.copy(alpha = 0.7f)) }
                    )
                    .conditional(
                        isMicEnabled,
                        ifTrue = { border(BorderStroke(1.dp, Color.White), CircleShape) })
                    .testTag(MIC_BUTTON_TAG.buildTestTag(isMicEnabled)),
                onClick = onMicToggle,
                icon = if (isMicEnabled) VividIcons.Solid.Microphone2 else VividIcons.Solid.MicMute,
            )

            CircularControlButton(
                modifier = Modifier
                    .conditional(
                        isCameraEnabled,
                        ifTrue = { background(Color.Unspecified) },
                        ifFalse = { background(Color.Red.copy(alpha = 0.7f)) }
                    )
                    .conditional(
                        isCameraEnabled,
                        ifTrue = { border(BorderStroke(1.dp, Color.White), CircleShape) })
                    .testTag(CAMERA_BUTTON_TAG.buildTestTag(isCameraEnabled)),
                onClick = onCameraToggle,
                icon = if (isCameraEnabled) VividIcons.Solid.Video else VividIcons.Solid.VideoOff,
            )
        }

        CircularControlButton(
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.White), CircleShape)
                .testTag(CAMERA_BLUR_BUTTON_TAG),
            onClick = onCameraBlur,
            icon = rememberBlurIcon(blurLevel),
        )
    }
}

@Composable
private fun MicVolumeIndicator(
    isMicEnabled: Boolean,
    audioLevelState: StateFlow<Float>,
) {
    val audioLevel by audioLevelState.collectAsStateWithLifecycle()
    if (isMicEnabled) {
        AudioVolumeIndicator(
            modifier = Modifier
                .shadow(elevation = 2.dp, shape = CircleShape)
                .testTag(VOLUME_INDICATOR_TAG),
            audioLevel = audioLevel,
        )
    } else {
        Spacer(modifier = Modifier.size(32.dp))
    }
}

@Composable
fun rememberBlurIcon(level: BlurLevel): ImageVector = remember(level) {
    when (level) {
        BlurLevel.HIGH -> VividIcons.Solid.Blur
        BlurLevel.LOW -> VividIcons.Line.Blur
        BlurLevel.NONE -> VividIcons.Line.BlurOff
    }
}

@PreviewLightDark
@Composable
internal fun VideoPreviewContainerPreview() {
    VonageVideoTheme {
        VideoPreviewContainer(
            actions = WaitingRoomActions(),
            view = previewCamera(),
            name = "John Doe",
            blurLevel = BlurLevel.LOW,
            isMicEnabled = true,
            isCameraEnabled = true,
            audioLevels = MutableStateFlow(0.5F),
        )
    }
}
