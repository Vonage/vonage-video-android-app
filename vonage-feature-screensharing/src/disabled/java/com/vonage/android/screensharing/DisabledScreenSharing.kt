package com.vonage.android.screensharing

import android.app.NotificationManager
import android.content.Intent
import com.vonage.android.kotlin.model.CallFacade

@Suppress("EmptyFunctionBlock")
class DisabledScreenSharing : VonageScreenSharing {

    override fun startScreenSharing(
        intent: Intent,
        call: CallFacade,
        onStarted: () -> Unit,
        onStopped: () -> Unit
    ) {
    }

    override fun stopSharingScreen() {
    }

    override fun createNotificationChannel(manager: NotificationManager) {
    }
}