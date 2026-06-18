package com.vonage.android.settings

import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.model.VideoCodec

data class PersistedCallSettings(
    val captureFrameRate: CaptureFrameRate = CaptureFrameRate.FPS_15,
    val captureResolution: CaptureResolution? = null,
    val preferredVideoCodecOrder: List<VideoCodec>? = null,
    val audioBitrate: Int? = null,
    val opusDtxEnabled: Boolean = true,
    val publisherAudioFallbackEnabled: Boolean = true,
    val subscriberAudioFallbackEnabled: Boolean = true,
    val senderStatsEnabled: Boolean = true,
    val videoBitrateConfig: VideoBitrateConfig = VideoBitrateConfig(
        preset = VideoBitratePreset.DEFAULT,
        maxBitrate = VideoBitratePreset.DEFAULT.defaultMaxBitrate,
    ),
    val degradationPreference: DegradationPreference = DegradationPreference.NOT_SET,
)

interface CallSettingsStorage {
    suspend fun load(): PersistedCallSettings
    suspend fun save(settings: PersistedCallSettings)
}

class NoOpCallSettingsStorage : CallSettingsStorage {
    override suspend fun load() = PersistedCallSettings()
    override suspend fun save(settings: PersistedCallSettings) = Unit
}
