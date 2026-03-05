package com.vonage.android.kotlin.model

import androidx.compose.runtime.Stable

/**
 * App-level representation of the OpenTok video bitrate presets.
 *
 * Maps to [com.opentok.android.PublisherKit.VideoBitratePreset].
 *
 * @property label Human-readable name for the preset
 * @property description Short explanation of the preset behavior
 * @property defaultMaxBitrate Suggested maxVideoBitrate (kbps) for the preset, or null to use SDK default
 */
enum class VideoBitratePreset(
    val label: String,
    val description: String,
    val defaultMaxBitrate: Int?,
) {
    DEFAULT(
        label = "Default",
        description = "Standard video bitrate",
        defaultMaxBitrate = null,
    ),
    BW_SAVER(
        label = "BW Saver",
        description = "Reduced bandwidth usage",
        defaultMaxBitrate = null,
    ),
    EXTRA_BW_SAVER(
        label = "Extra BW Saver",
        description = "Minimum bandwidth usage",
        defaultMaxBitrate = null,
    ),
    CUSTOM(
        label = "Custom",
        description = "Manual max bitrate (kbps)",
        defaultMaxBitrate = 2_000,
    ),
}

/**
 * Configuration for the publisher video bitrate.
 *
 * @property preset The selected bitrate preset
 * @property maxBitrate Custom max bitrate in kbps (only used when [preset] is [VideoBitratePreset.CUSTOM])
 */
@Stable
data class VideoBitrateConfig(
    val preset: VideoBitratePreset = VideoBitratePreset.DEFAULT,
    val maxBitrate: Int? = 2_000,
)
