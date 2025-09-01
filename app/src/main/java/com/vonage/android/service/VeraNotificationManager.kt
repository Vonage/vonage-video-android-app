package com.vonage.android.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.registerReceiver
import com.vonage.android.kotlin.signal.ChatSignalPlugin.Companion.NOTIFICATION_CHANNEL_ID
import com.vonage.android.kotlin.signal.ChatSignalPlugin.Companion.NOTIFICATION_CHANNEL_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VeraNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val callActionsLister: CallActionsListener,
) {
    private val filter = IntentFilter().apply { addAction(HANG_UP_ACTION) }
    private val callActionsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == HANG_UP_ACTION) {
                callActionsLister.onHangUp()
            }
        }
    }

    fun listenCallActions(): VeraNotificationManager {
        registerReceiver(context, callActionsReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        return this
    }

    fun createNotificationChannel(): VeraNotificationManager {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                IMPORTANCE_HIGH,
            )
            val chatChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_DEFAULT,
            )
            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(chatChannel)
        }
        return this
    }

    fun startForegroundService(roomName: String): VeraNotificationManager {
        val serviceIntent = Intent(context, VeraForegroundService::class.java)
        serviceIntent.putExtra("room", roomName)

        if (checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
        return this
    }

    fun stopForegroundService(): VeraNotificationManager {
        callActionsLister.stop()
        registerReceiver(context, null, filter, ContextCompat.RECEIVER_EXPORTED)
        val serviceIntent = Intent(context, VeraForegroundService::class.java)
        context.stopService(serviceIntent)
        return this
    }

    companion object {
        const val CHANNEL_ID: String = "VeraNotificationManager"
        const val CHANNEL_NAME: String = "Vonage Foreground Service"
    }
}
