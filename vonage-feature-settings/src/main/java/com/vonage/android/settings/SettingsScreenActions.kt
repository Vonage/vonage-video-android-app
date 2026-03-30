package com.vonage.android.settings

import androidx.compose.runtime.Immutable
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoCodec
import com.vonage.logger.LogLevel

@Immutable
data class SettingsScreenActions(
    val onFrameRateChange: (CaptureFrameRate) -> Unit = {},
    val onResolutionChange: (CaptureResolution?) -> Unit = {},
    val onVideoBitrateConfigChange: (VideoBitrateConfig) -> Unit = {},
    val onDegradationPreferenceChange: (DegradationPreference) -> Unit = {},
    val onPreferredVideoCodecOrderChange: (List<VideoCodec>?) -> Unit = {},
    val onOpusDtxToggle: (Boolean) -> Unit = {},
    val onLogsToggle: (Boolean) -> Unit = {},
    val onLogLevelChange: (LogLevel) -> Unit = {},
    val onSendLogsClick: () -> Unit = {},
    val onShareLogsClick: () -> Unit = {},
    val onAudioBitrateChange: (Int?) -> Unit = {},
    val onPublisherAudioFallbackToggle: (Boolean) -> Unit = {},
    val onSubscriberAudioFallbackToggle: (Boolean) -> Unit = {},
    val onSenderStatsTrackToggle: (Boolean) -> Unit = {},
    val onDismiss: () -> Unit = {},
)
