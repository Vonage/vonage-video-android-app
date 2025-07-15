package com.vonage.android.screen.room.components

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.compose.components.VideoRenderer
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.AvatarInitials
import com.vonage.android.screen.waiting.previewCamera

@Composable
fun ParticipantVideoCard(
    isCameraEnabled: Boolean,
    isMicEnabled: Boolean,
    name: String,
    view: View,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(16f / 9f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isCameraEnabled) {
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
                        .align(Alignment.Center),
                    userName = name,
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            if (!isMicEnabled) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        )
                        .padding(6.dp)
                ) {
                    Icon(
                        Icons.Default.MicOff,
                        contentDescription = "Muted",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun ParticipantVideoCardPreview() {
    VonageVideoTheme {
        ParticipantVideoCard(
            name = "Sample Name",
            isCameraEnabled = true,
            isMicEnabled = false,
            view = previewCamera(),
        )
    }
}

@PreviewLightDark
@Composable
internal fun ParticipantVideoCardPlaceholderPreview() {
    VonageVideoTheme {
        ParticipantVideoCard(
            name = "Sample Name",
            isCameraEnabled = false,
            isMicEnabled = false,
            view = previewCamera(),
        )
    }
}
