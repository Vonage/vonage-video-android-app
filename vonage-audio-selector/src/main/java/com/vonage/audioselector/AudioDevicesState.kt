package com.vonage.audioselector

import androidx.compose.runtime.Stable
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * UI-facing state snapshot of the audio device selector.
 *
 * Wraps the reactive state flows from [AudioDeviceSelector] together with a
 * selector callback so that Composables only need to hold a single stable reference.
 */
@Stable
data class AudioDevicesState(
    val availableDevices: StateFlow<ImmutableList<AudioDevice>>,
    val activeDevice: StateFlow<AudioDevice?>,
    val selectDevice: (AudioDevice) -> Unit,
)
