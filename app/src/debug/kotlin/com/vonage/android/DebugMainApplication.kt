package com.vonage.android

import android.net.TrafficStats
import android.os.StrictMode

class DebugMainApplication : MainApplication() {

    override fun onCreate() {
        super.onCreate()

        enableStrictMode()
    }

    private fun enableStrictMode() {
        TrafficStats.setThreadStatsTag(APP_THREAD_TAG)
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .build()
        )
    }

    private companion object {
        const val APP_THREAD_TAG = 10000
    }
}