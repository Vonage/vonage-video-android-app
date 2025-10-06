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
        TrafficStats.setThreadStatsTag(10000)
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
}
