package com.vonage.android.settings.ui.components.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
internal fun StatsRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.tertiary,
        )
        Text(
            text = value,
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.secondary,
        )
    }
}
