package com.vonage.android.util

import android.content.Context
import android.content.Intent
import com.vonage.android.BuildConfig

fun Context.navigateToShare(roomName: String) {
    // build URL based on environment
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, "${BuildConfig.BASE_API_URL}/room/$roomName")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}
