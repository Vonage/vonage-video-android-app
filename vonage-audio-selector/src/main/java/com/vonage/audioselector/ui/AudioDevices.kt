package com.vonage.audioselector.ui

import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.audioselector.AudioDeviceSelector
import com.vonage.audioselector.data.CurrentDevice
import com.vonage.audioselector.data.GetDevices
import com.vonage.audioselector.data.bluetooth.VeraBluetoothManager
import com.vonage.audioselector.util.AudioFocusRequester
import kotlinx.coroutines.Dispatchers

@Composable
fun AudioDevicesEffect() {
    if (LocalInspectionMode.current) return

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
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
fun rememberAudioDeviceSelector(context: Context): AudioDeviceSelector {
    val bluetoothManager = VeraBluetoothManager(
        context = context,
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager,
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager,
    )
    val audioDeviceManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val getDevices = GetDevices(
        context = context,
        bluetoothManager = bluetoothManager,
        audioManager = audioDeviceManager,
    )
    return AudioDeviceSelector(
        audioFocusRequester = AudioFocusRequester(
            audioManager = audioDeviceManager,
        ),
        bluetoothManager = bluetoothManager,
        getDevicesCommand = getDevices,
        currentDevice = CurrentDevice(
            bluetoothManager = bluetoothManager,
            audioManager = audioDeviceManager,
            getDevices = getDevices,
        ),
        dispatcher = Dispatchers.Default,
    )
}
