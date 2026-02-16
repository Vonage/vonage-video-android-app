package com.vonage.android

import android.app.Application
import com.vonage.android.notifications.VeraNotificationChannelRegistry
import com.vonage.logger.VonageLogger
import com.vonage.logger.interceptor.AndroidLogInterceptor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

val vonageLogger = VonageLogger.Builder()
    .addInterceptor(AndroidLogInterceptor())
    .build()

@HiltAndroidApp
open class MainApplication : Application() {

    @Inject
    lateinit var notificationChannelRegistry: VeraNotificationChannelRegistry

    override fun onCreate() {
        super.onCreate()

        notificationChannelRegistry.createNotificationChannels()
    }
}
