package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
internal fun SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        color = VonageVideoTheme.colors.border,
        modifier = modifier.padding(
            vertical = VonageVideoTheme.dimens.paddingSmall,
        ),
    )
}
