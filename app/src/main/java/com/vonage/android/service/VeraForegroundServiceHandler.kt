package com.vonage.android.service

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.registerReceiver
import com.vonage.android.notifications.VeraNotificationChannelRegistry.CallAction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VeraForegroundServiceHandler @Inject constructor(
    @param:ApplicationContext private val context: Context,
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

    fun startForegroundService(roomName: String) {
        val serviceIntent = Intent(context, VeraForegroundService::class.java)
        serviceIntent.putExtra(ROOM_INTENT_EXTRA_NAME, roomName)

        if (checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }

        listenCallActions()
    }

    fun stopForegroundService() {
        context.unregisterReceiver(callActionsReceiver)
        val serviceIntent = Intent(context, VeraForegroundService::class.java)
        context.stopService(serviceIntent)
    }

    private fun listenCallActions() {
        registerReceiver(context, callActionsReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    companion object {
        const val ROOM_INTENT_EXTRA_NAME = "room"
        const val HANG_UP_ACTION = "com.vonage.android.VERA_HANG_UP"
    }
}
