package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
internal fun SettingsToggleRow(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = VonageVideoTheme.typography.bodyBaseSemibold,
                color = VonageVideoTheme.colors.secondary,
            )
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = description,
                    style = VonageVideoTheme.typography.caption,
                    color = VonageVideoTheme.colors.tertiary,
                )
            }
        }
        Spacer(modifier = Modifier.width(VonageVideoTheme.dimens.spaceDefault))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VonageVideoTheme.colors.primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = VonageVideoTheme.colors.border,
                uncheckedBorderColor = VonageVideoTheme.colors.border,
            ),
        )
    }
}
