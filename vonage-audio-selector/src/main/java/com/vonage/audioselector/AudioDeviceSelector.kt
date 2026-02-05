package com.vonage.audioselector

import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.compose.runtime.Immutable
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AudioDeviceSelector(
    private val context: Context,
    dispatcher: CoroutineDispatcher,
) {

    private val appContext = context.applicationContext
    private val coroutineScope = CoroutineScope(dispatcher)

    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val bluetoothManager = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val veraBluetoothManager: VeraBluetoothManager = VeraBluetoothManager(
        context = appContext,
        audioManager = audioManager,
        bluetoothManager = bluetoothManager,
    )
    private val audioFocusRequester = AudioFocusRequester(
        audioManager = audioManager,
    )
    private val getDevices = GetDevices(
        context = appContext,
        bluetoothManager = veraBluetoothManager,
        audioManager = audioManager,
    )
    private val currentDevice = CurrentDevice(
        bluetoothManager = veraBluetoothManager,
        audioManager = audioManager,
        getDevices = getDevices,
    )

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
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
