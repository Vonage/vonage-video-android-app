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
 * Returns a new [Bitmap] center-cropped **and scaled** to exactly [targetWidth] × [targetHeight].
 * The source bitmap is NOT recycled here — the caller must recycle it after use.
 *
 * - Step 1 (crop): adjust the aspect ratio by trimming the wider dimension.
 * - Step 2 (scale): resize the cropped result to the exact target pixel dimensions.
 *
 * Any intermediate bitmap created during cropping is recycled internally.
 * If the source already has the correct ratio and dimensions, it is returned unchanged
 * (no allocation).
 */
internal fun Bitmap.centerCropTo(targetWidth: Int, targetHeight: Int): Bitmap {
    val srcRatio = width.toFloat() / height.toFloat()
    val dstRatio = targetWidth.toFloat() / targetHeight.toFloat()

    // Step 1: crop to the correct aspect ratio
    val cropped: Bitmap = when {
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
        else -> this // ratios already match — no crop needed
    }

    // Step 2: scale to exact target dimensions (if not already the right size)
    if (cropped.width == targetWidth && cropped.height == targetHeight) return cropped
    val scaled = Bitmap.createScaledBitmap(cropped, targetWidth, targetHeight, true)
    if (cropped !== this) cropped.recycle() // recycle the intermediate crop; caller owns `this`
    return scaled
}
