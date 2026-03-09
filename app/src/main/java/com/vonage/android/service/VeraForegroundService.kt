package com.vonage.android.service

import android.Manifest.permission
import android.app.Notification
import android.app.Notification.CallStyle
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Person
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.vonage.android.R
import com.vonage.android.notifications.VeraNotificationChannelRegistry.Companion.CHANNEL_ID
import com.vonage.android.service.VeraForegroundServiceHandler.Companion.HANG_UP_ACTION
import com.vonage.android.service.VeraForegroundServiceHandler.Companion.ROOM_INTENT_EXTRA_NAME
import com.vonage.logger.vonageLogger

class VeraForegroundService : Service() {

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            vonageLogger.e(TAG, "Cannot use microphone on background without RECORD_AUDIO permission")
            stopSelf()
        }

        if (intent == null) {
            return START_REDELIVER_INTENT
        }

        val roomName = intent.extras?.getString(ROOM_INTENT_EXTRA_NAME)

        val notification = buildInCallNotification(roomName.orEmpty())

        ServiceCompat.startForeground(
            this,
            FOREGROUND_SERVICE_NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                FOREGROUND_SERVICE_TYPE_MICROPHONE or FOREGROUND_SERVICE_TYPE_CAMERA
            } else {
                0
            },
        )
        return START_STICKY
    }

    private fun buildInCallNotification(roomName: String): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // caller
            val caller = Person.Builder()
                .setName(roomName)
                .setImportant(true)
                .build()

            // hangUp intent
            val hangupIntent = Intent(HANG_UP_ACTION)
            val hangupPendingIntent = PendingIntent.getBroadcast(
                this,
                FOREGROUND_SERVICE_REQUEST_CODE,
                hangupIntent,
                FLAG_IMMUTABLE
            )

            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vonage)
                .setStyle(CallStyle.forOngoingCall(caller, hangupPendingIntent))
                .setOngoing(true)
                .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_DEFAULT)
                .build()
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vonage)
                .setOngoing(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_DEFAULT)
                .build()
        }
    }

    private companion object {
        const val FOREGROUND_SERVICE_NOTIFICATION_ID = 1
        const val FOREGROUND_SERVICE_REQUEST_CODE = 999
        const val TAG: String = "VeraForegroundService"
    }
}
