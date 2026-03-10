package com.vonage.android.settings.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Apps
import com.vonage.android.compose.vivid.icons.solid.AudioMid
import com.vonage.android.compose.vivid.icons.solid.Video
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoCodec
import com.vonage.android.settings.R
import com.vonage.android.settings.SettingsUiState
import com.vonage.android.settings.ui.components.AudioBitrateSelector
import com.vonage.android.settings.ui.components.DegradationPreferenceSelector
import com.vonage.android.settings.ui.components.FrameRateSelector
import com.vonage.android.settings.ui.components.PreferredCodecOrderSelector
import com.vonage.android.settings.ui.components.ResolutionSelector
import com.vonage.android.settings.ui.components.SectionHeader
import com.vonage.android.settings.ui.components.SettingsToggleRow
import com.vonage.android.settings.ui.components.SettingsTopBar
import com.vonage.android.settings.ui.components.VideoBitrateSelector
import com.vonage.android.settings.ui.components.footer
import com.vonage.android.settings.ui.components.stats.PublisherStats
import com.vonage.android.settings.ui.components.stats.SubscribersStats

private enum class SettingsSection(@StringRes val titleRes: Int) {
    VIDEO(R.string.settings_section_video),
    AUDIO(R.string.settings_section_audio),
    STATS(R.string.settings_section_stats),
}

private val SettingsSection.icon: ImageVector
    get() = when (this) {
        SettingsSection.VIDEO -> VividIcons.Solid.Video
        SettingsSection.AUDIO -> VividIcons.Solid.AudioMid
        SettingsSection.STATS -> VividIcons.Solid.Apps
    }

@Suppress("LongParameterList")
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    onSenderStatsTrackToggle: (Boolean) -> Unit = {},
    onOpusDtxToggle: (Boolean) -> Unit = {},
    onPublisherAudioFallbackToggle: (Boolean) -> Unit = {},
    onSubscriberAudioFallbackToggle: (Boolean) -> Unit = {},
    onVideoBitrateConfigChange: (VideoBitrateConfig) -> Unit = {},
    onDegradationPreferenceChange: (DegradationPreference) -> Unit = {},
    onFrameRateChange: (CaptureFrameRate) -> Unit = {},
    onResolutionChange: (CaptureResolution?) -> Unit = {},
    onPreferredVideoCodecOrderChange: (List<VideoCodec>?) -> Unit = {},
    onAudioBitrateChange: (Int?) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val isWideLayout = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

    Scaffold(
        modifier = modifier,
        topBar = { SettingsTopBar(onDismiss) },
        containerColor = VonageVideoTheme.colors.surface,
    ) { innerPadding ->
        if (isWideLayout) {
            SettingsTabLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                uiState = uiState,
                onFrameRateChange = onFrameRateChange,
                onResolutionChange = onResolutionChange,
                onVideoBitrateConfigChange = onVideoBitrateConfigChange,
                onDegradationPreferenceChange = onDegradationPreferenceChange,
                onPreferredVideoCodecOrderChange = onPreferredVideoCodecOrderChange,
                onOpusDtxToggle = onOpusDtxToggle,
                onAudioBitrateChange = onAudioBitrateChange,
                onPublisherAudioFallbackToggle = onPublisherAudioFallbackToggle,
                onSubscriberAudioFallbackToggle = onSubscriberAudioFallbackToggle,
                onSenderStatsTrackToggle = onSenderStatsTrackToggle,
            )
        } else {
            SettingsListLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                uiState = uiState,
                onFrameRateChange = onFrameRateChange,
                onResolutionChange = onResolutionChange,
                onVideoBitrateConfigChange = onVideoBitrateConfigChange,
                onDegradationPreferenceChange = onDegradationPreferenceChange,
                onPreferredVideoCodecOrderChange = onPreferredVideoCodecOrderChange,
                onOpusDtxToggle = onOpusDtxToggle,
                onAudioBitrateChange = onAudioBitrateChange,
                onPublisherAudioFallbackToggle = onPublisherAudioFallbackToggle,
                onSubscriberAudioFallbackToggle = onSubscriberAudioFallbackToggle,
                onSenderStatsTrackToggle = onSenderStatsTrackToggle,
            )
        }
    }
}

// region Compact list layout (small screens)

@Suppress("LongParameterList")
@Composable
private fun SettingsListLayout(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    onFrameRateChange: (CaptureFrameRate) -> Unit = {},
    onResolutionChange: (CaptureResolution?) -> Unit = {},
    onVideoBitrateConfigChange: (VideoBitrateConfig) -> Unit = {},
    onDegradationPreferenceChange: (DegradationPreference) -> Unit = {},
    onPreferredVideoCodecOrderChange: (List<VideoCodec>?) -> Unit = {},
    onOpusDtxToggle: (Boolean) -> Unit = {},
    onAudioBitrateChange: (Int?) -> Unit = {},
    onPublisherAudioFallbackToggle: (Boolean) -> Unit = {},
    onSubscriberAudioFallbackToggle: (Boolean) -> Unit = {},
    onSenderStatsTrackToggle: (Boolean) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceXSmall),
    ) {
        item { SectionHeader(text = stringResource(R.string.settings_section_video)) }
        videoSectionItems(uiState, onFrameRateChange, onResolutionChange, onVideoBitrateConfigChange, onDegradationPreferenceChange, onPreferredVideoCodecOrderChange)

        item { SectionHeader(text = stringResource(R.string.settings_section_audio)) }
        audioSectionItems(uiState, onAudioBitrateChange, onPublisherAudioFallbackToggle, onSubscriberAudioFallbackToggle, onOpusDtxToggle)

        item { SectionHeader(text = stringResource(R.string.settings_section_stats)) }
        statsSectionItems(uiState, onSenderStatsTrackToggle)

        footer(uiState)
    }
}

// endregion

// region Vertical tabs layout (wide screens)

@Suppress("LongParameterList")
@Composable
private fun SettingsTabLayout(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    onFrameRateChange: (CaptureFrameRate) -> Unit = {},
    onResolutionChange: (CaptureResolution?) -> Unit = {},
    onVideoBitrateConfigChange: (VideoBitrateConfig) -> Unit = {},
    onDegradationPreferenceChange: (DegradationPreference) -> Unit = {},
    onPreferredVideoCodecOrderChange: (List<VideoCodec>?) -> Unit = {},
    onOpusDtxToggle: (Boolean) -> Unit = {},
    onAudioBitrateChange: (Int?) -> Unit = {},
    onPublisherAudioFallbackToggle: (Boolean) -> Unit = {},
    onSubscriberAudioFallbackToggle: (Boolean) -> Unit = {},
    onSenderStatsTrackToggle: (Boolean) -> Unit = {},
) {
    var selectedSection by rememberSaveable { mutableStateOf(SettingsSection.VIDEO) }

    Row(modifier = modifier) {
        SettingsTabRail(
            selectedSection = selectedSection,
            onSectionSelected = { selectedSection = it },
        )

        VerticalDivider(color = VonageVideoTheme.colors.border)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceXSmall),
        ) {
            item { SectionHeader(text = stringResource(selectedSection.titleRes)) }

            when (selectedSection) {
                SettingsSection.VIDEO -> videoSectionItems(
                    uiState, onFrameRateChange, onResolutionChange, onVideoBitrateConfigChange,
                    onDegradationPreferenceChange, onPreferredVideoCodecOrderChange,
                )
                SettingsSection.AUDIO -> audioSectionItems(
                    uiState, onAudioBitrateChange, onPublisherAudioFallbackToggle, onSubscriberAudioFallbackToggle, onOpusDtxToggle,
                )
                SettingsSection.STATS -> statsSectionItems(
                    uiState, onSenderStatsTrackToggle,
                )
            }

            footer(uiState)
        }
    }
}

@Composable
private fun SettingsTabRail(
    selectedSection: SettingsSection,
    onSectionSelected: (SettingsSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .fillMaxHeight()
            .background(VonageVideoTheme.colors.background)
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    ) {
        SettingsSection.entries.forEach { section ->
            SettingsTabItem(
                icon = section.icon,
                label = stringResource(section.titleRes),
                selected = section == selectedSection,
                onClick = { onSectionSelected(section) },
            )
        }
    }
}

@Composable
private fun SettingsTabItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) {
        VonageVideoTheme.colors.surface
    } else {
        VonageVideoTheme.colors.background
    }
    val contentColor = if (selected) {
        VonageVideoTheme.colors.primary
    } else {
        VonageVideoTheme.colors.tertiary
    }

    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingDefault,
                vertical = VonageVideoTheme.dimens.paddingMedium,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
            tint = contentColor,
        )
        Text(
            text = label,
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = contentColor,
        )
    }
}

// endregion

// region Section content

@Suppress("LongParameterList")
private fun LazyListScope.videoSectionItems(
    uiState: SettingsUiState,
    onFrameRateChange: (CaptureFrameRate) -> Unit,
    onResolutionChange: (CaptureResolution?) -> Unit,
    onVideoBitrateConfigChange: (VideoBitrateConfig) -> Unit,
    onDegradationPreferenceChange: (DegradationPreference) -> Unit,
    onPreferredVideoCodecOrderChange: (List<VideoCodec>?) -> Unit,
) {
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
        PreferredCodecOrderSelector(
            selectedOrder = uiState.preferredVideoCodecOrder,
            onOrderChange = onPreferredVideoCodecOrderChange,
        )
    }
}

private fun LazyListScope.audioSectionItems(
    uiState: SettingsUiState,
    onAudioBitrateChange: (Int?) -> Unit,
    onPublisherAudioFallbackToggle: (Boolean) -> Unit,
    onSubscriberAudioFallbackToggle: (Boolean) -> Unit,
    onOpusDtxToggle: (Boolean) -> Unit,
) {
    item {
        SettingsToggleRow(
            title = stringResource(R.string.settings_opus_dtx),
            description = stringResource(R.string.settings_opus_dtx_description),
            isChecked = uiState.opusDtxEnabled,
            onCheckedChange = onOpusDtxToggle,
        )
    }
    item {
        AudioBitrateSelector(
            audioBitrate = uiState.audioBitrate,
            onAudioBitrateChange = onAudioBitrateChange,
        )
    }
    item {
        SettingsToggleRow(
            title = stringResource(R.string.settings_publisher_audio_fallback),
            description = stringResource(R.string.settings_publisher_audio_fallback_description),
            isChecked = uiState.publisherAudioFallbackEnabled,
            onCheckedChange = onPublisherAudioFallbackToggle,
        )
    }
    item {
        SettingsToggleRow(
            title = stringResource(R.string.settings_subscriber_audio_fallback),
            description = stringResource(R.string.settings_subscriber_audio_fallback_description),
            isChecked = uiState.subscriberAudioFallbackEnabled,
            onCheckedChange = onSubscriberAudioFallbackToggle,
        )
    }
}

private fun LazyListScope.statsSectionItems(
    uiState: SettingsUiState,
    onSenderStatsTrackToggle: (Boolean) -> Unit,
) {
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
}

// endregion

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

@PreviewScreenSizes
@Composable
internal fun SettingsScreenAdaptivePreview() {
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
