package com.vonage.android.service

import android.Manifest.permission
import android.app.Notification
import android.app.Notification.CallStyle
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Person
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.vonage.android.MainActivity
import com.vonage.android.R
import com.vonage.android.di.RetrofitModule.BASE_URL
import com.vonage.android.service.VeraNotificationManager.Companion.CHANNEL_ID

class VeraForegroundService : Service() {

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Cannot use microphone on background without RECORD_AUDIO permission")
            stopSelf()
        }

        if (intent == null) {
            return START_REDELIVER_INTENT
        }

        val roomName = intent.extras?.getString("room")

        val notification = buildInCallNotification(roomName.orEmpty())

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID_MIC,
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
        // deeplink intent
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "$BASE_URL/room/$roomName".toUri(),
            this,
            MainActivity::class.java
        )
        val deepLinkPendingIntent: PendingIntent? = PendingIntent.getActivity(
            this,
            998,
            deepLinkIntent,
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
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
                REQUEST_CODE,
                hangupIntent,
                FLAG_IMMUTABLE
            )

            Notification.Builder(this, CHANNEL_ID)
                .setContentIntent(deepLinkPendingIntent)
                .setSmallIcon(R.drawable.ic_vonage)
                .setStyle(CallStyle.forOngoingCall(caller, hangupPendingIntent))
                .setOngoing(true)
                .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
                .build()
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(deepLinkPendingIntent)
                .setSmallIcon(R.drawable.ic_vonage)
                .setOngoing(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()
        }
    }

    private companion object {
        const val NOTIFICATION_ID_MIC = 1
        const val REQUEST_CODE = 999
        const val TAG: String = "VeraForegroundService"
    }
}
