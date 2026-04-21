package com.vonage.android.screen.components.audio

import com.vonage.audioselector.AudioDeviceSelector
import com.vonage.audioselector.AudioDevicesState
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
