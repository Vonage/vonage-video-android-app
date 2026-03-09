package com.vonage.android.screen.room.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.components.AudioVolumeIndicator
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.compose.components.ParticipantVideoRenderer
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Microphone2
import com.vonage.android.fx.ui.BlurIndicator
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.PublisherParticipant
import com.vonage.android.kotlin.model.VideoSource
import com.vonage.android.screen.room.MeetingRoomActions

@Composable
fun ParticipantVideoCard(
    participant: Participant,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    val isMicEnabled by participant.isMicEnabled.collectAsStateWithLifecycle()
    val isCameraEnabled by participant.isCameraEnabled.collectAsStateWithLifecycle()
    val isSpeaking by participant.isTalking.collectAsStateWithLifecycle()

    ParticipantContainer(
        modifier = modifier,
        isSpeaking = isSpeaking,
        isMicEnabled = isMicEnabled,
    ) {
        ParticipantVideoContainer(
            participant = participant
        )

        if (participant.name.isNotBlank()) {
            ParticipantLabel(participant.name)
        } else {
            ParticipantLabel(participant.id)
        }

        if (participant.videoSource == VideoSource.CAMERA) {
            MicrophoneIndicator(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                isMicEnabled = isMicEnabled,
                participant = participant,
                isShowVolumeIndicator = participant.isPublisher,
            )
        }

        if (participant.isPublisher && participant.isScreenShare.not()) {
            val blurLevel by (participant as PublisherParticipant).blurLevel.collectAsStateWithLifecycle()

            BlurIndicator(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(VonageVideoTheme.dimens.paddingSmall),
                isCameraEnabled = isCameraEnabled,
                blurLevel = blurLevel,
                onCameraBlur = actions.onCycleCameraBlur,
                size = VonageVideoTheme.dimens.minTouchTarget,
                iconSize = VonageVideoTheme.dimens.iconSizeSmall,
            )
        }
    }
}

@Composable
private fun ParticipantContainer(
    isSpeaking: Boolean,
    isMicEnabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val borderWidth = remember { 1.dp }

    val border = remember(isSpeaking, isMicEnabled) {
        if (isSpeaking && isMicEnabled) {
            BorderStroke(borderWidth, Color.Cyan)
        } else {
            null
        }
    }

    Card(
        modifier = modifier,
        shape = VonageVideoTheme.shapes.medium,
        border = border,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
private fun BoxScope.ParticipantVideoContainer(
    participant: Participant
) {
    val isCameraEnabled by participant.isCameraEnabled.collectAsStateWithLifecycle()

    if (isCameraEnabled) {
        ParticipantVideoRenderer(
            modifier = Modifier
                .fillMaxSize(),
            participant = participant,
        )
    } else {
        AvatarInitials(
            modifier = Modifier
                .align(Alignment.Center),
            userName = participant.name,
        )
    }
}

@Composable
private fun BoxScope.ParticipantLabel(
    name: String,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = remember { Color.Black.copy(alpha = 0.6f) }

    Box(
        modifier = modifier
            .align(Alignment.BottomStart)
            .padding(
                top = VonageVideoTheme.dimens.paddingXSmall,
                bottom = VonageVideoTheme.dimens.paddingXSmall,
                start = VonageVideoTheme.dimens.paddingXSmall,
                end = 48.dp,
            )
            .background(backgroundColor, VonageVideoTheme.shapes.medium)
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingSmall,
                vertical = VonageVideoTheme.dimens.paddingXSmall,
            )
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
private fun MicrophoneIndicator(
    isMicEnabled: Boolean,
    isShowVolumeIndicator: Boolean,
    participant: Participant,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        if (isMicEnabled && isShowVolumeIndicator) {
            val audioLevel by participant.audioLevel.collectAsStateWithLifecycle()
            AudioVolumeIndicator(
                size = 32.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                audioLevel = audioLevel,
            )
        } else {
            MicrophoneIcon(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                isMicEnabled = isMicEnabled,
            )
        }
    }
}

@Composable
private fun MicrophoneIcon(
    isMicEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = remember { Color.Black.copy(alpha = 0.6f) }
    val iconSize = remember { Modifier.size(16.dp) }

    Box(
        modifier = modifier
            .padding(12.dp)
            .background(backgroundColor, CircleShape)
            .padding(6.dp)
    ) {
        if (isMicEnabled) {
            Icon(
                imageVector = VividIcons.Solid.Microphone2,
                contentDescription = null,
                tint = Color.White,
                modifier = iconSize,
            )
        } else {
            Icon(
                imageVector = VividIcons.Solid.MicMute,
                contentDescription = null,
                tint = Color.Red,
                modifier = iconSize,
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun ParticipantVideoCardPreview() {
    VonageVideoTheme {
        ParticipantVideoCard(
            modifier = Modifier.height(300.dp),
            participant = buildParticipants(1).first(),
            actions = MeetingRoomActions(),
        )
    }
}
