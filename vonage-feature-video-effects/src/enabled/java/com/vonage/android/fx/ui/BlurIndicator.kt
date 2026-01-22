package com.vonage.android.fx.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
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
import com.vonage.android.compose.vivid.icons.line.Blur
import com.vonage.android.compose.vivid.icons.line.BlurOff
import com.vonage.android.compose.vivid.icons.solid.Blur
import com.vonage.android.kotlin.model.BlurLevel

@Composable
fun BlurIndicator(
    isCameraEnabled: Boolean,
    blurLevel: BlurLevel,
    onCameraBlur: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    iconSize: Dp = 24.dp,
) {
    if (isCameraEnabled) {
        CircularControlButton(
            modifier = modifier
                .border(BorderStroke(1.dp, Color.White), CircleShape),
            onClick = onCameraBlur,
            icon = rememberBlurIcon(blurLevel),
            size = size,
            iconSize = iconSize,
        )
    } else {
        Spacer(modifier = modifier.size(size))
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
