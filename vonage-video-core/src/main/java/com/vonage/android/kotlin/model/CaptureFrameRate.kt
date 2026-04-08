package com.vonage.android.kotlin.model

/**
 * App-level representation of the OpenTok camera capture frame rate.
 *
 * Maps to [com.opentok.android.Publisher.CameraCaptureFrameRate].
 * This is a **build-time** setting — changes take effect on the next publisher creation.
 *
 * @property label Human-readable label shown in the UI
 * @property fps Numeric frames-per-second value
 */
@Suppress("MagicNumber")
enum class CaptureFrameRate(
    val label: String,
    val fps: Int,
) {
    FPS_1("1 FPS", 1),
    FPS_7("7 FPS", 7),
    FPS_15("15 FPS", 15),
    FPS_30("30 FPS", 30),
}
