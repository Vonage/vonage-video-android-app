package com.vonage.android.notifications

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
            chatFeature.createNotificationChannel(manager)
            screenSharing.createNotificationChannel(manager)
            // The in-call notification is posted by vonage-meeting-room on its own channel.
            // Remove the channel the app's old foreground service used on existing installs.
            manager.deleteNotificationChannel(LEGACY_FOREGROUND_SERVICE_CHANNEL_ID)
        }
    }

    private companion object {
        const val LEGACY_FOREGROUND_SERVICE_CHANNEL_ID = "VeraNotificationChannelRegistry"
    }
}