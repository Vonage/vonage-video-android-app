package com.vonage.android.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.settings.R
import com.vonage.android.settings.ui.components.SectionHeader
import com.vonage.android.settings.ui.components.SettingsToggleRow
import com.vonage.android.settings.ui.components.SettingsTopBar
import com.vonage.android.settings.ui.components.footer
import com.vonage.android.settings.ui.components.stats.PublisherStats
import com.vonage.android.settings.ui.components.stats.SubscribersStats

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    onSenderStatsTrackToggle: (Boolean) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = { SettingsTopBar(onDismiss) },
        containerColor = VonageVideoTheme.colors.surface,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceXSmall),
        ) {

            item { SectionHeader(text = stringResource(R.string.settings_stats_title)) }

            item { Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall)) }

            item {
                SettingsToggleRow(
                    title = stringResource(R.string.settings_sender_stats_title),
                    description = stringResource(R.string.settings_sender_stats_description),
                    isChecked = uiState.senderStatsEnabled,
                    onCheckedChange = onSenderStatsTrackToggle,
                )
            }

            uiState.call?.let { call ->
                item { PublisherStats(call) }
                item { SubscribersStats(call) }
            }

            footer(uiState)
        }
    }
}

@PreviewLightDark
@Composable
internal fun SettingsScreenPreview() {
    VonageVideoTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                senderStatsEnabled = true,
                appVersion = "1.0.0",
                sdkVersion = "2.33.0",
            ),
        )
    }
}
