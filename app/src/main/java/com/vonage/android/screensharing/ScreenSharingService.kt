package com.vonage.android.screensharing

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class ScreenSharingService : Service() {

    private val binder: IBinder = LocalBinder()

    inner class LocalBinder : Binder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        return START_STICKY
    }

    @SuppressLint("InlinedApi")
    fun startForeground() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val chan = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(chan)

        val notificationId = 112233
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
        val notification =
            notificationBuilder
                .setOngoing(true)
                .setContentTitle("ScreenCapturerService is running in the foreground")
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()

        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
        )
    }

    fun endForeground() {
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    companion object {
        private const val CHANNEL_ID = "screen_capture"
        private const val CHANNEL_NAME = "Screen_Capture"
    }
}