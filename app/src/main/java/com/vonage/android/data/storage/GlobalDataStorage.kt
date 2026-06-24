package com.vonage.android.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "global")

@Singleton
class GlobalDataStorage @Inject constructor(
    @ApplicationContext context: Context,
) : DataStore<Preferences> by context.dataStore {
    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val SETTINGS_CAPTURE_FRAME_RATE = stringPreferencesKey("settings_capture_frame_rate")
        val SETTINGS_CAPTURE_RESOLUTION = stringPreferencesKey("settings_capture_resolution")
        val SETTINGS_CODEC_ORDER = stringPreferencesKey("settings_codec_order")
        val SETTINGS_AUDIO_BITRATE = intPreferencesKey("settings_audio_bitrate")
        val SETTINGS_OPUS_DTX = booleanPreferencesKey("settings_opus_dtx")
        val SETTINGS_PUBLISHER_AUDIO_FALLBACK = booleanPreferencesKey("settings_publisher_audio_fallback")
        val SETTINGS_SUBSCRIBER_AUDIO_FALLBACK = booleanPreferencesKey("settings_subscriber_audio_fallback")
        val SETTINGS_SENDER_STATS = booleanPreferencesKey("settings_sender_stats")
        val SETTINGS_VIDEO_BITRATE_PRESET = stringPreferencesKey("settings_video_bitrate_preset")
        val SETTINGS_VIDEO_BITRATE_MAX = intPreferencesKey("settings_video_bitrate_max")
        val SETTINGS_DEGRADATION_PREFERENCE = stringPreferencesKey("settings_degradation_preference")
    }
}
