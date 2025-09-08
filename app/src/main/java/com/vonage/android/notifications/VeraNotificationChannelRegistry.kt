package com.vonage.android.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VeraNotificationChannelRegistry @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            )
            val chatChannel = NotificationChannel(
                ChatSignalPlugin.Companion.NOTIFICATION_CHANNEL_ID,
                ChatSignalPlugin.Companion.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(chatChannel)
        }
    }

    companion object Companion {
        const val CHANNEL_ID = "VeraNotificationChannelRegistry"
        const val CHANNEL_NAME = "Vonage Foreground Service"
    }

    sealed interface CallAction {
        data object HangUp : CallAction
    }
}