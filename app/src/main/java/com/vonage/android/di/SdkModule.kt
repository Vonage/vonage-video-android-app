package com.vonage.android.di

import android.content.Context
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.internal.VeraAudioDevice
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.reactions.ReactionSignalPlugin
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

    @Singleton
    @Provides
    fun provideVonageVideoClient(
        @ApplicationContext context: Context,
        chatSignalPlugin: ChatSignalPlugin,
        baseAudioDevice: VeraAudioDevice,
    ): VonageVideoClient =
        VonageVideoClient(
            context = context,
            baseAudioDevice = baseAudioDevice,
            signalPlugins = listOfNotNull(
                chatSignalPlugin,
                ReactionSignalPlugin(),
            )
        )

}
