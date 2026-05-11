package com.vonage.android.fx.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.videofx.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.io.File
import java.io.FileOutputStream

/**
 * Provides the list of pre-rendered background images available for virtual background replacement.
 *
 * Extracts drawable resources to local JPEG files in the app's cache directory so
 * the SDK can reference them by absolute path.
 */
class BackgroundEffectsRepository(private val context: Context) {

    fun getBackgrounds(): ImmutableList<VideoBackgroundItem> =
        backgroundEntries.map { entry ->
            VideoBackgroundItem(
                id = entry.id,
                thumbnailRes = entry.drawableRes,
                imagePath = extractToFile(entry.id, entry.drawableRes),
            )
        }.toImmutableList()

    private fun extractToFile(id: String, drawableRes: Int): String {
        val dir = File(context.cacheDir, BACKGROUNDS_DIR)
        val file = File(dir, "$id.jpg")
        if (file.exists()) return file.absolutePath
        dir.mkdirs()
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableRes)
            ?: error("Failed to decode drawable resource $drawableRes for background '$id'")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }
        bitmap.recycle()
        return file.absolutePath
    }

    private companion object {
        const val BACKGROUNDS_DIR = "video_backgrounds"
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
