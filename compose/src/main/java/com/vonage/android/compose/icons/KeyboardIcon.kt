package com.vonage.android.compose.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun KeyboardIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Default.Keyboard,
        contentDescription = null,
        tint = Color.Gray,
        modifier = modifier.size(24.dp)
    )
}