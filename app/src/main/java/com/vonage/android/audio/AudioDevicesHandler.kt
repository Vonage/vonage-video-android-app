package com.vonage.android.audio

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.vonage.android.audio.ui.AudioDeviceList

@ExperimentalMaterial3Api
@Composable
fun AudioDevicesHandler(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val audioDeviceSelector = rememberAudioDeviceSelector(context)
    LaunchedEffect(audioDeviceSelector) {
        audioDeviceSelector.bind()
    }

    val availableDevices by audioDeviceSelector.availableDevices.collectAsState()
    val activeDevice by audioDeviceSelector.activeDevice.collectAsState()

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
    AudioDeviceSelector(context)
}
