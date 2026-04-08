package com.vonage.android.kotlin.model

/**
 * App-level representation of the OpenTok camera capture resolution.
 *
 * Maps to [com.opentok.android.Publisher.CameraCaptureResolution].
 * This is a **build-time** setting — changes take effect on the next publisher creation.
 *
 * @property label Human-readable label shown in the UI
 * @property description Short description of the resolution tier
 */
enum class CaptureResolution(
    val label: String,
    val description: String,
) {
    LOW("Low", "Lowest resolution (352×288)"),
    MEDIUM("Medium", "Standard resolution (640×480)"),
    HIGH("High", "HD resolution (1280×720)"),
    HIGH_1080P("1080p", "Full-HD resolution (1920×1080)"),
}
