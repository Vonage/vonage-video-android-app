package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
internal fun SettingHelperText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = VonageVideoTheme.typography.caption,
        color = VonageVideoTheme.colors.textDisabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = VonageVideoTheme.dimens.spaceXSmall),
    )
}
