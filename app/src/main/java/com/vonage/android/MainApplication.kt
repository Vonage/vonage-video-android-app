package com.vonage.android

import android.app.Application
import android.net.TrafficStats
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) { // could be moved to debug source set
            enableStrictMode()
        }
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
