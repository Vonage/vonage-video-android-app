package com.vonage.android.screen.components.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AudioDevicesMenu(
    audioDevicesState: AudioDevicesState,
    onDismissRequest: () -> Unit,
) {
    val availableDevices by audioDevicesState.availableDevices.collectAsStateWithLifecycle()
    val activeDevice by audioDevicesState.activeDevice.collectAsStateWithLifecycle()

    AudioDeviceList(
        availableDevices = availableDevices,
        activeDevice = activeDevice,
        selectDevice = { device ->
            onDismissRequest()
            audioDevicesState.selectDevice(device)
        },
    )
}
