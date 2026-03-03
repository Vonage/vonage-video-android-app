package com.vonage.android.settings.ui.components.stats

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.settings.R
import com.vonage.android.settings.ui.SettingsUiState
import com.vonage.android.settings.ui.components.SectionDivider
import com.vonage.android.settings.ui.components.SectionHeader
import com.vonage.android.settings.util.formatBitrate
import com.vonage.android.settings.util.formatBytes
import java.util.Locale

internal fun LazyListScope.publisherStats(uiState: SettingsUiState) {
    item { SectionDivider() }
    item { SectionHeader(text = stringResource(R.string.settings_stats_publisher_stats_header)) }

    uiState.videoStats?.let { video ->
        item {
            StatsSubHeader(text = stringResource(R.string.settings_stats_publisher_video_header))
        }
        item {
            StatsRow(
                label = stringResource(R.string.settings_stats_publisher_video_packets_sent),
                value = video.videoPacketsSent.toString(),
            )
        }
        item {
            StatsRow(
                label = stringResource(R.string.settings_stats_publisher_video_packets_lost),
                value = video.videoPacketsLost.toString(),
            )
        }
        item {
            StatsRow(
                label = stringResource(R.string.settings_stats_publisher_video_bytes_sent),
                value = video.videoBytesSent.formatBytes(),
            )
        }
        item {
            StatsRow(
                label = stringResource(R.string.settings_stats_publisher_video_est_bandwidth),
                value = video.estimatedBandwidthInBps.formatBitrate(),
            )
        }
        video.videoLayerStats.forEachIndexed { index, layer ->
            item {
                StatsRow(
                    label = stringResource(R.string.settings_stats_publisher_video_layer, index),
                    value = stringResource(
                        R.string.settings_stats_publisher_video_layer_fps,
                        layer.width,
                        layer.height,
                        layer.codec,
                        String.format(
                            Locale.getDefault(),
                            "%.1f",
                            layer.encodedFrameRate
                        )
                    ),
                )
            }
        }
    }

    uiState.audioStats?.let { audio ->
        item {
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))
        }
        item {
            StatsSubHeader(text = stringResource(R.string.settings_stats_publisher_audio_header))
        }
        item {
            StatsRow(
                label = stringResource(R.string.settings_stats_publisher_audio_packets_sent),
                value = audio.audioPacketsSent.toString(),
            )
        }
        item {
            StatsRow(
                label = stringResource(R.string.settings_stats_publisher_audio_packets_lost),
                value = audio.audioPacketsLost.toString(),
            )
        }
        item {
            StatsRow(
                label = stringResource(R.string.settings_stats_publisher_audio_bytes_sent),
                value = audio.audioBytesSent.formatBytes(),
            )
        }
        item {
            StatsRow(
                label = stringResource(R.string.settings_stats_publisher_audio_est_bandwidth),
                value = audio.estimatedBandwidthInBps.formatBitrate(),
            )
        }
    }
}
