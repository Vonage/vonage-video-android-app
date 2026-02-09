package com.vonage.android.screensharing

import android.app.NotificationManager
import android.content.Intent
import android.media.projection.MediaProjection

@Suppress("EmptyFunctionBlock")
class DisabledScreensharing : VonageScreenSharing {

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