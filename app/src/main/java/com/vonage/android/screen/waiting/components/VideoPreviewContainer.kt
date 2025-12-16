package com.vonage.android.screen.waiting.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.components.AudioVolumeIndicator
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.compose.components.ParticipantVideoRenderer
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Blur
import com.vonage.android.compose.vivid.icons.line.BlurOff
import com.vonage.android.compose.vivid.icons.solid.Blur
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Microphone2
import com.vonage.android.compose.vivid.icons.solid.Video
import com.vonage.android.compose.vivid.icons.solid.VideoOff
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.PublisherParticipant
import com.vonage.android.screen.components.CircularControlButton
import com.vonage.android.screen.waiting.WaitingRoomActions
import com.vonage.android.screen.waiting.WaitingRoomTestTags.CAMERA_BLUR_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.CAMERA_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.MIC_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.USER_INITIALS_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.VOLUME_INDICATOR_TAG
import com.vonage.android.util.buildTestTag

@Composable
fun VideoPreviewContainer(
    name: String,
    actions: WaitingRoomActions,
    publisher: PublisherParticipant,
    modifier: Modifier = Modifier,
) {
    val isCameraEnabled by publisher.isCameraEnabled.collectAsStateWithLifecycle()

    Card(
        modifier = modifier,
        shape = VonageVideoTheme.shapes.none,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            if (isCameraEnabled) {
                ParticipantVideoRenderer(
                    modifier = Modifier
                        .fillMaxSize(),
                    participant = publisher,
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
                modifier = Modifier.padding(bottom = VonageVideoTheme.dimens.paddingSmall),
                publisher = publisher,
                actions = actions,
            )
        }
    }
}

@Composable
private fun VideoControlPanel(
    publisher: PublisherParticipant,
    actions: WaitingRoomActions,
    modifier: Modifier = Modifier,
) {
    val isCameraEnabled by publisher.isCameraEnabled.collectAsStateWithLifecycle()
    val isMicEnabled by publisher.isMicEnabled.collectAsStateWithLifecycle()
    val audioLevel by publisher.audioLevel.collectAsStateWithLifecycle()
    val blurLevel by publisher.blurLevel.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = VonageVideoTheme.dimens.spaceDefault),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MicVolumeIndicator(
            isMicEnabled = isMicEnabled,
            audioLevel = audioLevel,
        )

        Row(
            horizontalArrangement = spacedBy(VonageVideoTheme.dimens.spaceDefault),
        ) {
            CircularControlButton(
                modifier = Modifier
                    .conditional(
                        isMicEnabled,
                        ifTrue = {
                            background(Color.Unspecified)
                                .border(BorderStroke(1.dp, Color.White), CircleShape)
                        },
                        ifFalse = { background(VonageVideoTheme.colors.error) }
                    )
                    .testTag(MIC_BUTTON_TAG.buildTestTag(isMicEnabled)),
                onClick = actions.onMicToggle,
                icon = if (isMicEnabled) VividIcons.Solid.Microphone2 else VividIcons.Solid.MicMute,
            )

            CircularControlButton(
                modifier = Modifier
                    .conditional(
                        isCameraEnabled,
                        ifTrue = {
                            background(Color.Unspecified)
                                .border(BorderStroke(1.dp, Color.White), CircleShape)
                        },
                        ifFalse = { background(VonageVideoTheme.colors.error) }
                    )
                    .testTag(CAMERA_BUTTON_TAG.buildTestTag(isCameraEnabled)),
                onClick = actions.onCameraToggle,
                icon = if (isCameraEnabled) VividIcons.Solid.Video else VividIcons.Solid.VideoOff,
            )
        }

        BlurIndicator(
            isCameraEnabled = isCameraEnabled,
            blurLevel = blurLevel,
            actions = actions,
        )
    }
}

@Composable
private fun MicVolumeIndicator(
    isMicEnabled: Boolean,
    audioLevel: Float,
) {
    if (isMicEnabled) {
        AudioVolumeIndicator(
            modifier = Modifier
                .shadow(elevation = 2.dp, shape = CircleShape)
                .testTag(VOLUME_INDICATOR_TAG),
            audioLevel = audioLevel,
        )
    } else {
        Spacer(modifier = Modifier.size(VonageVideoTheme.dimens.spaceXLarge))
    }
}

@Composable
private fun BlurIndicator(
    isCameraEnabled: Boolean,
    blurLevel: BlurLevel,
    actions: WaitingRoomActions,
) {
    if (isCameraEnabled) {
        CircularControlButton(
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.White), CircleShape)
                .testTag(CAMERA_BLUR_BUTTON_TAG),
            onClick = actions.onCameraBlur,
            icon = rememberBlurIcon(blurLevel),
        )
    } else {
        Spacer(modifier = Modifier.size(56.dp))
    }
}

@Composable
private fun rememberBlurIcon(level: BlurLevel): ImageVector = remember(level) {
    when (level) {
        BlurLevel.HIGH -> VividIcons.Solid.Blur
        BlurLevel.LOW -> VividIcons.Line.Blur
        BlurLevel.NONE -> VividIcons.Line.BlurOff
    }
}
