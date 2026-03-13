package com.vonage.android.screen.room.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.AudioVolumeIndicator
import com.vonage.android.compose.components.AvatarInitials
import com.vonage.android.compose.components.ParticipantVideoRenderer
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.EndCall
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Microphone2
import com.vonage.android.compose.vivid.icons.solid.MoreVertical
import com.vonage.android.compose.vivid.icons.solid.Pin2
import com.vonage.android.compose.vivid.icons.solid.Pin2Off
import com.vonage.android.fx.ui.BlurIndicator
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.PublisherParticipant
import com.vonage.android.kotlin.model.VideoSource
import com.vonage.android.screen.room.MeetingRoomActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantVideoCard(
    participant: Participant,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
    isPinned: Boolean = false,
) {
    val isMicEnabled by participant.isMicEnabled.collectAsStateWithLifecycle()
    val isCameraEnabled by participant.isCameraEnabled.collectAsStateWithLifecycle()
    val isSpeaking by participant.isTalking.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    ParticipantContainer(
        modifier = if (!participant.isPublisher) {
            modifier
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        showBottomSheet = true
                    },
                )
        } else {
            modifier
        },
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

        if (isPinned) {
            PinIndicator(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
            )
        }

        if (participant.isPublisher.not()) {
            IconButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = { showBottomSheet = true }
            ) {
                Icon(
                    imageVector = VividIcons.Solid.MoreVertical,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeSmall),
                )
            }
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

    if (showBottomSheet && !participant.isPublisher) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            ParticipantContextualActions(
                participant = participant,
                actions = actions,
                isPinned = isPinned
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ParticipantContextualActionsPreview() {
    VonageVideoTheme {
        ParticipantContextualActions(
            participant = buildParticipants(1).first(),
            actions = MeetingRoomActions(),
            isPinned = true,
        )
    }
}

@Composable
private fun ParticipantContextualActions(
    participant: Participant,
    actions: MeetingRoomActions,
    isPinned: Boolean
) {
    val isMicEnabled by participant.isMicEnabled.collectAsStateWithLifecycle()
    Column {
        Text(
            modifier = Modifier.padding(
                start = VonageVideoTheme.dimens.paddingSmall,
                bottom = VonageVideoTheme.dimens.paddingSmall,
            ),
            text = participant.name,
            color = VonageVideoTheme.colors.textSecondary,
            style = VonageVideoTheme.typography.heading2,
        )

        HorizontalDivider()

        val pinnedIcon = if (isPinned) {
            VividIcons.Solid.Pin2Off
        } else {
            VividIcons.Solid.Pin2
        }
        val pinnedLabel = if (isPinned) {
            stringResource(R.string.meeting_room_unpin_participant)
        } else {
            stringResource(R.string.meeting_room_pin_participant)
        }
        ActionRow(
            onClick = {
                actions.onTogglePinParticipant(participant.id)
            },
            icon = pinnedIcon,
            label = pinnedLabel,
        )
        if (isMicEnabled) {
            ActionRow(
                onClick = {
                    actions.onForceMuteParticipant(participant.id)
                },
                icon = VividIcons.Solid.MicMute,
                label = "Mute",
            )
        }
        ActionRow(
            onClick = {
                actions.onForceMuteParticipant(participant.id)
            },
            icon = VividIcons.Solid.EndCall,
            label = "Kick",
        )
    }
}

@Composable
private fun ActionRow(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
            )
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingDefault,
                vertical = VonageVideoTheme.dimens.paddingDefault,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VonageVideoTheme.colors.onSurface,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
        )
        Text(
            text = label,
            color = VonageVideoTheme.colors.textSecondary,
            style = VonageVideoTheme.typography.bodyExtended,
        )
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
    val isSpeakingColor = VonageVideoTheme.colors.primary

    val border = remember(isSpeaking, isMicEnabled) {
        when {
            isSpeaking && isMicEnabled -> BorderStroke(borderWidth, isSpeakingColor)
            else -> null
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

@Composable
private fun PinIndicator(
    modifier: Modifier = Modifier,
) {
    val backgroundColor = remember { Color.Black.copy(alpha = 0.6f) }

    Box(
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .padding(6.dp)
    ) {
        Icon(
            imageVector = VividIcons.Solid.Pin2,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeSmall),
        )
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
