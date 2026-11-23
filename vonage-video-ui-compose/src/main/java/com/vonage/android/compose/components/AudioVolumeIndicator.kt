package com.vonage.android.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun AudioVolumeIndicator(
    audioLevel: Float,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    color: Color = Color.White,
) {
    AudioVolumeIndicator(
        audioLevel = { audioLevel },
        modifier = modifier,
        size = size,
        color = color,
    )
}

/**
 * Reads [audioLevel] only in the draw phase, so level changes redraw the bars without
 * recomposing or remeasuring.
 */
@Composable
fun AudioVolumeIndicator(
    audioLevel: () -> Float,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    color: Color = Color.White,
) {
    Box(
        modifier = modifier
            .background(VonageVideoTheme.colors.primary, CircleShape)
            .size(size)
            .drawWithCache {
                val barWidth = BAR_WIDTH.toPx()
                val barSpacing = BAR_SPACING.toPx()
                val startX = (this.size.width - BAR_FACTORS.size * barWidth - (BAR_FACTORS.size - 1) * barSpacing) / 2
                val cornerRadius = CornerRadius(barWidth / 2)
                onDrawBehind {
                    val level = audioLevel()
                    BAR_FACTORS.forEachIndexed { index, factor ->
                        val barHeight = this.size.height * (level * factor).coerceIn(MIN_BAR_FRACTION, MAX_BAR_FRACTION)
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(startX + index * (barWidth + barSpacing), (this.size.height - barHeight) / 2),
                            size = Size(barWidth, barHeight),
                            cornerRadius = cornerRadius,
                        )
                    }
                }
            },
    )
}

private val BAR_WIDTH = 4.dp
private val BAR_SPACING = 4.dp
private const val SIDE_BAR_FACTOR = 0.6f
private const val CENTER_BAR_FACTOR = 0.85f
private val BAR_FACTORS = floatArrayOf(SIDE_BAR_FACTOR, CENTER_BAR_FACTOR, SIDE_BAR_FACTOR)
private const val MIN_BAR_FRACTION = 0.12f
private const val MAX_BAR_FRACTION = 0.9f

@PreviewLightDark
@Composable
internal fun AudioVolumeIndicatorSilentPreview() {
    VonageVideoTheme {
        AudioVolumeIndicator(audioLevel = 0f)
    }
}

@PreviewLightDark
@Composable
internal fun AudioVolumeIndicatorMediumPreview() {
    VonageVideoTheme {
        AudioVolumeIndicator(audioLevel = 0.5f)
    }
}

@PreviewLightDark
@Composable
internal fun AudioVolumeIndicatorLoudPreview() {
    VonageVideoTheme {
        AudioVolumeIndicator(audioLevel = 1f)
    }
}
