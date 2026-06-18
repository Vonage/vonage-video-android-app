package com.vonage.android.data.storage

import androidx.datastore.preferences.core.edit
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_AUDIO_BITRATE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CAPTURE_FRAME_RATE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CAPTURE_RESOLUTION
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CODEC_ORDER
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_DEGRADATION_PREFERENCE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_OPUS_DTX
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_PUBLISHER_AUDIO_FALLBACK
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_SENDER_STATS
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_SUBSCRIBER_AUDIO_FALLBACK
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_VIDEO_BITRATE_MAX
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_VIDEO_BITRATE_PRESET
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.model.VideoCodec
import com.vonage.android.settings.CallSettingsStorage
import com.vonage.android.settings.PersistedCallSettings
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

class DataStoreCallSettingsStorage @Inject constructor(
    private val dataStore: GlobalDataStorage,
) : CallSettingsStorage {

    override suspend fun load(): PersistedCallSettings {
        val prefs = dataStore.data.firstOrNull() ?: return PersistedCallSettings()
        return PersistedCallSettings(
            captureFrameRate = prefs[SETTINGS_CAPTURE_FRAME_RATE]
                ?.let { CaptureFrameRate.valueOf(it) } ?: CaptureFrameRate.FPS_15,
            captureResolution = prefs[SETTINGS_CAPTURE_RESOLUTION]
                ?.let { CaptureResolution.valueOf(it) },
            preferredVideoCodecOrder = prefs[SETTINGS_CODEC_ORDER]
                ?.split(",")?.map { VideoCodec.valueOf(it) },
            audioBitrate = prefs[SETTINGS_AUDIO_BITRATE],
            opusDtxEnabled = prefs[SETTINGS_OPUS_DTX] ?: true,
            publisherAudioFallbackEnabled = prefs[SETTINGS_PUBLISHER_AUDIO_FALLBACK] ?: true,
            subscriberAudioFallbackEnabled = prefs[SETTINGS_SUBSCRIBER_AUDIO_FALLBACK] ?: true,
            senderStatsEnabled = prefs[SETTINGS_SENDER_STATS] ?: true,
            videoBitrateConfig = VideoBitrateConfig(
                preset = prefs[SETTINGS_VIDEO_BITRATE_PRESET]
                    ?.let { VideoBitratePreset.valueOf(it) } ?: VideoBitratePreset.DEFAULT,
                maxBitrate = prefs[SETTINGS_VIDEO_BITRATE_MAX],
            ),
            degradationPreference = prefs[SETTINGS_DEGRADATION_PREFERENCE]
                ?.let { DegradationPreference.valueOf(it) } ?: DegradationPreference.NOT_SET,
        )
    }

    override suspend fun save(settings: PersistedCallSettings) {
        val captureResolution = settings.captureResolution
        val preferredVideoCodecOrder = settings.preferredVideoCodecOrder
        val audioBitrate = settings.audioBitrate
        val maxBitrate = settings.videoBitrateConfig.maxBitrate
        dataStore.edit { prefs ->
            prefs[SETTINGS_CAPTURE_FRAME_RATE] = settings.captureFrameRate.name
            if (captureResolution != null) prefs[SETTINGS_CAPTURE_RESOLUTION] = captureResolution.name
            else prefs.remove(SETTINGS_CAPTURE_RESOLUTION)
            if (preferredVideoCodecOrder != null) prefs[SETTINGS_CODEC_ORDER] = preferredVideoCodecOrder.joinToString(",") { it.name }
            else prefs.remove(SETTINGS_CODEC_ORDER)
            if (audioBitrate != null) prefs[SETTINGS_AUDIO_BITRATE] = audioBitrate
            else prefs.remove(SETTINGS_AUDIO_BITRATE)
            prefs[SETTINGS_OPUS_DTX] = settings.opusDtxEnabled
            prefs[SETTINGS_PUBLISHER_AUDIO_FALLBACK] = settings.publisherAudioFallbackEnabled
            prefs[SETTINGS_SUBSCRIBER_AUDIO_FALLBACK] = settings.subscriberAudioFallbackEnabled
            prefs[SETTINGS_SENDER_STATS] = settings.senderStatsEnabled
            prefs[SETTINGS_VIDEO_BITRATE_PRESET] = settings.videoBitrateConfig.preset.name
            if (maxBitrate != null) prefs[SETTINGS_VIDEO_BITRATE_MAX] = maxBitrate
            else prefs.remove(SETTINGS_VIDEO_BITRATE_MAX)
            prefs[SETTINGS_DEGRADATION_PREFERENCE] = settings.degradationPreference.name
        }
    }
}
