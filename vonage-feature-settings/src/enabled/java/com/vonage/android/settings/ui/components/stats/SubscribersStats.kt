package com.vonage.android.settings.ui.components.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.settings.R
import com.vonage.android.settings.ui.components.SectionDivider
import com.vonage.android.settings.ui.components.SectionHeader
import com.vonage.android.settings.util.formatBitrate
import com.vonage.android.settings.util.formatBytes
import java.util.Locale

@Composable
fun SubscribersStats(
    callFacade: CallFacade,
    modifier: Modifier = Modifier,
) {
    val participants by callFacade.participantsStateFlow.collectAsStateWithLifecycle()
    Column(
        modifier = modifier,
    ) {
        participants
            .filterIsInstance<ParticipantState>()
            .forEach { sub ->
                val videoStats by sub.videoStats.collectAsStateWithLifecycle()
                val audioStats by sub.audioStats.collectAsStateWithLifecycle()
                SectionDivider()
                SectionHeader(
                    text = stringResource(
                        R.string.settings_stats_subscriber_stats_header,
                        sub.name,
                    )
                )
                videoStats?.let { SubscriberVideoStats(it) }
                audioStats?.let { SubscriberAudioStats(it) }
            }
    }
}

@Composable
private fun SubscriberVideoStats(video: ParticipantState.SubscriberVideoStats) {
    Column {
        StatsSubHeader(text = stringResource(R.string.settings_stats_subscriber_video_header))
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
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_video_packets_received),
            value = video.videoPacketsReceived.toString(),
        )
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_video_packets_lost),
            value = video.videoPacketsLost.toString(),
        )
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_video_bytes_received),
            value = video.videoBytesReceived.toLong().formatBytes(),
        )
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_video_bitrate),
            value = video.bitrate.formatBitrate(),
        )
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_video_est_bandwidth),
            value = video.estimatedBandwidthInBps.formatBitrate(),
        )
        if (video.freezeCount > 0) {
            StatsRow(
                label = stringResource(R.string.settings_stats_subscriber_video_freezes),
                value = "${video.freezeCount} (${video.totalFreezesDuration}ms)",
            )
        }
    }
}

@Composable
private fun SubscriberAudioStats(audio: ParticipantState.SubscriberAudioStats) {
    Column {
        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))
        StatsSubHeader(text = stringResource(R.string.settings_stats_subscriber_audio_header))
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_audio_packets_received),
            value = audio.audioPacketsReceived.toString(),
        )
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_audio_packets_lost),
            value = audio.audioPacketsLost.toString(),
        )
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_audio_bytes_received),
            value = audio.audioBytesReceived.toLong().formatBytes(),
        )
        StatsRow(
            label = stringResource(R.string.settings_stats_subscriber_audio_est_bandwidth),
            value = audio.estimatedBandwidthInBps.formatBitrate(),
        )
    }
}
