package com.vonage.android.screen.components.audio

import androidx.compose.runtime.Stable
import com.vonage.audioselector.AudioDeviceSelector
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AudioDevicesHandler @Inject constructor(
    private val audioDeviceSelector: AudioDeviceSelector,
) {

    val audioDevicesState: AudioDevicesState by lazy {
        AudioDevicesState(
            availableDevices = audioDeviceSelector.availableDevices,
            activeDevice = audioDeviceSelector.activeDevice,
            selectDevice = { device -> audioDeviceSelector.selectDevice(device) },
        )
    }

    fun start() {
        audioDeviceSelector.start()
    }

    fun stop() {
        audioDeviceSelector.stop()
    }
}

@Stable
data class AudioDevicesState(
    val availableDevices: StateFlow<ImmutableList<AudioDevice>>,
    val activeDevice: StateFlow<AudioDevice?>,
    val selectDevice: (AudioDevice) -> Unit,
)
