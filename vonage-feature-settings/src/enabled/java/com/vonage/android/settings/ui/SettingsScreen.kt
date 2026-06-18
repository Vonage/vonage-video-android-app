package com.vonage.android.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.AudioMid
import com.vonage.android.compose.vivid.icons.solid.Chart
import com.vonage.android.compose.vivid.icons.solid.Video
import com.vonage.android.settings.R
import com.vonage.android.settings.Setting
import com.vonage.android.settings.SettingsScreenActions
import com.vonage.android.settings.SettingsSection
import com.vonage.android.settings.SettingsUiState
import com.vonage.android.settings.settings
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

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    modifier: Modifier = Modifier,
) {
    val isWideLayout = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

    Scaffold(
        modifier = modifier,
        topBar = { SettingsTopBar(actions.onDismiss) },
        containerColor = VonageVideoTheme.colors.surface,
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        if (isWideLayout) {
            SettingsTabLayout(
                modifier = contentModifier,
                uiState = uiState,
                actions = actions,
            )
        } else {
            SettingsListLayout(
                modifier = contentModifier,
                uiState = uiState,
                actions = actions,
            )
        }
    }
}

// region Compact list layout (small screens)

@Composable
private fun SettingsListLayout(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceXSmall),
    ) {
        SettingsSection.entries.forEach { section ->
            item { SectionHeader(
                text = stringResource(section.titleRes),
                icon = section.icon,
            ) }
            settingItems(section, uiState, actions)
        }
        footer(uiState)
    }
}

// endregion

// region Vertical tabs layout (wide screens)

@Composable
private fun SettingsTabLayout(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    modifier: Modifier = Modifier,
) {
    var selectedSection by rememberSaveable { mutableStateOf(SettingsSection.VIDEO) }

    Row(modifier = modifier) {
        SettingsTabRail(
            selectedSection = selectedSection,
            onSelectSection = { selectedSection = it },
        )

        VerticalDivider(color = VonageVideoTheme.colors.border)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceXSmall),
        ) {
            item { SectionHeader(text = stringResource(selectedSection.titleRes),) }
            settingItems(selectedSection, uiState, actions)
            footer(uiState)
        }
    }
}

@Composable
private fun SettingsTabRail(
    selectedSection: SettingsSection,
    onSelectSection: (SettingsSection) -> Unit,
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
                onClick = { onSelectSection(section) },
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

private fun LazyListScope.settingItems(
    section: SettingsSection,
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    section.settings.forEach { setting ->
        settingItem(setting, uiState, actions)
    }
    if (section == SettingsSection.STATS) {
        uiState.call?.let { call ->
            item { PublisherStats(call) }
            item { SubscribersStats(call) }
        }
    }
}

private fun LazyListScope.settingItem(
    setting: Setting,
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    when (setting) {
        Setting.FRAME_RATE -> frameRateItem(uiState, actions)
        Setting.RESOLUTION -> resolutionItem(uiState, actions)
        Setting.VIDEO_BITRATE -> videoBitrateItem(uiState, actions)
        Setting.DEGRADATION_PREFERENCE -> degradationPreferenceItem(uiState, actions)
        Setting.PREFERRED_CODEC_ORDER -> codecOrderItem(uiState, actions)
        Setting.OPUS_DTX -> opusDtxItem(uiState, actions)
        Setting.AUDIO_BITRATE -> audioBitrateItem(uiState, actions)
        Setting.PUBLISHER_AUDIO_FALLBACK -> publisherAudioFallbackItem(uiState, actions)
        Setting.SUBSCRIBER_AUDIO_FALLBACK -> subscriberAudioFallbackItem(uiState, actions)
        Setting.SENDER_STATS -> senderStatsItem(uiState, actions)
    }
}

@Composable
private fun nextCallNotice(isCallActive: Boolean): String? =
    if (isCallActive) stringResource(R.string.settings_next_call_notice) else null

private fun LazyListScope.frameRateItem(uiState: SettingsUiState, actions: SettingsScreenActions) {
    item {
        val isCallActive = uiState.call != null
        FrameRateSelector(
            selected = uiState.captureFrameRate,
            onSelectionChange = actions.onFrameRateChange,
            helperText = nextCallNotice(isCallActive),
        )
    }
}

private fun LazyListScope.resolutionItem(uiState: SettingsUiState, actions: SettingsScreenActions) {
    item {
        val isCallActive = uiState.call != null
        ResolutionSelector(
            selected = uiState.captureResolution,
            onSelectionChange = actions.onResolutionChange,
            helperText = nextCallNotice(isCallActive),
        )
    }
}

private fun LazyListScope.videoBitrateItem(uiState: SettingsUiState, actions: SettingsScreenActions) {
    item {
        VideoBitrateSelector(
            config = uiState.videoBitrateConfig,
            onConfigChange = actions.onVideoBitrateConfigChange,
        )
    }
}

private fun LazyListScope.degradationPreferenceItem(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    item {
        DegradationPreferenceSelector(
            selected = uiState.degradationPreference,
            onSelectionChange = actions.onDegradationPreferenceChange,
        )
    }
}

private fun LazyListScope.codecOrderItem(uiState: SettingsUiState, actions: SettingsScreenActions) {
    item {
        val isCallActive = uiState.call != null
        PreferredCodecOrderSelector(
            selectedOrder = uiState.preferredVideoCodecOrder,
            onOrderChange = actions.onPreferredVideoCodecOrderChange,
            helperText = nextCallNotice(isCallActive),
        )
    }
}

private fun LazyListScope.opusDtxItem(uiState: SettingsUiState, actions: SettingsScreenActions) {
    item {
        val isCallActive = uiState.call != null
        SettingsToggleRow(
            title = stringResource(R.string.settings_opus_dtx),
            description = stringResource(R.string.settings_opus_dtx_description),
            isChecked = uiState.opusDtxEnabled,
            onCheckedChange = actions.onOpusDtxToggle,
            helperText = nextCallNotice(isCallActive),
        )
    }
}

private fun LazyListScope.audioBitrateItem(uiState: SettingsUiState, actions: SettingsScreenActions) {
    item {
        val isCallActive = uiState.call != null
        AudioBitrateSelector(
            audioBitrate = uiState.audioBitrate,
            onAudioBitrateChange = actions.onAudioBitrateChange,
            helperText = nextCallNotice(isCallActive),
        )
    }
}

private fun LazyListScope.publisherAudioFallbackItem(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    item {
        val isCallActive = uiState.call != null
        SettingsToggleRow(
            title = stringResource(R.string.settings_publisher_audio_fallback),
            description = stringResource(R.string.settings_publisher_audio_fallback_description),
            isChecked = uiState.publisherAudioFallbackEnabled,
            onCheckedChange = actions.onPublisherAudioFallbackToggle,
            helperText = nextCallNotice(isCallActive),
        )
    }
}

private fun LazyListScope.subscriberAudioFallbackItem(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
) {
    item {
        val isCallActive = uiState.call != null
        SettingsToggleRow(
            title = stringResource(R.string.settings_subscriber_audio_fallback),
            description = stringResource(R.string.settings_subscriber_audio_fallback_description),
            isChecked = uiState.subscriberAudioFallbackEnabled,
            onCheckedChange = actions.onSubscriberAudioFallbackToggle,
            helperText = nextCallNotice(isCallActive),
        )
    }
}

private fun LazyListScope.senderStatsItem(uiState: SettingsUiState, actions: SettingsScreenActions) {
    item {
        val isCallActive = uiState.call != null
        SettingsToggleRow(
            title = stringResource(R.string.settings_sender_stats_title),
            description = stringResource(R.string.settings_sender_stats_description),
            isChecked = uiState.senderStatsEnabled,
            onCheckedChange = actions.onSenderStatsTrackToggle,
            helperText = nextCallNotice(isCallActive),
        )
    }
}

// endregion

private val SettingsSection.icon: ImageVector
    get() = when (this) {
        SettingsSection.VIDEO -> VividIcons.Solid.Video
        SettingsSection.AUDIO -> VividIcons.Solid.AudioMid
        SettingsSection.STATS -> VividIcons.Solid.Chart
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
            actions = SettingsScreenActions(),
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
            actions = SettingsScreenActions(),
        )
    }
}
