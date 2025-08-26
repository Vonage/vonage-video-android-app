package com.vonage.android.screen.room.components.recording

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecordingIndicator(
    modifier: Modifier = Modifier,
) {
    Pulsating {
        Icon(
            modifier = modifier
                .padding(end = 4.dp),
            imageVector = Icons.Default.FiberManualRecord,
            tint = Color.Red,
            contentDescription = null,
        )
    }
}

@Composable
fun Pulsating(
    modifier: Modifier = Modifier,
    pulseFraction: Float = 1.2f,
    durationMillis: Int = 1000,
    content: @Composable () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier.scale(scale)) {
        content()
    }
}