package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
internal fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = VonageVideoTheme.typography.bodyBaseSemibold,
        color = VonageVideoTheme.colors.primary,
        modifier = modifier.padding(
            top = VonageVideoTheme.dimens.paddingSmall,
        ),
    )
}
