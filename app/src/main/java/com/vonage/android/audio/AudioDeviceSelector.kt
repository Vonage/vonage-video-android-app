package com.vonage.android.audio

import android.content.Context
import com.vonage.android.audio.data.AudioDeviceStore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AudioDeviceSelector @Inject constructor(
    private val context: Context,
    private val audioDeviceStore: AudioDeviceStore = AudioDeviceStore(context),
) {

    data class AudioDevice(
        val id: Int,
        val type: AudioDeviceType,
    )

    enum class AudioDeviceType {
        EARPIECE,
        BLUETOOTH,
        SPEAKER,
        HEADSET,
        UNKNOWN,
    }

    private val _availableDevices = MutableStateFlow<ImmutableList<AudioDevice>>(persistentListOf())
    val availableDevices: StateFlow<ImmutableList<AudioDevice>> = _availableDevices.asStateFlow()

    private val _activeDevice = MutableStateFlow<AudioDevice?>(null)
    val activeDevice: StateFlow<AudioDevice?> = _activeDevice.asStateFlow()

    fun init() {
        populateAvailableDevices()
        setActiveDevice()
    }

    fun selectDevice(device: AudioDevice) {
        audioDeviceStore.selectDevice(device)
            .takeIf { it }
            ?.let {
                _activeDevice.value = AudioDevice(
                    id = device.id,
                    type = device.type,
                )
            }
    }

    private fun populateAvailableDevices() {
        audioDeviceStore.getDevices()
            .let {
                _availableDevices.value = it.toImmutableList()
            }
    }

    private fun setActiveDevice() {
        audioDeviceStore.getActiveDevice()
            ?.let { device ->
                _activeDevice.value = device
            }
    }
}
