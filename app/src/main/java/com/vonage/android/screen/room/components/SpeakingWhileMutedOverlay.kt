package com.vonage.android.screen.room.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.kotlin.model.PublisherState
import kotlinx.coroutines.flow.MutableStateFlow

private const val OVERLAY_ZINDEX = 11F

@Composable
fun SpeakingWhileMutedOverlay(
    publisher: PublisherState?,
    modifier: Modifier = Modifier,
) {
    val isSpeakingWhileMuted by (publisher?.isSpeakingWhileMuted
        ?: MutableStateFlow(false))
        .collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .zIndex(OVERLAY_ZINDEX)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = isSpeakingWhileMuted,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        shape = VonageVideoTheme.shapes.medium,
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = VividIcons.Solid.MicMute,
                    contentDescription = null,
                    tint = VonageVideoTheme.colors.error,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 6.dp),
                )
                Text(
                    text = "You are muted. Unmute to speak.",
                    color = Color.White,
                    fontSize = 14.sp,
                )
            }
        }
    }
}
