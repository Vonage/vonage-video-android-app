package com.vonage.android.settings.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Close
import com.vonage.android.settings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsTopBar(onDismiss: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.settings),
                style = VonageVideoTheme.typography.heading3,
                color = VonageVideoTheme.colors.secondary,
            )
        },
        actions = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = VividIcons.Line.Close,
                    contentDescription = stringResource(R.string.settings_close),
                    tint = VonageVideoTheme.colors.secondary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VonageVideoTheme.colors.surface,
        ),
    )
}
