package com.vonage.android.fx.data

import android.graphics.Bitmap
import com.vonage.android.kotlin.model.CaptureResolution

/**
 * Returns the portrait (width x height) dimensions for the given [CaptureResolution].
 *
 * These dimensions are used to center-crop background images to match the publisher's
 * video frame aspect ratio before handing the file path to the SDK transformer.
 */
@Suppress("MagicNumber")
internal fun portraitDimensionsFor(resolution: CaptureResolution?): Pair<Int, Int> =
    when (resolution) {
        CaptureResolution.LOW -> 288 to 352
        CaptureResolution.MEDIUM -> 480 to 640
        CaptureResolution.HIGH, null -> 720 to 1280 // default / most devices
        CaptureResolution.HIGH_1080P -> 1080 to 1920
    }

/**
 * Returns a new [Bitmap] center-cropped to the given [targetWidth]:[targetHeight] aspect ratio.
 * The source bitmap is NOT recycled here — the caller must recycle it after use.
 *
 * - Source wider than target ratio → keep full height, crop left/right.
 * - Source taller than target ratio → keep full width, crop top/bottom.
 * - Ratios match exactly → return source unchanged (no allocation).
 */
internal fun Bitmap.centerCropTo(targetWidth: Int, targetHeight: Int): Bitmap {
    val srcRatio = width.toFloat() / height.toFloat()
    val dstRatio = targetWidth.toFloat() / targetHeight.toFloat()

    return when {
        srcRatio > dstRatio -> {
            // Source is wider → crop left/right
            val cropWidth = (height * dstRatio).toInt().coerceAtMost(width)
            val left = (width - cropWidth) / 2
            Bitmap.createBitmap(this, left, 0, cropWidth, height)
        }
        srcRatio < dstRatio -> {
            // Source is taller → crop top/bottom
            val cropHeight = (width / dstRatio).toInt().coerceAtMost(height)
            val top = (height - cropHeight) / 2
            Bitmap.createBitmap(this, 0, top, width, cropHeight)
        }
        else -> this // ratios already match
    }
}
