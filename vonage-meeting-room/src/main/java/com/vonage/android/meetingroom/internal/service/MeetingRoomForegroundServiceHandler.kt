package com.vonage.android.meetingroom.internal.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

internal class MeetingRoomForegroundServiceHandler(
    private val context: Context,
) {

    private val _actions = MutableSharedFlow<CallAction?>(extraBufferCapacity = 1)
    val actions: SharedFlow<CallAction?> = _actions

    private val filter = IntentFilter().apply { addAction(HANG_UP_ACTION) }
    private val callActionsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == HANG_UP_ACTION) {
                _actions.tryEmit(CallAction.HangUp)
            }
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(com.vonage.android.meetingroom.R.string.meeting_room_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH,
            )
            manager.createNotificationChannel(channel)
        }
    }

    fun startForegroundService(roomName: String) {
        val serviceIntent = Intent(context, MeetingRoomForegroundService::class.java).apply {
            putExtra(ROOM_INTENT_EXTRA_NAME, roomName)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            listenCallActions()
        }
    }

    fun stopForegroundService() {
        try {
            context.unregisterReceiver(callActionsReceiver)
        } catch (_: IllegalArgumentException) {
            // receiver was never registered — safe to ignore
        }
        val serviceIntent = Intent(context, MeetingRoomForegroundService::class.java)
        context.stopService(serviceIntent)
    }

    private fun listenCallActions() {
        ContextCompat.registerReceiver(context, callActionsReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    sealed interface CallAction {
        data object HangUp : CallAction
    }

    companion object {
        const val CHANNEL_ID = "VonageMeetingRoomChannel"
        const val ROOM_INTENT_EXTRA_NAME = "room"
        const val HANG_UP_ACTION = "com.vonage.android.meetingroom.HANG_UP"
    }
}
