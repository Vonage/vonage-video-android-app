package com.vonage.android

import android.app.Application
import com.opentok.android.OpenTokConfig
import com.vonage.android.notifications.VeraNotificationChannelRegistry
import com.vonage.logger.DefaultVonageLogger
import com.vonage.logger.LogLevel
import com.vonage.logger.interceptor.FileLogInterceptor
import com.vonage.logger.interceptor.OpenTokLogcatInterceptor
import com.vonage.logger.vonageLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class MainApplication : Application() {

    @Inject
    lateinit var notificationChannelRegistry: VeraNotificationChannelRegistry

    override fun onCreate() {
        super.onCreate()

        enableOpenTokLogs()

        DefaultVonageLogger.init(
            context = this,
            retentionDays = FileLogInterceptor.DEFAULT_RETENTION_DAYS,
        )

        OpenTokLogcatInterceptor(logger = vonageLogger, minLogLevel = LogLevel.INFO).start()

        notificationChannelRegistry.createNotificationChannels()
    }

    private fun enableOpenTokLogs() {
        OpenTokConfig.setWebRTCLogs(true)
        OpenTokConfig.setOTKitLogs(true)
        OpenTokConfig.setJNILogs(true)
    }
}
