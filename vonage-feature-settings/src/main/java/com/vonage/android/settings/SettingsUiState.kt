package com.vonage.android.settings

import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset

@Stable
data class SettingsUiState(
    val appVersion: String = "",
    val sdkVersion: String = "",
    val call: CallFacade? = null,
    val senderStatsEnabled: Boolean = true,
    val opusDtxEnabled: Boolean = true,
    val videoBitrateConfig: VideoBitrateConfig = VideoBitrateConfig(
        preset = VideoBitratePreset.BW_SAVER,
        maxBitrate = VideoBitratePreset.BW_SAVER.defaultMaxBitrate,
    ),
    val degradationPreference: DegradationPreference = DegradationPreference.NOT_SET,
    val captureFrameRate: CaptureFrameRate = CaptureFrameRate.FPS_15,
    val captureResolution: CaptureResolution? = null,
)
