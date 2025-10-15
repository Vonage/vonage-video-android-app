package com.vonage.android.chat

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.shared.ForegroundChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ChatModule {

    @Provides
    fun provideChatFeature(): ChatFeature =
        EnabledChatFeature()

    @Provides
    fun provideChatNotifications(
        @ApplicationContext context: Context,
    ): ChatNotifications = EnabledChatNotifications(
        context = context,
        notificationManager = NotificationManagerCompat.from(context),
    )

    @Provides
    fun provideChatSignalPlugin(
        notifications: ChatNotifications,
    ): ChatSignalPlugin =
        EnabledChatSignalPlugin(
            foregroundChecker = ForegroundChecker(),
            chatNotifications = notifications,
        )
}
