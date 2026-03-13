package com.vonage.android.settings

import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.model.VideoCodec
import com.vonage.android.shared.ViewState

@Stable
data class SettingsUiState(
    val appVersion: String = "",
    val sdkVersion: String = "",
    val call: CallFacade? = null,
    val senderStatsEnabled: Boolean = true,
    val opusDtxEnabled: Boolean = true,
    val publisherAudioFallbackEnabled: Boolean = true,
    val subscriberAudioFallbackEnabled: Boolean = true,
    val videoBitrateConfig: VideoBitrateConfig = VideoBitrateConfig(
        preset = VideoBitratePreset.DEFAULT,
        maxBitrate = VideoBitratePreset.DEFAULT.defaultMaxBitrate,
    ),
    val degradationPreference: DegradationPreference = DegradationPreference.NOT_SET,
    val captureFrameRate: CaptureFrameRate = CaptureFrameRate.FPS_15,
    val captureResolution: CaptureResolution? = null,
    val preferredVideoCodecOrder: List<VideoCodec>? = null,
    val audioBitrate: Int? = null,
) : ViewState
