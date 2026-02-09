package com.vonage.android.screensharing

import android.app.NotificationManager
import android.content.Intent
import com.vonage.android.kotlin.model.CallFacade

interface VonageScreenSharing {

    fun bind(call: CallFacade)

    fun startScreenSharing(
        intent: Intent,
        onStarted: () -> Unit,
        onStopped: () -> Unit
    )

    fun stopSharingScreen()

    fun createNotificationChannel(manager: NotificationManager)

}
