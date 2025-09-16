package com.vonage.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.Window
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.scale
import com.vonage.android.di.DefaultDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ImageProcessor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {

    private val scope = CoroutineScope(dispatcher)

    suspend fun extractImageFromUri(uri: Uri): Result<ImageBitmap> =
        runCatching {
            val imageStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(imageStream, null, BitmapFactory.Options())
            imageStream?.close()
            return bitmap?.let {
                Result.success(bitmap.asImageBitmap())
            } ?: Result.failure(Exception("Unknown error while extracting image from URI $uri"))
        }

    suspend fun extractImageFromWindow(window: Window): Result<ImageBitmap> =
        runCatching {
            val view = window.decorView
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            return Result.success(bitmap.asImageBitmap())
        }

    suspend fun encodeImageToBase64(imageBitmap: ImageBitmap?): String =
        scope.async { imageBitmap.toBase64() }.await()

    private fun ImageBitmap?.toBase64(): String {
        this?.let { imageBitmap ->
            val outputStream = ByteArrayOutputStream()
            imageBitmap.asAndroidBitmap()
                .scale(width / 2, height / 2, false)
                .compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, outputStream)
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        }
        return ""
    }

    private companion object {
        const val PNG_QUALITY = 100
    }
}