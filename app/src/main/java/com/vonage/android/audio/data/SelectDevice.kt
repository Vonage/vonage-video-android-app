package com.vonage.android.audio.data

import android.media.AudioManager
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager
import javax.inject.Inject

class SelectDevice @Inject constructor(
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

    fun activeDevice(): AudioDevice? {
        if (userSelectedDevice != null) {
            return userSelectedDevice
        } else {
            val availableDevices = getDevices()
            performSwitchTo(availableDevices[0])
            return availableDevices[0]
        }
    }

    private fun performSwitchTo(audioDevice: AudioDevice) {
        when (audioDevice.type) {
            AudioDeviceType.EARPIECE,
            AudioDeviceType.WIRED_HEADSET -> {
                bluetoothManager.stopBluetooth()
                audioManager.apply {
                    if (isBluetoothScoOn) {
                        stopBluetoothSco()
                    }
                    isBluetoothScoOn = false
                    isSpeakerphoneOn = false
                    mode = AudioManager.MODE_IN_COMMUNICATION
                }
            }

            AudioDeviceType.BLUETOOTH -> {
                bluetoothManager.startBluetooth()
                audioManager.apply {
                    isBluetoothScoOn = true
                    isSpeakerphoneOn = false
                    mode = AudioManager.MODE_IN_COMMUNICATION
                    startBluetoothSco()
                }
            }

            AudioDeviceType.SPEAKER -> {
                bluetoothManager.stopBluetooth()
                audioManager.apply {
                    isBluetoothScoOn = false
                    isSpeakerphoneOn = true
                    if (isBluetoothScoOn) {
                        stopBluetoothSco()
                    }
                    mode = AudioManager.MODE_IN_COMMUNICATION
                }
            }
        }
    }

    fun reset() {
        userSelectedDevice = null
    }
}
