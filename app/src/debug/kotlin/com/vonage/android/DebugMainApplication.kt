package com.vonage.android

import android.net.TrafficStats
import android.os.Build
import android.os.StrictMode
import android.os.strictmode.UntaggedSocketViolation
import android.util.Log

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
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        // OkHttp connects on its own internal threads, which carry no TrafficStats tag,
                        // and the app does not use per-tag traffic stats.
                        penaltyListener({ it.run() }) { violation ->
                            if (violation !is UntaggedSocketViolation) {
                                Log.d(STRICT_MODE_TAG, "StrictMode policy violation", violation)
                            }
                        }
                    } else {
                        penaltyLog()
                    }
                }
                .build()
        )
    }

    private companion object {
        const val APP_THREAD_TAG = 10000
        const val STRICT_MODE_TAG = "StrictMode"
    }
}
