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
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.settings.R
import com.vonage.android.settings.SettingsUiState
import com.vonage.android.settings.ui.components.SectionHeader
import com.vonage.android.settings.ui.components.SettingsToggleRow
import com.vonage.android.settings.ui.components.SettingsTopBar
import com.vonage.android.settings.ui.components.DegradationPreferenceSelector
import com.vonage.android.settings.ui.components.FrameRateSelector
import com.vonage.android.settings.ui.components.ResolutionSelector
import com.vonage.android.settings.ui.components.VideoBitrateSelector
import com.vonage.android.settings.ui.components.footer
import com.vonage.android.settings.ui.components.stats.PublisherStats
import com.vonage.android.settings.ui.components.stats.SubscribersStats

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    onSenderStatsTrackToggle: (Boolean) -> Unit = {},
    onOpusDtxToggle: (Boolean) -> Unit = {},
    onVideoBitrateConfigChange: (VideoBitrateConfig) -> Unit = {},
    onDegradationPreferenceChange: (DegradationPreference) -> Unit = {},
    onFrameRateChange: (CaptureFrameRate) -> Unit = {},
    onResolutionChange: (CaptureResolution?) -> Unit = {},
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

            item { SectionHeader(text = "Video") }

            item {
                FrameRateSelector(
                    selected = uiState.captureFrameRate,
                    onSelectionChange = onFrameRateChange,
                )
            }

            item {
                ResolutionSelector(
                    selected = uiState.captureResolution,
                    onSelectionChange = onResolutionChange,
                )
            }

            item {
                VideoBitrateSelector(
                    config = uiState.videoBitrateConfig,
                    onConfigChange = onVideoBitrateConfigChange,
                )
            }

            item {
                DegradationPreferenceSelector(
                    selected = uiState.degradationPreference,
                    onSelectionChange = onDegradationPreferenceChange,
                )
            }

            item {
                SettingsToggleRow(
                    title = "Enable Opus Dtx",
                    description = "Enabling Opus DTX can reduce bandwidth usage in streams that have long periods of silence",
                    isChecked = uiState.opusDtxEnabled,
                    onCheckedChange = onOpusDtxToggle,
                )
            }

            item { SectionHeader(text = "Audio") }

            item {
                SettingsToggleRow(
                    title = "Publisher audio fallback",
                    description = "With the publisher audio-fallback feature enabled, " +
                            "when publisher determines that a stream's quality has degraded significantly, it disables the video " +
                            "in order to preserve audio quality",
                    isChecked = uiState.opusDtxEnabled,
                    onCheckedChange = onOpusDtxToggle,
                )
            }

            item {
                SettingsToggleRow(
                    title = "Subscriber audio fallback",
                    description = "With the subscriber audio-fallback feature enabled, when the SFU determines " +
                            "that a stream's quality has degraded significantly for a specific subscriber, " +
                            "it disables the video in that subscriber in order to preserve audio quality",
                    isChecked = uiState.opusDtxEnabled,
                    onCheckedChange = onOpusDtxToggle,
                )
            }

            item { SectionHeader(text = stringResource(R.string.settings_stats_title)) }

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
