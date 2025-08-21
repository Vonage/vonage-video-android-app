package com.vonage.android.audio

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.audio.ui.AudioDeviceList
import com.vonage.android.di.VeraAudioDeviceEntryPoint
import dagger.hilt.android.EntryPointAccessors

@ExperimentalMaterial3Api
@Composable
fun AudioDevicesHandler(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val audioDeviceSelector = rememberAudioDeviceSelector(context)
    LaunchedEffect(audioDeviceSelector) {
        audioDeviceSelector.init()
    }

    val availableDevices by audioDeviceSelector.availableDevices.collectAsStateWithLifecycle()
    val activeDevice by audioDeviceSelector.activeDevice.collectAsStateWithLifecycle()

    AudioDeviceList(
        availableDevices = availableDevices,
        activeDevice = activeDevice,
        selectDevice = { device ->
            onDismissRequest()
            audioDeviceSelector.selectDevice(device)
        },
    )
}

@Composable
fun rememberAudioDeviceSelector(context: Context) = remember(Unit) {
    val entryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        VeraAudioDeviceEntryPoint::class.java
    )
    val veraAudioDevice = entryPoint.veraAudioDevice()
    AudioDeviceSelector(context, veraAudioDevice)
}
