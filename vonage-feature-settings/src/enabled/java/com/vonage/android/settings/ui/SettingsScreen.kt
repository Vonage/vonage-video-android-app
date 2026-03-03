package com.vonage.android.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.settings.R
import com.vonage.android.settings.SubscriberStatsSnapshot
import com.vonage.android.settings.ui.components.SectionHeader
import com.vonage.android.settings.ui.components.SettingsToggleRow
import com.vonage.android.settings.ui.components.SettingsTopBar
import com.vonage.android.settings.ui.components.footer
import com.vonage.android.settings.ui.components.stats.publisherStats
import com.vonage.android.settings.ui.components.stats.subscribersStats

@Stable
data class SettingsUiState(
    val senderStatsEnabled: Boolean = true,
    val appVersion: String = "",
    val sdkVersion: String = "",
    val videoStats: PublisherState.VideoStats? = null,
    val audioStats: PublisherState.AudioStats? = null,
    val subscriberStats: List<SubscriberStatsSnapshot> = emptyList(),
)

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onSenderStatsTrackToggle: (Boolean) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Scaffold(
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

            if (uiState.videoStats != null || uiState.audioStats != null) {
                publisherStats(uiState)
            }

            if (uiState.subscriberStats.isNotEmpty()) {
                subscribersStats(uiState)
            }

            footer(uiState)
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
    VonageVideoTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                senderStatsEnabled = true,
                appVersion = "1.0.0",
                sdkVersion = "2.33.0",
                videoStats = PublisherState.VideoStats(
                    duration = 123.0,
                    videoPacketsSent = 45_000,
                    videoPacketsLost = 12,
                    videoBytesSent = 15_000_000,
                    estimatedBandwidthInBps = 2_500_000,
                    videoLayerStats = arrayOf(
                        PublisherState.VideoLayerStats(
                            height = 720,
                            width = 1280,
                            codec = "VP8",
                            encodedFrameRate = 30.0,
                            qualityLimitationReason = "none",
                            scalabilityMode = null,
                            bitrate = 1_200_000,
                            totalBitrate = 1_500_000,
                        ),
                    ),
                ),
                audioStats = PublisherState.AudioStats(
                    duration = 123.0,
                    audioPacketsSent = 12_000,
                    audioPacketsLost = 2,
                    audioBytesSent = 960_000,
                    estimatedBandwidthInBps = 2_500_000,
                ),
                subscriberStats = listOf(
                    SubscriberStatsSnapshot(
                        name = "Alice",
                        videoStats = ParticipantState.SubscriberVideoStats(
                            videoPacketsReceived = 38_000,
                            videoPacketsLost = 5,
                            videoBytesReceived = 12_000_000,
                            width = 1280,
                            height = 720,
                            codec = "VP8",
                            decodedFrameRate = 29.5,
                            bitrate = 1_100_000,
                            freezeCount = 1,
                            totalFreezesDuration = 200,
                            estimatedBandwidthInBps = 2_400_000,
                        ),
                        audioStats = ParticipantState.SubscriberAudioStats(
                            audioPacketsReceived = 10_500,
                            audioPacketsLost = 1,
                            audioBytesReceived = 840_000,
                            estimatedBandwidthInBps = 2_400_000,
                        ),
                    ),
                    SubscriberStatsSnapshot(
                        name = "Bob",
                        videoStats = ParticipantState.SubscriberVideoStats(
                            videoPacketsReceived = 22_000,
                            videoPacketsLost = 30,
                            videoBytesReceived = 8_500_000,
                            width = 640,
                            height = 480,
                            codec = "H264",
                            decodedFrameRate = 24.0,
                            bitrate = 800_000,
                            freezeCount = 3,
                            totalFreezesDuration = 950,
                            estimatedBandwidthInBps = 1_800_000,
                        ),
                        audioStats = ParticipantState.SubscriberAudioStats(
                            audioPacketsReceived = 9_800,
                            audioPacketsLost = 4,
                            audioBytesReceived = 780_000,
                            estimatedBandwidthInBps = 1_800_000,
                        ),
                    ),
                ),
            ),
        )
    }
}
