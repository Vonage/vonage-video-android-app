package com.vonage.android.fx.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Manages user-uploaded virtual background images stored in [Context.getFilesDir].
 *
 * Images are persisted in `filesDir/user_backgrounds/` as JPEG files, named with a
 * millisecond timestamp prefix so the directory scan produces a chronological list.
 * Unlike [DefaultBackgroundEffectsRepository] which writes to `cacheDir`, this directory is
 * never cleared by the system, giving user uploads the same lifetime as app data.
 */
class DefaultUserBackgroundRepository(private val context: Context) : UserBackgroundRepository {

    /**
     * Scans [filesDir]/user_backgrounds/ and returns one [VideoBackgroundItem] per JPEG.
     * Items are sorted alphabetically by filename (timestamp prefix → chronological order).
     * [captureResolution] is accepted for API symmetry with [DefaultBackgroundEffectsRepository]
     * but is not used — images were already cropped to the resolution active at save time.
     */
    override suspend fun getUserBackgrounds(
        captureResolution: CaptureResolution?,
    ): ImmutableList<VideoBackgroundItem> = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, USER_BACKGROUNDS_DIR)
        if (!dir.exists()) return@withContext persistentListOf()
        dir.listFiles()
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
            ?: persistentListOf()
    }

    /**
     * Reads the image at [uri] via [Context.getContentResolver], center-crops it to the
     * portrait frame dimensions for [captureResolution], and writes a JPEG to
     * `filesDir/user_backgrounds/<timestamp>.jpg`.
     *
     * @return The [VideoBackgroundItem] representing the saved image, or `null` if the URI
     *         cannot be decoded (e.g. corrupt or inaccessible stream).
     */
    override suspend fun saveBackground(uri: Uri, captureResolution: CaptureResolution?): VideoBackgroundItem? =
        withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, USER_BACKGROUNDS_DIR)
            dir.mkdirs()

            // Belt-and-suspenders: guard against concurrent or stale-UI saves exceeding the cap.
            val existingCount = dir.listFiles()?.count { it.isFile && it.extension == "jpg" } ?: 0
            if (existingCount >= UserBackgroundRepository.MAX_USER_BACKGROUNDS) return@withContext null

            val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            } ?: return@withContext null

            val id = "$USER_ID_PREFIX${System.currentTimeMillis()}"
            val file = File(dir, "$id.jpg")

            val (targetW, targetH) = portraitDimensionsFor(captureResolution)
            val cropped = bitmap.centerCropTo(targetW, targetH)

            FileOutputStream(file).use { out ->
                cropped.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            if (cropped !== bitmap) bitmap.recycle()
            cropped.recycle()

            VideoBackgroundItem(
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
    override suspend fun deleteBackground(id: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(File(context.filesDir, USER_BACKGROUNDS_DIR), "$id.jpg")
        file.delete()
    }

    private companion object {
        private const val USER_BACKGROUNDS_DIR = "user_backgrounds"
        private const val USER_ID_PREFIX = "user_bg_"
        private const val JPEG_QUALITY = 90
    }
}
