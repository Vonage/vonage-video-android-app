package com.vonage.android.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun ControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val iconColor = remember(isActive) { if (isActive) Color.White else Color.Gray }

    Box(
        modifier = modifier
            .size(VonageVideoTheme.dimens.minTouchTarget)
            .clip(CircleShape)
            .conditional(
                isActive,
                ifTrue = { background(Color.White.copy(alpha = 0.2f), CircleShape) },
                ifFalse = { background(Color.Transparent, CircleShape) },
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
        )
    }
}
