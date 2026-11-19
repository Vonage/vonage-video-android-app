package com.vonage.audioselector.data

import android.media.AudioManager
import com.vonage.audioselector.data.bluetooth.VeraBluetoothManager
import com.vonage.audioselector.AudioDeviceSelector

/**
 * Manages the current active audio device selection and routing configuration.
 */
internal class CurrentDevice(
    private val bluetoothManager: VeraBluetoothManager,
    private val audioManager: AudioManager,
    private val getDevices: GetDevices,
) {

    private var userSelectedDevice: AudioDeviceSelector.AudioDevice? = null

    fun userSelectDevice(device: AudioDeviceSelector.AudioDevice) {
        userSelectedDevice = device
        performSwitchTo(device)
    }

    fun getCurrentActiveDevice(): AudioDeviceSelector.AudioDevice? =
        if (userSelectedDevice != null) {
            userSelectedDevice
        } else {
            val availableDevices = getDevices()
            availableDevices.firstOrNull()?.let { firstDevice ->
                performSwitchTo(firstDevice)
                return firstDevice
            }
            null
        }

    /**
     * Routes audio to the active device again, e.g. after another app took audio focus and changed
     * the audio mode or speakerphone state.
     */
    fun reapplyActiveDevice(): AudioDeviceSelector.AudioDevice? =
        userSelectedDevice?.also { performSwitchTo(it) } ?: getCurrentActiveDevice()

    private fun performSwitchTo(audioDevice: AudioDeviceSelector.AudioDevice) {
        when (audioDevice.type) {
            AudioDeviceSelector.AudioDeviceType.EARPIECE,
            AudioDeviceSelector.AudioDeviceType.WIRED_HEADSET -> {
                bluetoothManager.stopBluetooth()
                audioManager.apply {
                    isSpeakerphoneOn = false
                    mode = AudioManager.MODE_IN_COMMUNICATION
                }
            }

            AudioDeviceSelector.AudioDeviceType.BLUETOOTH -> {
                bluetoothManager.startBluetooth()
                audioManager.apply {
                    isSpeakerphoneOn = false
                    mode = AudioManager.MODE_IN_COMMUNICATION
                }
            }

            AudioDeviceSelector.AudioDeviceType.SPEAKER -> {
                bluetoothManager.stopBluetooth()
                audioManager.apply {
                    isSpeakerphoneOn = true
                    mode = AudioManager.MODE_IN_COMMUNICATION
                }
            }
        }
    }

    fun reset() {
        userSelectedDevice = null
    }
}
