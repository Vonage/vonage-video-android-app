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
        val request = DownloadManager.Request(uri)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "vonage recording")
        request.setMimeType("video/mp4")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        manager?.enqueue(request)
    }
}
