package com.vonage.android.meetingroom.internal.screen.audio

import androidx.compose.runtime.Stable
import com.vonage.audioselector.AudioDeviceSelector
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

internal class MeetingRoomAudioDevicesHandler(
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
internal data class AudioDevicesState(
    val availableDevices: StateFlow<ImmutableList<AudioDevice>>,
    val activeDevice: StateFlow<AudioDevice?>,
    val selectDevice: (AudioDevice) -> Unit,
)
