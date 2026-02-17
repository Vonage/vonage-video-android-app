package com.vonage.android.screensharing.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class ScreenSharingService : Service() {

    private val binder: IBinder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForeground() {
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            } else {
                0
            }
        )
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

    class LocalBinder : Binder()

    companion object {
        const val NOTIFICATION_ID = 112233
        const val NOTIFICATION_CHANNEL_ID = "screen_capture"
        const val NOTIFICATION_CHANNEL_NAME = "Screen_Capture"

        fun intent(context: Context) = Intent(context, ScreenSharingService::class.java)
    }
}
