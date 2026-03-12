package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
internal fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeSmall),
                tint = VonageVideoTheme.colors.primary,
            )
        }
        Text(
            text = text,
            style = VonageVideoTheme.typography.heading3,
            color = VonageVideoTheme.colors.primary,
        )
    }
}
