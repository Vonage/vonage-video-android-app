package com.vonage.android

import android.app.Application
import com.vonage.android.data.ClientLogsRepository
import com.vonage.android.logging.OpenTokLoggingController
import com.vonage.android.notifications.VeraNotificationChannelRegistry
import com.vonage.logger.DefaultVonageLogger
import com.vonage.logger.LogLevel
import com.vonage.logger.interceptor.FileLogInterceptor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class MainApplication : Application() {

    @Inject
    lateinit var notificationChannelRegistry: VeraNotificationChannelRegistry

    @Inject
    lateinit var clientLogsRepository: ClientLogsRepository

    override fun onCreate() {
        super.onCreate()

        val minLogLevel = LogLevel.INFO

        DefaultVonageLogger.init(
            context = this,
            retentionDays = FileLogInterceptor.DEFAULT_RETENTION_DAYS,
            minLogLevel = minLogLevel,
        )

        OpenTokLoggingController.apply(
            enabled = DefaultVonageLogger.isEnabled,
            minLogLevel = DefaultVonageLogger.currentMinLogLevel,
        )
        clientLogsRepository.warmUp()

        notificationChannelRegistry.createNotificationChannels()
    }
}
