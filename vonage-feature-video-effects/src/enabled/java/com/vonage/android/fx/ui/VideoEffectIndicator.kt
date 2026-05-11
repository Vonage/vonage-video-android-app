package com.vonage.android.fx.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.CircularControlButton
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Blur as LineBlur
import com.vonage.android.compose.vivid.icons.line.BlurOff
import com.vonage.android.compose.vivid.icons.line.Image
import com.vonage.android.compose.vivid.icons.solid.Blur as SolidBlur
import com.vonage.android.kotlin.model.VideoEffect

@Composable
fun VideoEffectIndicator(
    videoEffect: VideoEffect,
    onCameraBlur: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    iconSize: Dp = 24.dp,
) {
    CircularControlButton(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.White), CircleShape),
        onClick = onCameraBlur,
        icon = rememberEffectIcon(videoEffect),
        size = size,
        iconSize = iconSize,
    )
}

@Composable
private fun rememberEffectIcon(effect: VideoEffect): ImageVector = remember(effect) {
    when (effect) {
        VideoEffect.None -> VividIcons.Line.BlurOff
        VideoEffect.BlurLow -> VividIcons.Line.LineBlur
        VideoEffect.BlurHigh -> VividIcons.Solid.SolidBlur
        is VideoEffect.BackgroundImage -> VividIcons.Line.Image
    }
}
