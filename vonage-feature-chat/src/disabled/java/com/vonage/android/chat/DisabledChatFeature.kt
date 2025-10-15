package com.vonage.android.chat

import android.app.NotificationManager

class DisabledChatFeature : ChatFeature {

    override fun createNotificationChannel(manager: NotificationManager) {

    }
}
