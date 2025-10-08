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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurCircular
import androidx.compose.material.icons.filled.BlurOff
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.audio.ui.AudioVolumeIndicator
import com.vonage.android.compose.components.VideoRenderer
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.screen.components.CircularControlButton
import com.vonage.android.screen.waiting.WaitingRoomActions
import com.vonage.android.screen.waiting.WaitingRoomTestTags.CAMERA_BLUR_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.CAMERA_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.MIC_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.USER_INITIALS_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.VOLUME_INDICATOR_TAG
import com.vonage.android.util.buildTestTag

@Suppress("LongParameterList")
@Composable
fun VideoPreviewContainer(
    view: View?,
    name: String,
    audioLevels: Float,
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
            audioLevel = audioLevels,
        )
    }
}

@Suppress("LongParameterList")
@Composable
fun VideoControlPanel(
    audioLevel: Float,
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
        if (isMicEnabled) {
            AudioVolumeIndicator(
                modifier = Modifier
                    .testTag(VOLUME_INDICATOR_TAG),
                audioLevel = audioLevel,
            )
        } else {
            Spacer(modifier = Modifier.size(32.dp))
        }

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
                icon = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
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
                icon = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
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
fun rememberBlurIcon(level: BlurLevel): ImageVector = remember(level) {
    when (level) {
        BlurLevel.HIGH -> Icons.Default.BlurOn
        BlurLevel.LOW -> Icons.Default.BlurCircular
        BlurLevel.NONE -> Icons.Default.BlurOff
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
            audioLevels = 0.6f,
        )
    }
}
