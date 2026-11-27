package com.vonage.android.meetingroom.internal.service

import android.Manifest.permission
import android.app.Notification
import android.app.Notification.CallStyle
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Person
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler.Companion.HANG_UP_ACTION
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler.Companion.HOST_ACTIVITY_INTENT_EXTRA_NAME
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler.Companion.ROOM_INTENT_EXTRA_NAME
import com.vonage.logger.vonageLogger

internal class MeetingRoomForegroundService : Service() {

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (!hasPermission(permission.RECORD_AUDIO)) {
            vonageLogger.e(TAG, "Cannot use microphone in background without RECORD_AUDIO permission")
            stopSelf()
            return START_NOT_STICKY
        }
        // A null intent only comes from a system restart after process death. The call is gone by
        // then, and a service restarted from the background would not get while-in-use access.
        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val roomName = intent.extras?.getString(ROOM_INTENT_EXTRA_NAME).orEmpty()
        val hostActivity = IntentCompat.getParcelableExtra(
            intent,
            HOST_ACTIVITY_INTENT_EXTRA_NAME,
            ComponentName::class.java,
        )
        val notification = buildInCallNotification(roomName, returnToCallIntent(hostActivity))

        ServiceCompat.startForeground(
            this,
            FOREGROUND_SERVICE_NOTIFICATION_ID,
            notification,
            foregroundServiceType(hasCameraPermission = hasPermission(permission.CAMERA)),
        )
        return START_NOT_STICKY
    }

    private fun hasPermission(name: String): Boolean =
        ContextCompat.checkSelfPermission(this, name) == PackageManager.PERMISSION_GRANTED

    /**
     * Brings the Activity showing the call back to the front, reusing its existing instance so the
     * call screen is not recreated (and expanding it if it is in picture-in-picture).
     */
    private fun returnToCallIntent(hostActivity: ComponentName?): PendingIntent? =
        hostActivity?.let { component ->
            val intent = Intent()
                .setComponent(component)
                .addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP,
                )
            PendingIntent.getActivity(
                this,
                RETURN_TO_CALL_REQUEST_CODE,
                intent,
                FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT,
            )
        }

    private fun buildInCallNotification(roomName: String, contentIntent: PendingIntent?): Notification =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val caller = Person.Builder()
                .setName(roomName)
                .setImportant(true)
                .build()
            val hangupIntent = Intent(HANG_UP_ACTION).setPackage(packageName)
            val hangupPendingIntent = PendingIntent.getBroadcast(
                this,
                FOREGROUND_SERVICE_REQUEST_CODE,
                hangupIntent,
                FLAG_IMMUTABLE,
            )
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vonage_meeting_room)
                .setStyle(CallStyle.forOngoingCall(caller, hangupPendingIntent))
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_DEFAULT)
                .build()
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vonage_meeting_room)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_DEFAULT)
                .build()
        }

    private companion object {
        const val CHANNEL_ID = "VonageMeetingRoomChannel"
        const val FOREGROUND_SERVICE_NOTIFICATION_ID = 1
        const val FOREGROUND_SERVICE_REQUEST_CODE = 999
        const val RETURN_TO_CALL_REQUEST_CODE = 998
        const val TAG: String = "MeetingRoomForegroundService"
    }
}

/**
 * Foreground service type for an active call. The camera type is only added when CAMERA is
 * granted: since Android 14 starting a camera-type service without it throws SecurityException.
 */
internal fun foregroundServiceType(
    hasCameraPermission: Boolean,
    sdkInt: Int = Build.VERSION.SDK_INT,
): Int = when {
    sdkInt < Build.VERSION_CODES.R -> 0
    hasCameraPermission -> FOREGROUND_SERVICE_TYPE_MICROPHONE or FOREGROUND_SERVICE_TYPE_CAMERA
    else -> FOREGROUND_SERVICE_TYPE_MICROPHONE
}
