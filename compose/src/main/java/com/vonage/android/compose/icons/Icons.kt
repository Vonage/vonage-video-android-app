package com.vonage.android.compose.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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

@Composable
fun PersonIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color.Gray,
) {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(size),
    )
}

@Composable
fun VideoCameraIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Default.VideoCall,
        contentDescription = null,
        tint = Color.White,
        modifier = modifier.size(24.dp)
    )
}
