package com.vonage.android.fx.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.videofx.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
class DefaultBackgroundEffectsRepository(private val context: Context) : BackgroundEffectsRepository {

    /**
     * @param captureResolution The configured publisher capture resolution, used to derive the
     *   portrait aspect ratio for the center-crop applied to each background image before
     *   caching. Pass `null` to use the default (9:16, matching `CaptureResolution.HIGH`
     *   which is auto-selected on devices with ≥ 512 MB RAM).
     */
    override suspend fun getBackgrounds(
        captureResolution: CaptureResolution?,
    ): ImmutableList<VideoBackgroundItem> = withContext(Dispatchers.IO) {
        val (targetW, targetH) = portraitDimensionsFor(captureResolution)
        backgroundEntries.map { entry ->
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
    }
}

private data class BackgroundEntry(val id: String, val drawableRes: Int)
