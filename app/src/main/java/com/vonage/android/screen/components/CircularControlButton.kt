package com.vonage.android.screen.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun CircularControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .border(BorderStroke(1.dp, Color.White), CircleShape)
            .then(modifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Unify with CircularControlButton???
@Composable
fun ControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isActive) Color.White else Color.Gray

    Box(
        modifier = modifier
            .size(48.dp)
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
            modifier = Modifier.size(24.dp),
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun CircularControlButtonPreview() {
    VonageVideoTheme {
        CircularControlButton(
            icon = Icons.Default.Mic,
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun CircularControlButtonDisabledPreview() {
    VonageVideoTheme {
        CircularControlButton(
            icon = Icons.Default.Mic,
            onClick = {},
        )
    }
}
