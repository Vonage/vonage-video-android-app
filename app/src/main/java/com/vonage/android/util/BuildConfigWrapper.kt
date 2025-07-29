package com.vonage.android.util

import android.os.Build

object BuildConfigWrapper {
    fun sdkVersion() = Build.VERSION.SDK_INT
}
