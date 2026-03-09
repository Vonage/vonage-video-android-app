package com.vonage.android.kotlin.model

import com.opentok.android.PublisherKit

/**
 * App-level representation of the OpenTok degradation preference.
 *
 * Controls what the publisher prioritizes when network conditions degrade.
 * Maps to [com.opentok.android.PublisherKit.DegradationPreference].
 *
 * @property label Human-readable name shown in the UI
 * @property description Short explanation of the behavior
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

fun DegradationPreference.toSdkValue(): PublisherKit.DegradationPreference = when (this) {
    DegradationPreference.NOT_SET, null ->
        PublisherKit.DegradationPreference.DegradationPreferenceNotSet

    DegradationPreference.MAINTAIN_FRAME_RATE_AND_RESOLUTION ->
        PublisherKit.DegradationPreference.DegradationPreferenceMaintainFrameRateAndResolution

    DegradationPreference.MAINTAIN_FRAME_RATE ->
        PublisherKit.DegradationPreference.DegradationPreferenceMaintainFrameRate

    DegradationPreference.MAINTAIN_RESOLUTION ->
        PublisherKit.DegradationPreference.DegradationPreferenceMaintainResolution

    DegradationPreference.BALANCED ->
        PublisherKit.DegradationPreference.DegradationPreferenceBalanced
}
