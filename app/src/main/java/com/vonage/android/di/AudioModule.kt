package com.vonage.android.di

import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioManager
import com.vonage.android.audio.AudioDeviceSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface VeraAudioEntryPoint {
    fun audioDeviceSelector(): AudioDeviceSelector
}

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Provides
    fun provideAudioManager(
        @ApplicationContext context: Context,
    ): AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    @Provides
    fun provideBluetoothManager(
        @ApplicationContext context: Context,
    ): BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

}
