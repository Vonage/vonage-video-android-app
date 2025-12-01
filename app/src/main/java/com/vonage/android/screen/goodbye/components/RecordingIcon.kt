package com.vonage.android.screen.goodbye.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.VideoActive

@Composable
fun RecordingIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier
            .size(VonageVideoTheme.dimens.iconSizeDefault),
        imageVector = VividIcons.Line.VideoActive,
        contentDescription = null,
        tint = VonageVideoTheme.colors.secondary,
    )
}
