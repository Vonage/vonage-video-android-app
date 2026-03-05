package com.vonage.android.kotlin.model

/**
 * App-level representation of the OpenTok degradation preference.
 *
 * Controls what the publisher prioritises when network conditions degrade.
 * Maps to [com.opentok.android.PublisherKit.DegradationPreference].
 *
 * @property label Human-readable name shown in the UI
 * @property description Short explanation of the behaviour
 */
enum class DegradationPreference(
    val label: String,
    val description: String,
) {
    NOT_SET(
        label = "Not Set",
        description = "No preference — SDK decides automatically",
    ),
    MAINTAIN_FRAME_RATE_AND_RESOLUTION(
        label = "Maintain FPS & Resolution",
        description = "Preserve both frame rate and resolution (may drop quality instead)",
    ),
    MAINTAIN_FRAME_RATE(
        label = "Maintain FPS",
        description = "Preserve frame rate, allow resolution to decrease",
    ),
    MAINTAIN_RESOLUTION(
        label = "Maintain Resolution",
        description = "Preserve resolution, allow frame rate to decrease",
    ),
    BALANCED(
        label = "Balanced",
        description = "Balance between frame rate and resolution",
    ),
}
