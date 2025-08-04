package com.vonage.android.audio.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AudioLevelIndicator(
    level: Float,
    modifier: Modifier = Modifier
) {
    val animatedLevel by animateFloatAsState(
        targetValue = level,
        animationSpec = tween(durationMillis = 100)
    )

    Canvas(
        modifier = modifier
            .size(width = 200.dp, height = 20.dp)
            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
    ) {
        val width = size.width * animatedLevel
        drawRoundRect(
            color = when {
                animatedLevel > 0.8f -> Color.Red
                animatedLevel > 0.5f -> Color.Yellow
                else -> Color.Green
            },
            size = Size(width, size.height),
            cornerRadius = CornerRadius(10.dp.toPx())
        )
    }
}