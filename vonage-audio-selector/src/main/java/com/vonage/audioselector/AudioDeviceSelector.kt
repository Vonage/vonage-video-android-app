package com.vonage.audioselector

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.BLUETOOTH_SERVICE
import android.media.AudioManager
import android.util.Log
import androidx.compose.runtime.Stable
import com.vonage.audioselector.data.CurrentDevice
import com.vonage.audioselector.data.GetDevices
import com.vonage.audioselector.data.bluetooth.VeraBluetoothManager
import com.vonage.audioselector.util.AudioFocusRequester
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AudioDeviceSelector {

    private val coroutineScope: CoroutineScope

    private val audioManager: AudioManager
    private val bluetoothManager: BluetoothManager
    private val veraBluetoothManager: VeraBluetoothManager
    private val audioFocusRequester: AudioFocusRequester
    private val getDevices: GetDevices
    private val currentDevice: CurrentDevice

    constructor(
        context: Context,
        dispatcher: CoroutineDispatcher,
    ) {
        coroutineScope = CoroutineScope(dispatcher)
        audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        veraBluetoothManager = VeraBluetoothManager(
            context = context,
            audioManager = audioManager,
            bluetoothManager = bluetoothManager,
        )
        audioFocusRequester = AudioFocusRequester(
            audioManager = audioManager,
        )
        getDevices = GetDevices(
            context = context,
            bluetoothManager = veraBluetoothManager,
            audioManager = audioManager,
        )
        currentDevice = CurrentDevice(
            bluetoothManager = veraBluetoothManager,
            audioManager = audioManager,
            getDevices = getDevices,
        )
    }

    // internal constructor for testing purposes
    internal constructor(
        context: Context,
        dispatcher: CoroutineDispatcher,
        audioFocusRequester: AudioFocusRequester,
        bluetoothManager: VeraBluetoothManager,
        getDevices: GetDevices,
        currentDevice: CurrentDevice,
    ) {
        this.coroutineScope = CoroutineScope(dispatcher)
        this.audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        this.bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        this.veraBluetoothManager = bluetoothManager
        this.audioFocusRequester = audioFocusRequester
        this.getDevices = getDevices
        this.currentDevice = currentDevice
    }

    @Stable
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
    val availableDevices: StateFlow<ImmutableList<AudioDevice>> = _availableDevices

    private val _activeDevice = MutableStateFlow<AudioDevice?>(null)
    val activeDevice: StateFlow<AudioDevice?> = _activeDevice

    fun start() {
        Log.d(TAG, "start")
        coroutineScope.launch {
            audioFocusRequester.request()
            veraBluetoothManager.onStart()

            populateAvailableDevices()
            setActiveDevice()

            veraBluetoothManager.bluetoothStates.collect { _ ->
                currentDevice.reset()
                populateAvailableDevices()
                setActiveDevice()
            }
        }
    }

    fun stop() {
        Log.d(TAG, "stop")
        veraBluetoothManager.onStop()
    }

    fun selectDevice(device: AudioDevice) {
        currentDevice.userSelectDevice(device)
            .let { _activeDevice.value = device }
    }

    private fun populateAvailableDevices() {
        getDevices()
            .let {
                Log.d(TAG, "available devices $it")
                _availableDevices.value = it.toImmutableList()
            }
    }

    private fun setActiveDevice() {
        currentDevice.getCurrentActiveDevice()
            ?.let { device ->
                Log.d(TAG, "set active device $device")
                _activeDevice.value = device
            }
    }

    private companion object {
        const val TAG = "AudioDeviceSelector"
    }
}
