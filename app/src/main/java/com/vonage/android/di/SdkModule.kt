package com.vonage.android.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.vonage.android.chat.ChatFeature
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.internal.VeraAudioDevice
import com.vonage.android.kotlin.signal.ReactionSignalPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SdkModule {

    @Singleton
    @Provides
    fun provideVeraAudioDevice(
        @ApplicationContext context: Context,
    ): VeraAudioDevice = VeraAudioDevice(context)

    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context,
    ): NotificationManagerCompat = NotificationManagerCompat.from(context)

    @Singleton
    @Provides
    fun provideVonageVideoClient(
        @ApplicationContext context: Context,
        chatFeature: ChatFeature,
        notificationManager: NotificationManager,
        baseAudioDevice: VeraAudioDevice,
    ): VonageVideoClient =
        VonageVideoClient(
            context = context,
            baseAudioDevice = baseAudioDevice,
            signalPlugins = listOfNotNull(
                chatFeature.getPlugin(context, notificationManager),
                ReactionSignalPlugin(),
            )
        )

}
