package com.vonage.android.meetingroom.internal.screen.audio

import androidx.compose.runtime.Stable
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

@Stable
internal data class AudioDevicesState(
    val availableDevices: StateFlow<ImmutableList<AudioDevice>>,
    val activeDevice: StateFlow<AudioDevice?>,
    val selectDevice: (AudioDevice) -> Unit,
)
