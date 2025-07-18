package com.vonage.android.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.navigateToSystemPermissions() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.setData(uri)
    startActivity(intent)
}
