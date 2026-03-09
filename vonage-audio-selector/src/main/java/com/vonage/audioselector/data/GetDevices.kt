package com.vonage.audioselector.data

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.vonage.audioselector.data.bluetooth.VeraBluetoothManager
import com.vonage.audioselector.AudioDeviceSelector

/**
 * Discovers available audio output devices based on current hardware state.
 * Returns devices in priority order: Bluetooth → Wired → Speaker → Earpiece.
 */
internal class GetDevices(
    private val context: Context,
    private val bluetoothManager: VeraBluetoothManager,
    private val audioManager: AudioManager,
) {

    operator fun invoke(): List<AudioDeviceSelector.AudioDevice> {
        val devices = mutableListOf<AudioDeviceSelector.AudioDevice>()

        if (bluetoothManager.bluetoothState != VeraBluetoothManager.BluetoothState.Disconnected) {
            devices.add(
                AudioDeviceSelector.AudioDevice(
                    id = 1,
                    type = AudioDeviceSelector.AudioDeviceType.BLUETOOTH
                )
            )
        }

        if (bluetoothManager.wiredState == VeraBluetoothManager.WiredState.Plugged) {
            devices.add(
                AudioDeviceSelector.AudioDevice(
                    id = 2,
                    type = AudioDeviceSelector.AudioDeviceType.WIRED_HEADSET
                )
            )
        }

        if (hasSpeaker()) {
            devices.add(
                AudioDeviceSelector.AudioDevice(
                    id = 3,
                    type = AudioDeviceSelector.AudioDeviceType.SPEAKER
                )
            )
        }

        if (hasEarpiece() && bluetoothManager.wiredState == VeraBluetoothManager.WiredState.UnPlugged) {
            devices.add(
                AudioDeviceSelector.AudioDevice(
                    id = 4,
                    type = AudioDeviceSelector.AudioDeviceType.EARPIECE
                )
            )
        }

        return devices
    }

    private fun hasEarpiece(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

    private fun hasSpeaker(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)
        ) {
            audioManager
                .getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                .any { device -> device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
        } else {
            true
        }
}
