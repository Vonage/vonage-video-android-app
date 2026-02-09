package com.vonage.android.screensharing

import android.app.NotificationManager
import android.content.Intent
import com.vonage.android.kotlin.model.CallFacade

interface VonageScreenSharing {

    fun startScreenSharing(
        intent: Intent,
        call: CallFacade,
        onStarted: () -> Unit,
        onStopped: () -> Unit,
    )

    fun stopSharingScreen()

    fun createNotificationChannel(manager: NotificationManager)

}
