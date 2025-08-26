package com.vonage.android.screen.goodbye

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DownloadManager @Inject constructor(
    @param:ApplicationContext val context: Context,
) {

    fun downloadByUrl(url: String) {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val uri = url.toUri()
        DownloadManager.Request(uri)
            .apply {
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "vonage recording")
                setMimeType("video/mp4")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            }
            .let { request ->
                manager?.enqueue(request)
            }
    }
}
