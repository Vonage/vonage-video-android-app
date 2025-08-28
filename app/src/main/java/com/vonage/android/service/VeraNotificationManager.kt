package com.vonage.android.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat.checkSelfPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VeraNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun startForegroundService(roomName: String) {
        val serviceIntent = Intent(context, VeraForegroundService::class.java)
        serviceIntent.putExtra("room", roomName)

        if (checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }

    fun stopForegroundService() {
        val serviceIntent = Intent(context, VeraForegroundService::class.java)
        context.stopService(serviceIntent)
    }

    companion object {
        const val CHANNEL_ID: String = "MyForegroundService"
        const val CHANNEL_NAME: String = "Audio Foreground Service"
    }
}
