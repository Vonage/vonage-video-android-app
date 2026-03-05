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
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.settings.R
import com.vonage.android.settings.ui.components.SectionDivider
import com.vonage.android.settings.ui.components.SectionHeader
import com.vonage.android.settings.util.formatBitrate
import com.vonage.android.settings.util.formatBytes
import java.util.Locale

@Composable
internal fun PublisherStats(
    callFacade: CallFacade,
    modifier: Modifier = Modifier,
) {
    val publisher by callFacade.publisher.collectAsStateWithLifecycle()
    publisher?.let { pub ->
        val videoStats by pub.videoStats.collectAsStateWithLifecycle()
        val audioStats by pub.audioStats.collectAsStateWithLifecycle()
        Column(
            modifier = modifier,
        ) {
            SectionDivider()
            SectionHeader(text = stringResource(R.string.settings_stats_publisher_stats_header))
            videoStats?.let {
                StatsRow(
                    label = stringResource(R.string.settings_stats_publisher_est_bandwidth),
                    value = it.estimatedBandwidthInBps.formatBitrate(),
                )
            }
            videoStats?.let { PublisherVideoStats(it) }
            audioStats?.let { PublisherAudioStats(it) }
        }
    }
}

@Composable
private fun PublisherAudioStats(audio: PublisherState.AudioStats) {
    Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))
    StatsSubHeader(text = stringResource(R.string.settings_stats_publisher_audio_header))
    StatsRow(
        label = stringResource(R.string.settings_stats_publisher_audio_packets_sent),
        value = "${audio.audioPacketsSent} / ${audio.audioPacketsLost}",
    )
    StatsRow(
        label = stringResource(R.string.settings_stats_publisher_audio_bytes_sent),
        value = audio.audioBytesSent.formatBytes(),
    )
}

@Composable
private fun PublisherVideoStats(video: PublisherState.VideoStats) {
    StatsSubHeader(text = stringResource(R.string.settings_stats_publisher_video_header))
    StatsRow(
        label = stringResource(R.string.settings_stats_publisher_video_packets_sent),
        value = "${video.videoPacketsSent} / ${video.videoPacketsLost}",
    )
    StatsRow(
        label = stringResource(R.string.settings_stats_publisher_video_bytes_sent),
        value = video.videoBytesSent.formatBytes(),
    )
    video.videoLayerStats.forEachIndexed { index, layer ->
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
