package com.vonage.android.data.storage

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_AUDIO_BITRATE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_BACKGROUND_ID
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_BACKGROUND_PATH
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CAPTURE_FRAME_RATE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CAPTURE_RESOLUTION
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CODEC_ORDER
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_DEGRADATION_PREFERENCE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_OPUS_DTX
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_PUBLISHER_AUDIO_FALLBACK
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_PUBLISH_AUDIO
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_PUBLISH_VIDEO
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_SENDER_STATS
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_SUBSCRIBER_AUDIO_FALLBACK
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_VIDEO_BITRATE_MAX
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_VIDEO_BITRATE_PRESET
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_VIDEO_EFFECT
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.model.VideoCodec
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.settings.CallSettingsStorage
import com.vonage.android.settings.PersistedCallSettings
import com.vonage.android.settings.PublisherPreferences
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
            publisherPreferences = PublisherPreferences(
                publishAudio = prefs[SETTINGS_PUBLISH_AUDIO],
                publishVideo = prefs[SETTINGS_PUBLISH_VIDEO],
                videoEffect = prefs.videoEffect(),
            ),
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
            prefs.putPublisherPreferences(settings.publisherPreferences)
        }
    }

    private fun Preferences.videoEffect(): VideoEffect = when (this[SETTINGS_VIDEO_EFFECT]) {
        EFFECT_BLUR_LOW -> VideoEffect.BlurLow
        EFFECT_BLUR_HIGH -> VideoEffect.BlurHigh
        EFFECT_BACKGROUND_IMAGE -> backgroundImage() ?: VideoEffect.None
        else -> VideoEffect.None
    }

    private fun Preferences.backgroundImage(): VideoEffect.BackgroundImage? {
        val id = this[SETTINGS_BACKGROUND_ID] ?: return null
        val path = this[SETTINGS_BACKGROUND_PATH] ?: return null
        return VideoEffect.BackgroundImage(id = id, imagePath = path)
    }

    private fun MutablePreferences.putPublisherPreferences(preferences: PublisherPreferences) {
        putOrRemove(SETTINGS_PUBLISH_AUDIO, preferences.publishAudio)
        putOrRemove(SETTINGS_PUBLISH_VIDEO, preferences.publishVideo)
        val effect = preferences.videoEffect
        this[SETTINGS_VIDEO_EFFECT] = when (effect) {
            VideoEffect.None -> EFFECT_NONE
            VideoEffect.BlurLow -> EFFECT_BLUR_LOW
            VideoEffect.BlurHigh -> EFFECT_BLUR_HIGH
            is VideoEffect.BackgroundImage -> EFFECT_BACKGROUND_IMAGE
        }
        val image = effect as? VideoEffect.BackgroundImage
        putOrRemove(SETTINGS_BACKGROUND_ID, image?.id)
        putOrRemove(SETTINGS_BACKGROUND_PATH, image?.imagePath)
    }

    private fun <T> MutablePreferences.putOrRemove(key: Preferences.Key<T>, value: T?) {
        if (value != null) this[key] = value else remove(key)
    }

    private companion object {
        const val EFFECT_NONE = "NONE"
        const val EFFECT_BLUR_LOW = "BLUR_LOW"
        const val EFFECT_BLUR_HIGH = "BLUR_HIGH"
        const val EFFECT_BACKGROUND_IMAGE = "BACKGROUND_IMAGE"
    }
}
