package com.vonage.android.compose.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.solid.AbcKeyboard
import com.vonage.android.compose.vivid.icons.line.CameraSwitch
import com.vonage.android.compose.vivid.icons.line.Plus
import com.vonage.android.compose.vivid.icons.solid.Share2
import com.vonage.android.compose.vivid.icons.solid.User2
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.AudioMid

@Composable
fun KeyboardIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = VividIcons.Solid.AbcKeyboard,
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
        imageVector = VividIcons.Solid.User2,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(size),
    )
}

@Composable
fun PlusIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = VividIcons.Line.Plus,
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
        imageVector = VividIcons.Solid.Share2,
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
        imageVector = VividIcons.Solid.AudioMid,
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
        imageVector = VividIcons.Line.CameraSwitch,
        contentDescription = contentDescription,
        tint = VonageVideoTheme.colors.inverseSurface,
        modifier = modifier.size(24.dp)
    )
}
