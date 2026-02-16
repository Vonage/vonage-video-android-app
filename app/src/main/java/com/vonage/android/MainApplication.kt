package com.vonage.android

import android.app.Application
import com.vonage.android.notifications.VeraNotificationChannelRegistry
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class MainApplication : Application() {

    @Inject
    lateinit var notificationChannelRegistry: VeraNotificationChannelRegistry

    override fun onCreate() {
        super.onCreate()

        notificationChannelRegistry.createNotificationChannels()
    }
}
