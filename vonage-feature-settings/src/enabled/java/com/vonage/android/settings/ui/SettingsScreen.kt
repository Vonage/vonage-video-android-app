package com.vonage.android.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Close
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.settings.SubscriberStatsSnapshot

@Stable
data class SettingsUiState(
    val senderStatsEnabled: Boolean = true,
    val appVersion: String = "",
    val sdkVersion: String = "",
    val videoStats: PublisherState.VideoStats? = null,
    val audioStats: PublisherState.AudioStats? = null,
    val subscriberStats: List<SubscriberStatsSnapshot> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState ,
    onSenderStatsTrackToggle: (Boolean) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = VonageVideoTheme.typography.heading3,
                        color = VonageVideoTheme.colors.secondary,
                    )
                },
                actions = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = VividIcons.Line.Close,
                            contentDescription = "Close settings",
                            tint = VonageVideoTheme.colors.secondary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VonageVideoTheme.colors.surface,
                ),
            )
        },
        containerColor = VonageVideoTheme.colors.surface,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceXSmall),
        ) {

            item { SectionHeader(text = "Stats") }

            item { Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall)) }

            item {
                SettingsToggleRow(
                    title = "Sender stats",
                    description = "Enable extra stats for subscribers",
                    isChecked = uiState.senderStatsEnabled,
                    onCheckedChange = onSenderStatsTrackToggle,
                )
            }

            // ── Publisher Stats ──────────────────────────
            if (uiState.videoStats != null || uiState.audioStats != null) {
                item { SectionDivider() }
                item { SectionHeader(text = "Publisher Stats") }

                uiState.videoStats?.let { video ->
                    item {
                        StatsSubHeader(text = "Video")
                    }
                    item {
                        StatsRow(label = "Packets sent", value = video.videoPacketsSent.toString())
                    }
                    item {
                        StatsRow(label = "Packets lost", value = video.videoPacketsLost.toString())
                    }
                    item {
                        StatsRow(label = "Bytes sent", value = formatBytes(video.videoBytesSent))
                    }
                    item {
                        StatsRow(
                            label = "Est. bandwidth",
                            value = formatBitrate(video.estimatedBandwidthInBps),
                        )
                    }
                    video.videoLayerStats.forEachIndexed { index, layer ->
                        item {
                            StatsRow(
                                label = "Layer $index",
                                value = "${layer.width}x${layer.height} · ${layer.codec} · ${String.format("%.1f", layer.encodedFrameRate)} fps",
                            )
                        }
                    }
                }

                uiState.audioStats?.let { audio ->
                    item {
                        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))
                    }
                    item {
                        StatsSubHeader(text = "Audio")
                    }
                    item {
                        StatsRow(label = "Packets sent", value = audio.audioPacketsSent.toString())
                    }
                    item {
                        StatsRow(label = "Packets lost", value = audio.audioPacketsLost.toString())
                    }
                    item {
                        StatsRow(label = "Bytes sent", value = formatBytes(audio.audioBytesSent))
                    }
                    item {
                        StatsRow(
                            label = "Est. bandwidth",
                            value = formatBitrate(audio.estimatedBandwidthInBps),
                        )
                    }
                }
            }

            // ── Subscriber Stats ────────────────────────
            if (uiState.subscriberStats.isNotEmpty()) {
                uiState.subscriberStats.forEach { sub ->
                    if (sub.videoStats != null || sub.audioStats != null) {
                        item { SectionDivider() }
                        item { SectionHeader(text = "Subscriber: ${sub.name}") }

                        sub.videoStats?.let { video ->
                            item { StatsSubHeader(text = "Video") }
                            item {
                                StatsRow(
                                    label = "Resolution",
                                    value = "${video.width}x${video.height}",
                                )
                            }
                            item {
                                StatsRow(label = "Codec", value = video.codec)
                            }
                            item {
                                StatsRow(
                                    label = "Frame rate",
                                    value = "${String.format("%.1f", video.decodedFrameRate)} fps",
                                )
                            }
                            item {
                                StatsRow(
                                    label = "Packets received",
                                    value = video.videoPacketsReceived.toString(),
                                )
                            }
                            item {
                                StatsRow(
                                    label = "Packets lost",
                                    value = video.videoPacketsLost.toString(),
                                )
                            }
                            item {
                                StatsRow(
                                    label = "Bytes received",
                                    value = formatBytes(video.videoBytesReceived.toLong()),
                                )
                            }
                            item {
                                StatsRow(
                                    label = "Bitrate",
                                    value = formatBitrate(video.bitrate),
                                )
                            }
                            item {
                                StatsRow(
                                    label = "Est. bandwidth",
                                    value = formatBitrate(video.estimatedBandwidthInBps),
                                )
                            }
                            if (video.freezeCount > 0) {
                                item {
                                    StatsRow(
                                        label = "Freezes",
                                        value = "${video.freezeCount} (${video.totalFreezesDuration}ms)",
                                    )
                                }
                            }
                        }

                        sub.audioStats?.let { audio ->
                            item {
                                Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))
                            }
                            item { StatsSubHeader(text = "Audio") }
                            item {
                                StatsRow(
                                    label = "Packets received",
                                    value = audio.audioPacketsReceived.toString(),
                                )
                            }
                            item {
                                StatsRow(
                                    label = "Packets lost",
                                    value = audio.audioPacketsLost.toString(),
                                )
                            }
                            item {
                                StatsRow(
                                    label = "Bytes received",
                                    value = formatBytes(audio.audioBytesReceived.toLong()),
                                )
                            }
                            item {
                                StatsRow(
                                    label = "Est. bandwidth",
                                    value = formatBitrate(audio.estimatedBandwidthInBps),
                                )
                            }
                        }
                    }
                }
            }

            // ── Version footer ──────────────────────────
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
    }
}

// endregion

// region Reusable components

@Composable
private fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = VonageVideoTheme.typography.bodyBaseSemibold,
        color = VonageVideoTheme.colors.primary,
        modifier = modifier.padding(top = VonageVideoTheme.dimens.paddingSmall),
    )
}

@Composable
private fun SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        color = VonageVideoTheme.colors.border,
        modifier = modifier.padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = VonageVideoTheme.typography.bodyBaseSemibold,
                color = VonageVideoTheme.colors.secondary,
            )
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = description,
                    style = VonageVideoTheme.typography.caption,
                    color = VonageVideoTheme.colors.tertiary,
                )
            }
        }
        Spacer(modifier = Modifier.width(VonageVideoTheme.dimens.spaceDefault))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VonageVideoTheme.colors.primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = VonageVideoTheme.colors.border,
                uncheckedBorderColor = VonageVideoTheme.colors.border,
            ),
        )
    }
}

// endregion

// region Stats components

@Composable
private fun StatsSubHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = VonageVideoTheme.typography.bodyBaseSemibold,
        color = VonageVideoTheme.colors.secondary,
        modifier = modifier.padding(top = VonageVideoTheme.dimens.paddingXSmall),
    )
}

@Composable
private fun StatsRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.tertiary,
        )
        Text(
            text = value,
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.secondary,
        )
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
    bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
    else -> "$bytes B"
}

private fun formatBitrate(bps: Long): String = when {
    bps >= 1_000_000 -> String.format("%.1f Mbps", bps / 1_000_000.0)
    bps >= 1_000 -> String.format("%.1f Kbps", bps / 1_000.0)
    else -> "$bps bps"
}

// endregion

// region Previews

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
