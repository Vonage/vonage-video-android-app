package com.vonage.android.settings

import androidx.annotation.StringRes

/**
 * Sections for grouping settings in the UI.
 *
 * The declaration order defines the display order of sections.
 */
enum class SettingsSection(@param:StringRes val titleRes: Int) {
    VIDEO(R.string.settings_section_video),
    AUDIO(R.string.settings_section_audio),
    STATS(R.string.settings_section_stats),
}

/**
 * Defines all configurable settings with their section.
 *
 * The declaration order within each section defines the display order of items.
 * All settings can be modified at any time. Changes to settings that affect
 * publisher creation will trigger a publisher refresh automatically.
 */
enum class Setting(val section: SettingsSection) {
    FRAME_RATE(section = SettingsSection.VIDEO),
    RESOLUTION(section = SettingsSection.VIDEO),
    VIDEO_BITRATE(section = SettingsSection.VIDEO),
    DEGRADATION_PREFERENCE(section = SettingsSection.VIDEO),
    PREFERRED_CODEC_ORDER(section = SettingsSection.VIDEO),
    OPUS_DTX(section = SettingsSection.AUDIO),
    AUDIO_BITRATE(section = SettingsSection.AUDIO),
    PUBLISHER_AUDIO_FALLBACK(section = SettingsSection.AUDIO),
    SUBSCRIBER_AUDIO_FALLBACK(section = SettingsSection.AUDIO),
    SENDER_STATS(section = SettingsSection.STATS),
}

/**
 * Returns the settings for this section in declaration order.
 */
val SettingsSection.settings: List<Setting>
    get() = Setting.entries.filter { it.section == this }
