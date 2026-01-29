package com.vonage.android.compose.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.ArrowBoldLeft
import com.vonage.android.compose.vivid.icons.line.CameraSwitch
import com.vonage.android.compose.vivid.icons.line.Plus
import com.vonage.android.compose.vivid.icons.solid.AudioMid
import com.vonage.android.compose.vivid.icons.solid.Share2
import com.vonage.android.compose.vivid.icons.solid.User2

@Composable
fun BackIcon(
    modifier: Modifier = Modifier,
    size: Dp = VonageVideoTheme.dimens.iconSizeDefault,
    tint: Color = VonageVideoTheme.colors.secondary,
) {
    Icon(
        imageVector = VividIcons.Line.ArrowBoldLeft,
        tint = tint,
        contentDescription = null,
        modifier = modifier.size(size),
    )
}

@Composable
fun PersonIcon(
    modifier: Modifier = Modifier,
    size: Dp = VonageVideoTheme.dimens.iconSizeDefault,
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
        modifier = modifier.size(VonageVideoTheme.dimens.iconSizeDefault)
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
        tint = VonageVideoTheme.colors.onSurface,
        modifier = modifier.size(VonageVideoTheme.dimens.iconSizeDefault)
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
        tint = VonageVideoTheme.colors.surface,
        modifier = modifier.size(VonageVideoTheme.dimens.iconSizeDefault)
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
        tint = VonageVideoTheme.colors.onSurface,
        modifier = modifier.size(VonageVideoTheme.dimens.iconSizeDefault)
    )
}
