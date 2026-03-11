package com.vonage.android.settings

/**
 * Defines all configurable settings.
 *
 * All settings can be modified at any time. Changes to settings that affect
 * publisher creation will trigger a publisher refresh automatically.
 */
enum class Setting {
    FRAME_RATE,
    RESOLUTION,
    VIDEO_BITRATE,
    DEGRADATION_PREFERENCE,
    PREFERRED_CODEC_ORDER,
    OPUS_DTX,
    AUDIO_BITRATE,
    PUBLISHER_AUDIO_FALLBACK,
    SUBSCRIBER_AUDIO_FALLBACK,
    SENDER_STATS,
}
