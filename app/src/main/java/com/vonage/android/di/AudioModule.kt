package com.vonage.android.di

import android.content.Context
import com.vonage.android.audio.AudioDeviceSelector
import com.vonage.android.audio.util.AudioFocusRequester
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager
import com.vonage.android.audio.data.AudioDeviceStore
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@EntryPoint
@InstallIn(SingletonComponent::class)
interface VeraAudioEntryPoint {
    fun audioDeviceSelector(): AudioDeviceSelector
}

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    @Singleton
    @Provides
    fun provideAudioDeviceStore(
        @ApplicationContext context: Context,
        bluetoothManager: VeraBluetoothManager,
    ): AudioDeviceStore =
        AudioDeviceStore(
            context = context,
            bluetoothManager = bluetoothManager,
        )

    @Singleton
    @Provides
    fun provideVeraBluetoothManager(
        @ApplicationContext context: Context,
    ): VeraBluetoothManager = VeraBluetoothManager(context)

    @Singleton
    @Provides
    fun provideAudioDeviceSelector(
        @ApplicationContext context: Context,
        audioDeviceStore: AudioDeviceStore,
    ): AudioDeviceSelector = AudioDeviceSelector(
        context = context,
        audioFocusRequester = AudioFocusRequester(),
        audioDeviceStore = audioDeviceStore,
    )
}
