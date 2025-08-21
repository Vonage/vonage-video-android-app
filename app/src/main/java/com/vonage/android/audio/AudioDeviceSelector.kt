package com.vonage.android.audio

import android.content.Context
import android.util.Log
import com.vonage.android.audio.data.AudioDeviceStore
import com.vonage.android.audio.util.AudioFocusRequester
import com.vonage.android.kotlin.internal.VeraAudioDevice.Companion.TAG
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AudioDeviceSelector @Inject constructor(
    private val context: Context,
    private val audioDeviceStore: AudioDeviceStore,
    private val audioFocusRequester: AudioFocusRequester,
) {

    data class AudioDevice(
        val id: Int,
        val type: AudioDeviceType,
        val name: String = "",
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

    fun start() {
        if (audioFocusRequester.request(context)) {
            Log.d(TAG, "Audio Focus request GRANTED !")
        } else {
            Log.e(TAG, "Audio Focus request DENIED !")
        }
        audioDeviceStore.start()

        populateAvailableDevices()
        setActiveDevice()
    }

    fun stop() {
        audioDeviceStore.stop()
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
