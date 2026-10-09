package com.vonage.android.util

import android.content.Context
import android.content.Intent

fun Context.navigateToShare(roomName: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, "$DEEP_LINK_BASE_URL/room/$roomName")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}
