package com.vonage.android.audio

import android.content.Context
import android.util.Log
import com.vonage.android.audio.data.GetDevices
import com.vonage.android.audio.data.SelectDevice
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager
import com.vonage.android.audio.util.AudioFocusRequester
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioDeviceSelector @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val audioFocusRequester: AudioFocusRequester,
    private val bluetoothManager: VeraBluetoothManager,
    private val getDevicesCommand: GetDevices,
    private val currentDevice: SelectDevice,
) {

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    data class AudioDevice(
        val id: Int,
        val type: AudioDeviceType,
    )

    enum class AudioDeviceType {
        EARPIECE,
        BLUETOOTH,
        SPEAKER,
        WIRED_HEADSET,
    }

    private val _availableDevices = MutableStateFlow<ImmutableList<AudioDevice>>(persistentListOf())
    val availableDevices: StateFlow<ImmutableList<AudioDevice>> = _availableDevices.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = persistentListOf(),
    )

    private val _activeDevice = MutableStateFlow<AudioDevice?>(null)
    val activeDevice: StateFlow<AudioDevice?> = _activeDevice.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = null,
    )

    fun start() {
        Log.d("AudioDeviceSelector", "start")
        coroutineScope.launch {
            audioFocusRequester.request(context)
            bluetoothManager.onStart()

            populateAvailableDevices()
            setActiveDevice()

            bluetoothManager.bluetoothStates.collect { bluetoothState ->
                when (bluetoothState) {
                    is VeraBluetoothManager.BluetoothState.AudioConnected,
                    is VeraBluetoothManager.BluetoothState.Connected,
                    is VeraBluetoothManager.BluetoothState.Disconnected -> {
                        currentDevice.reset()
                        populateAvailableDevices()
                        setActiveDevice()
                    }
                }
            }
        }
    }

    fun stop() {
        Log.d("AudioDeviceSelector", "stop")
        coroutineScope.launch {
            bluetoothManager.onStop()
        }
    }

    fun selectDevice(device: AudioDevice) {
        coroutineScope.launch {
            currentDevice.userSelectDevice(device)
                .takeIf { it }
                ?.let {
                    _activeDevice.value = AudioDevice(
                        id = device.id,
                        type = device.type,
                    )
                }
        }
    }

    private fun populateAvailableDevices() {
        coroutineScope.launch {
            getDevicesCommand()
                .let { _availableDevices.value = it.toImmutableList() }
        }
    }

    private fun setActiveDevice() {
        coroutineScope.launch {
            currentDevice.activeDevice()
                ?.let { device -> _activeDevice.value = device }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 2000L
    }
}
