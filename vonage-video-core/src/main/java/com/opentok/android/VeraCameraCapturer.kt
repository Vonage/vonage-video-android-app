package com.opentok.android

import android.content.Context

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
