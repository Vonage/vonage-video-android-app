package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Pin2

@Composable
internal fun PinIndicator(
    modifier: Modifier = Modifier,
) {
    val backgroundColor = remember { Color.Black.copy(alpha = 0.6f) }

    Box(
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .padding(6.dp)
    ) {
        Icon(
            imageVector = VividIcons.Solid.Pin2,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeSmall),
        )
    }
}
