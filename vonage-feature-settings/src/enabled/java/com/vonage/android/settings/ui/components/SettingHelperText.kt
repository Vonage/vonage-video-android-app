package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
internal fun SettingHelperText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = VonageVideoTheme.typography.captionSemibold,
        color = VonageVideoTheme.colors.textDisabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = VonageVideoTheme.dimens.spaceXSmall),
    )
}

@Composable
@PreviewLightDark
internal fun SettingHelperTextPreview() {
    VonageVideoTheme {
        SettingHelperText(
            text = "Setting helper text sample"
        )
    }
}
