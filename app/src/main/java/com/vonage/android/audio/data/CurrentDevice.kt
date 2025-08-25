package com.vonage.android.audio.data

import android.media.AudioManager
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager
import javax.inject.Inject

class CurrentDevice @Inject constructor(
    private val bluetoothManager: VeraBluetoothManager,
    private val audioManager: AudioManager,
    private val getDevices: GetDevices,
) {

    private var userSelectedDevice: AudioDevice? = null

    fun userSelectDevice(device: AudioDevice): Boolean {
        userSelectedDevice = device
        performSwitchTo(device)
        return true
    }

    fun getCurrentActiveDevice(): AudioDevice? {
        if (userSelectedDevice != null) {
            return userSelectedDevice
        } else {
            val availableDevices = getDevices()
            availableDevices.firstOrNull()?.let { firstDevice ->
                performSwitchTo(firstDevice)
                return firstDevice
            }
            return null
        }
    }

    private fun performSwitchTo(audioDevice: AudioDevice) {
        when (audioDevice.type) {
            AudioDeviceType.EARPIECE,
            AudioDeviceType.WIRED_HEADSET -> {
                bluetoothManager.stopBluetooth()
                audioManager.apply {
                    isSpeakerphoneOn = false
                    mode = AudioManager.MODE_IN_COMMUNICATION
                }
            }

            AudioDeviceType.BLUETOOTH -> {
                bluetoothManager.startBluetooth()
                audioManager.apply {
                    isSpeakerphoneOn = false
                    mode = AudioManager.MODE_IN_COMMUNICATION
                }
            }

            AudioDeviceType.SPEAKER -> {
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
