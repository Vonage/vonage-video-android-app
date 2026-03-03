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

fun LazyListScope.subscribersStats(uiState: SettingsUiState) {
    uiState.subscriberStats.forEach { sub ->
        if (sub.videoStats != null || sub.audioStats != null) {
            item { SectionDivider() }
            item {
                SectionHeader(
                    text = stringResource(
                        R.string.settings_stats_subscriber_stats_header,
                        sub.name,
                    )
                )
            }

            sub.videoStats?.let { video ->
                item { StatsSubHeader(text = stringResource(R.string.settings_stats_subscriber_video_header)) }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_video_resolution),
                        value = "${video.width}x${video.height} - ${video.codec} - " +
                                "${
                                    String.format(
                                        Locale.getDefault(),
                                        "%.1f",
                                        video.decodedFrameRate
                                    )
                                } fps",
                    )
                }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_video_packets_received),
                        value = video.videoPacketsReceived.toString(),
                    )
                }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_video_packets_lost),
                        value = video.videoPacketsLost.toString(),
                    )
                }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_video_bytes_received),
                        value = video.videoBytesReceived.toLong().formatBytes(),
                    )
                }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_video_bitrate),
                        value = video.bitrate.formatBitrate(),
                    )
                }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_video_est_bandwidth),
                        value = video.estimatedBandwidthInBps.formatBitrate(),
                    )
                }
                if (video.freezeCount > 0) {
                    item {
                        StatsRow(
                            label = stringResource(R.string.settings_stats_subscriber_video_freezes),
                            value = "${video.freezeCount} (${video.totalFreezesDuration}ms)",
                        )
                    }
                }
            }

            sub.audioStats?.let { audio ->
                item {
                    Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))
                }
                item { StatsSubHeader(text = stringResource(R.string.settings_stats_subscriber_audio_header)) }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_audio_packets_received),
                        value = audio.audioPacketsReceived.toString(),
                    )
                }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_audio_packets_lost),
                        value = audio.audioPacketsLost.toString(),
                    )
                }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_audio_bytes_received),
                        value = audio.audioBytesReceived.toLong().formatBytes(),
                    )
                }
                item {
                    StatsRow(
                        label = stringResource(R.string.settings_stats_subscriber_audio_est_bandwidth),
                        value = audio.estimatedBandwidthInBps.formatBitrate(),
                    )
                }
            }
        }
    }
}
