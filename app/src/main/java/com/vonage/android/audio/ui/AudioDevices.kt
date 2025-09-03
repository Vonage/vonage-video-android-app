package com.vonage.android.audio.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.di.VeraAudioEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun AudioDevicesEffect(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val context = LocalContext.current
    val audioDeviceSelector = rememberAudioDeviceSelector(context)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                audioDeviceSelector.start()
            } else if (event == Lifecycle.Event.ON_STOP) {
                audioDeviceSelector.stop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun AudioDevices(
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val audioDeviceSelector = rememberAudioDeviceSelector(context)

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
    EntryPointAccessors
        .fromApplication(context.applicationContext, VeraAudioEntryPoint::class.java)
        .audioDeviceSelector()
}
