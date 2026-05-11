package com.vonage.android.fx.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.videofx.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.io.File
import java.io.FileOutputStream

/**
 * Provides the list of pre-rendered background images available for virtual background replacement.
 *
 * Extracts drawable resources to local JPEG files in the app's cache directory so
 * the SDK can reference them by absolute path. Each image is center-cropped to the
 * publisher video frame's portrait aspect ratio before being written, preventing the
 * SDK from non-uniformly stretching a landscape image into a portrait frame.
 */
class BackgroundEffectsRepository(private val context: Context) {

    /**
     * @param captureResolution The configured publisher capture resolution, used to derive the
     *   portrait aspect ratio for the center-crop applied to each background image before
     *   caching. Pass `null` to use the default (9:16, matching `CaptureResolution.HIGH`
     *   which is auto-selected on devices with ≥ 512 MB RAM).
     */
    fun getBackgrounds(
        captureResolution: CaptureResolution? = null,
    ): ImmutableList<VideoBackgroundItem> {
        val (targetW, targetH) = portraitDimensionsFor(captureResolution)
        return backgroundEntries.map { entry ->
            VideoBackgroundItem(
                id = entry.id,
                thumbnailRes = entry.drawableRes,
                imagePath = extractToFile(entry.id, entry.drawableRes, targetW, targetH),
            )
        }.toImmutableList()
    }

    private fun extractToFile(
        id: String,
        drawableRes: Int,
        targetWidth: Int,
        targetHeight: Int,
    ): String {
        val dir = File(context.cacheDir, BACKGROUNDS_DIR)
        // Filename encodes target dimensions so different resolutions get separate cache entries
        // and old uncropped files (from the previous directory) are never picked up.
        val file = File(dir, "${id}_${targetWidth}x${targetHeight}.jpg")
        if (file.exists()) return file.absolutePath
        dir.mkdirs()
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableRes)
            ?: error("Failed to decode drawable resource $drawableRes for background '$id'")
        val cropped = bitmap.centerCropTo(targetWidth, targetHeight)
        FileOutputStream(file).use { out ->
            cropped.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }
        // centerCropTo() returns `this` when the aspect ratios already match, so guard against
        // double-recycling the same Bitmap object (which would crash compress() above).
        if (cropped !== bitmap) bitmap.recycle()
        cropped.recycle()
        return file.absolutePath
    }

    private companion object {
        // "video_backgrounds_cropped" avoids serving old uncropped files from "video_backgrounds/"
        const val BACKGROUNDS_DIR = "video_backgrounds_cropped"
        const val JPEG_QUALITY = 90

        val backgroundEntries = listOf(
            BackgroundEntry("bookshelf_room", R.drawable.bg_bookshelf_room),
            BackgroundEntry("busy_room", R.drawable.bg_busy_room),
            BackgroundEntry("dune_view", R.drawable.bg_dune_view),
            BackgroundEntry("hogwarts", R.drawable.bg_hogwarts),
            BackgroundEntry("library", R.drawable.bg_library),
            BackgroundEntry("new_york", R.drawable.bg_new_york),
            BackgroundEntry("plane", R.drawable.bg_plane),
            BackgroundEntry("white_room", R.drawable.bg_white_room),
        )

        /**
         * Returns portrait (width, height) dimensions for the publisher video frame that
         * corresponds to [resolution].
         *
         * OpenTok's Camera2VideoCapturer output pixel dimensions per tier:
         *   LOW         → 352 × 288 landscape → 288 × 352 portrait
         *   MEDIUM      → 640 × 480 landscape → 480 × 640 portrait  (3:4)
         *   HIGH        → 1280 × 720 landscape → 720 × 1280 portrait (9:16)
         *   HIGH_1080P  → 1920 × 1080 landscape → 1080 × 1920 portrait (9:16)
         *   null (auto) → defaults to HIGH (most devices ≥ 512 MB)
         *
         * Note: if the OpenTok BackgroundReplacement transformer processes frames BEFORE the
         * camera rotation is applied (i.e., on the raw landscape frame), replace these with
         * the landscape dimensions (352×288, 640×480, 1280×720, 1920×1080) and validate on
         * a real device.
         */
        @Suppress("MagicNumber")
        fun portraitDimensionsFor(resolution: CaptureResolution?): Pair<Int, Int> =
            when (resolution) {
                CaptureResolution.LOW -> 288 to 352
                CaptureResolution.MEDIUM -> 480 to 640
                CaptureResolution.HIGH, null -> 720 to 1280
                CaptureResolution.HIGH_1080P -> 1080 to 1920
            }
    }
}

private data class BackgroundEntry(val id: String, val drawableRes: Int)

/**
 * Returns a new [Bitmap] center-cropped to the given [targetWidth]:[targetHeight] aspect
 * ratio. The source bitmap is NOT recycled here — the caller must recycle it after use.
 *
 * Algorithm:
 *  - If the source is wider than the target ratio → keep full height, crop width.
 *  - If the source is taller than the target ratio → keep full width, crop height.
 *  - If ratios match exactly → return the source unchanged (no allocation).
 */
private fun Bitmap.centerCropTo(targetWidth: Int, targetHeight: Int): Bitmap {
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
        else -> this // already the right ratio
    }
}
