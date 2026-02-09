package com.vonage.android.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.vonage.android.chat.ChatFeature
import com.vonage.android.screensharing.VonageScreenSharing
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VeraNotificationChannelRegistry @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val chatFeature: ChatFeature,
    private val screenSharing: VonageScreenSharing,
) {

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            )
            chatFeature.createNotificationChannel(manager)
            screenSharing.createNotificationChannel(manager)
            manager.createNotificationChannel(channel)
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