package com.vonage.android.fx.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.io.File
import java.io.FileOutputStream

/**
 * Manages user-uploaded virtual background images stored in [Context.getFilesDir].
 *
 * Images are persisted in `filesDir/user_backgrounds/` as JPEG files, named with a
 * millisecond timestamp prefix so the directory scan produces a chronological list.
 * Unlike [BackgroundEffectsRepository] which writes to `cacheDir`, this directory is
 * never cleared by the system, giving user uploads the same lifetime as app data.
 *
 * All IO-bound functions must be called from a background thread (e.g. [kotlinx.coroutines.Dispatchers.IO]).
 */
class UserBackgroundRepository(private val context: Context) {

    /**
     * Scans [filesDir]/user_backgrounds/ and returns one [VideoBackgroundItem] per JPEG.
     * Items are sorted alphabetically by filename (timestamp prefix → chronological order).
     * [captureResolution] is accepted for API symmetry with [BackgroundEffectsRepository] but is
     * not used — images were already cropped to the resolution active at save time.
     */
    fun getUserBackgrounds(
        @Suppress("UnusedParameter") captureResolution: CaptureResolution? = null,
    ): ImmutableList<VideoBackgroundItem> {
        val dir = File(context.filesDir, USER_BACKGROUNDS_DIR)
        if (!dir.exists()) return kotlinx.collections.immutable.persistentListOf()
        return dir.listFiles()
            ?.filter { it.isFile && it.extension == "jpg" }
            ?.sortedBy { it.name }
            ?.map { file ->
                VideoBackgroundItem(
                    id = file.nameWithoutExtension,
                    thumbnailRes = null,
                    imagePath = file.absolutePath,
                    isUserUploaded = true,
                )
            }
            ?.toImmutableList()
            ?: kotlinx.collections.immutable.persistentListOf()
    }

    /**
     * Reads the image at [uri] via [Context.getContentResolver], center-crops it to the
     * portrait frame dimensions for [captureResolution], and writes a JPEG to
     * `filesDir/user_backgrounds/<timestamp>.jpg`.
     *
     * @return The [VideoBackgroundItem] representing the saved image, or `null` if the URI
     *         cannot be decoded (e.g. corrupt or inaccessible stream).
     */
    fun saveBackground(uri: Uri, captureResolution: CaptureResolution? = null): VideoBackgroundItem? {
        val dir = File(context.filesDir, USER_BACKGROUNDS_DIR)
        dir.mkdirs()

        val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: return null

        val id = "$USER_ID_PREFIX${System.currentTimeMillis()}"
        val file = File(dir, "$id.jpg")

        val (targetW, targetH) = portraitDimensionsFor(captureResolution)
        val cropped = bitmap.centerCropTo(targetW, targetH)

        FileOutputStream(file).use { out ->
            cropped.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }
        if (cropped !== bitmap) bitmap.recycle()
        cropped.recycle()

        return VideoBackgroundItem(
            id = id,
            thumbnailRes = null,
            imagePath = file.absolutePath,
            isUserUploaded = true,
        )
    }

    /**
     * Deletes the background image whose filename (without extension) matches [id].
     * @return `true` if the file was found and deleted successfully.
     */
    fun deleteBackground(id: String): Boolean {
        val file = File(File(context.filesDir, USER_BACKGROUNDS_DIR), "$id.jpg")
        return file.delete()
    }

    /**
     * Returns the number of stored user backgrounds without constructing the full item list.
     * Used to compute [canAddBackground] efficiently.
     */
    fun getCount(): Int {
        val dir = File(context.filesDir, USER_BACKGROUNDS_DIR)
        return dir.listFiles()?.count { it.isFile && it.extension == "jpg" } ?: 0
    }

    companion object {
        /** Configurable upper limit. The "Add image" tile is hidden once this is reached. */
        const val MAX_USER_BACKGROUNDS = 10

        private const val USER_BACKGROUNDS_DIR = "user_backgrounds"
        private const val USER_ID_PREFIX = "user_bg_"
        private const val JPEG_QUALITY = 90
    }
}
