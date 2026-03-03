package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.settings.ui.SettingsUiState

fun LazyListScope.footer(uiState: SettingsUiState) {
    item { Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceDefault)) }
    item { SectionDivider() }
    item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "App version: ${uiState.appVersion}",
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.tertiary,
            )
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))
            Text(
                text = "OpenTok SDK: ${uiState.sdkVersion}",
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.tertiary,
            )
        }
    }
}
