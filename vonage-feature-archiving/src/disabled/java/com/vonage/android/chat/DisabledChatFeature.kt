package com.vonage.android.chat

import android.app.NotificationManager

@Suppress("EmptyFunctionBlock")
class DisabledChatFeature : ChatFeature {

    override fun createNotificationChannel(manager: NotificationManager) {

    }
}
