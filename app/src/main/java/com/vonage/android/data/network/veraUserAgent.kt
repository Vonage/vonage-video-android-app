package com.vonage.android.data.network

import android.os.Build
import com.vonage.android.BuildConfig

/** `User-Agent` sent by the app's backend requests. */
fun veraUserAgent(): String = "VeraNativeAndroid/${BuildConfig.VERSION_NAME} android ${Build.VERSION.RELEASE}"
