package com.vonage.android.compose.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme

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

@Composable
fun ShareIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.Default.Share,
        contentDescription = contentDescription,
        tint = VonageVideoTheme.colors.inverseSurface,
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun AudioSelectorIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.AutoMirrored.Default.VolumeUp,
        contentDescription = contentDescription,
        tint = VonageVideoTheme.colors.inverseSurface,
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun CameraSwitchIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.Default.Cameraswitch,
        contentDescription = contentDescription,
        tint = VonageVideoTheme.colors.inverseSurface,
        modifier = modifier.size(24.dp)
    )
}
