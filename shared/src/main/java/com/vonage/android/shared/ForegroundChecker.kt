package com.vonage.android.shared

import android.app.ActivityManager

class ForegroundChecker {

    fun isInBackground(): Boolean = !isInForeground()

    fun isInForeground(): Boolean =
        ActivityManager.RunningAppProcessInfo()
            .let { appProcessInfo ->
                ActivityManager.getMyMemoryState(appProcessInfo)
                appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
}
