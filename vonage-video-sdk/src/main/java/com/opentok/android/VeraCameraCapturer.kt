package com.opentok.android

import android.content.Context

/**
 * Camera capturer wrapping [Camera2VideoCapturer] that applies an initial camera index
 * after the camera session is configured (needed because the SDK selects a default camera).
 */
internal class VeraCameraCapturer(
    context: Context,
    resolution: Publisher.CameraCaptureResolution,
    frameRate: Publisher.CameraCaptureFrameRate,
    initialCameraIndex: Int,
) : Camera2VideoCapturer(context, resolution, frameRate) {

    init {
        super.executeAfterCameraSessionConfigured = Runnable {
            super.swapCamera(initialCameraIndex)
        }
    }
}
