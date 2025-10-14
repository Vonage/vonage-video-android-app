package com.vonage.android.screen.room.components

import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import com.vonage.android.audio.ui.AudioVolumeIndicator
import com.vonage.android.compose.components.VideoRenderer
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.VideoSource
import com.vonage.android.screen.components.AvatarInitials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("LongParameterList")
@Composable
fun ParticipantVideoCard(
    isCameraEnabled: StateFlow<Boolean>,
    isVolumeIndicatorVisible: Boolean,
    isMicEnabled: StateFlow<Boolean>,
    isSpeaking: StateFlow<Boolean>,
    audioLevel: StateFlow<Float>,
    videoSource: VideoSource,
    name: String,
    view: View,
    modifier: Modifier = Modifier,
) {
    ParticipantContainer(
        modifier = modifier,
        isSpeaking = isSpeaking,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ParticipantVideoContainer(
                isCameraEnabled = isCameraEnabled,
                name = name,
                view = view,
            )

            ParticipantLabel(name)

            if (videoSource == VideoSource.CAMERA) {
                MicrophoneIndicator(
                    audioLevel = audioLevel,
                    isMicEnabled = isMicEnabled,
                    isShowVolumeIndicator = isVolumeIndicatorVisible,
                )
            }
        }
    }
}

@Composable
private fun ParticipantContainer(
    isSpeaking: StateFlow<Boolean>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isSpeaking by isSpeaking.collectAsStateWithLifecycle()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = if (isSpeaking) BorderStroke(1.dp, VonageVideoTheme.colors.primary) else null,
    ) {
        content()
    }
}

@Composable
private fun BoxScope.ParticipantVideoContainer(
    isCameraEnabled: StateFlow<Boolean>,
    name: String,
    view: View,
) {
    val isCameraEnabled by isCameraEnabled.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    key(view.hashCode(), isCameraEnabled) {
        if (isCameraEnabled
            && lifecycle.currentStateAsState().value.isAtLeast(STARTED)
        ) {
            VideoRenderer(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds(),
                view = view,
            )
        } else {
            AvatarInitials(
                modifier = Modifier
                    .align(Alignment.Center),
                userName = name,
            )
        }
    }
}

@Composable
private fun BoxScope.ParticipantLabel(
    name: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .align(Alignment.BottomStart)
            .padding(4.dp)
            .background(
                Color.Black.copy(alpha = 0.6f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
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
private fun BoxScope.MicrophoneIndicator(
    audioLevel: StateFlow<Float>,
    isMicEnabled: StateFlow<Boolean>,
    isShowVolumeIndicator: Boolean,
    modifier: Modifier = Modifier,
) {
    val audioLevel by audioLevel.collectAsStateWithLifecycle()
    val isMicEnabled by isMicEnabled.collectAsStateWithLifecycle()

    if (isMicEnabled && isShowVolumeIndicator) {
        AudioVolumeIndicator(
            size = 32.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            audioLevel = audioLevel,
        )
    } else {
        Box(
            modifier = modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    CircleShape
                )
                .padding(6.dp)
        ) {
            Icon(
                imageVector = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
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
            name = "Sample Name",
            audioLevel = MutableStateFlow(0.4f),
            isCameraEnabled = MutableStateFlow(true),
            isMicEnabled = MutableStateFlow(true),
            isSpeaking = MutableStateFlow(false),
            isVolumeIndicatorVisible = true,
            videoSource = VideoSource.CAMERA,
            view = previewCamera(),
        )
    }
}

@PreviewLightDark
@Composable
internal fun ParticipantVideoCardPlaceholderPreview() {
    VonageVideoTheme {
        ParticipantVideoCard(
            modifier = Modifier.height(300.dp),
            name = "Sample Name Name Name Name Name Name Name Name Name Name",
            audioLevel = MutableStateFlow(0.4f),
            isCameraEnabled = MutableStateFlow(false),
            isMicEnabled = MutableStateFlow(true),
            isSpeaking = MutableStateFlow(false),
            isVolumeIndicatorVisible = false,
            videoSource = VideoSource.SCREEN,
            view = previewCamera(),
        )
    }
}
