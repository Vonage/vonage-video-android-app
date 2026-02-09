package com.vonage.android.di

import android.content.Context
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.audioselector.VeraAudioDevice
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.reactions.ReactionSignalPlugin
import com.vonage.audioselector.AudioDeviceSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SdkModule {

    @Singleton
    @Provides
    fun provideAudioDeviceSelector(
        @ApplicationContext context: Context,
        @DefaultDispatcher dispatcher: CoroutineDispatcher,
    ): AudioDeviceSelector =
        AudioDeviceSelector(
            context = context,
            dispatcher = dispatcher,
        )

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
        reactionSignalPlugin: ReactionSignalPlugin,
        baseAudioDevice: VeraAudioDevice,
    ): VonageVideoClient =
        VonageVideoClient(
            context = context,
            baseAudioDevice = baseAudioDevice,
            signalPlugins = listOfNotNull(
                chatSignalPlugin,
                reactionSignalPlugin,
            )
        )

}
