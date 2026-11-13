package com.vonage.audioselector

import androidx.compose.runtime.Stable
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * Exposes an [AudioDeviceSelector] as [AudioDevicesState] for UI consumption.
 */
class AudioDevicesHandler(
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

/**
 * UI-facing snapshot of the audio device selection.
 */
@Stable
data class AudioDevicesState(
    val availableDevices: StateFlow<ImmutableList<AudioDevice>>,
    val activeDevice: StateFlow<AudioDevice?>,
    val selectDevice: (AudioDevice) -> Unit,
)
