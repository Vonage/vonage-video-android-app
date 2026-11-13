package com.vonage.android.kotlin.ext

import android.app.ActivityManager

fun isInForeground(): Boolean =
    ActivityManager.RunningAppProcessInfo()
        .let { appProcessInfo ->
            ActivityManager.getMyMemoryState(appProcessInfo)
            appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }

fun isInBackground(): Boolean = !isInForeground()
