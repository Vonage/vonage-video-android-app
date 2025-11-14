package com.vonage.android

import android.app.Application
import com.vonage.android.notifications.VeraNotificationChannelRegistry
import android.net.TrafficStats
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var notificationChannelRegistry: VeraNotificationChannelRegistry

    override fun onCreate() {
        super.onCreate()

        notificationChannelRegistry.createNotificationChannels()
        if (BuildConfig.DEBUG) { // could be moved to debug source set
//            enableStrictMode()
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
