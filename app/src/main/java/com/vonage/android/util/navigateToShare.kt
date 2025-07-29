package com.vonage.android.util

import android.content.Context
import android.content.Intent
import com.vonage.android.di.RetrofitModule.BASE_URL

fun Context.navigateToShare(roomName: String) {
    // build URL based on environment
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, "$BASE_URL/room/$roomName")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}
