package com.vonage.android.settings.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Gear

@Composable
fun SettingsIcon(
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = navigateToSettings,
    ) {
        Icon(
            imageVector = VividIcons.Solid.Gear,
            contentDescription = null,
            tint = VonageVideoTheme.colors.onSurface,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault)
        )
    }
}
