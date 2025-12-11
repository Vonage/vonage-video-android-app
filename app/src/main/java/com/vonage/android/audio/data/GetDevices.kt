package com.vonage.android.audio.data

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager.BluetoothState
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager.WiredState
import com.vonage.android.util.BuildConfigWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetDevices @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val bluetoothManager: VeraBluetoothManager,
    private val audioManager: AudioManager,
) {

    operator fun invoke(): List<AudioDevice> {
        val devices = mutableListOf<AudioDevice>()

        if (bluetoothManager.bluetoothState != BluetoothState.Disconnected) {
            devices.add(AudioDevice(id = 1, type = AudioDeviceType.BLUETOOTH))
        }

        if (bluetoothManager.wiredState == WiredState.Plugged) {
            devices.add(AudioDevice(id = 2, type = AudioDeviceType.WIRED_HEADSET))
        }

        if (hasSpeaker()) {
            devices.add(AudioDevice(id = 3, type = AudioDeviceType.SPEAKER))
        }

        if (hasEarpiece() && bluetoothManager.wiredState == WiredState.UnPlugged) {
            devices.add(AudioDevice(id = 4, type = AudioDeviceType.EARPIECE))
        }

        return devices
    }

    private fun hasEarpiece(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

    private fun hasSpeaker(): Boolean =
        if (BuildConfigWrapper.sdkVersion() >= Build.VERSION_CODES.M
            && context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)
        ) {
            audioManager
                .getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                .any { device -> device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
        } else {
            true
        }
}
