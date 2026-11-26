package com.vonage.android.util

import android.app.DownloadManager
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DownloadManager @Inject constructor(
    @param:ApplicationContext val context: Context,
) {

    fun downloadByUrl(url: String) {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = url.toUri()
        DownloadManager.Request(uri)
            .apply {
                // Public Downloads needs WRITE_EXTERNAL_STORAGE before API 29; the app-specific dir needs nothing.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FILE_NAME)
                } else {
                    setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, FILE_NAME)
                }
                setMimeType("video/mp4")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }
            .let { request ->
                manager.enqueue(request)
            }
    }

    private companion object {
        const val FILE_NAME = "vonage-recording"
    }
}
