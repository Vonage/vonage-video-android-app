package com.vonage.android.audio.data

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager
import com.vonage.android.util.BuildConfigWrapper
import javax.inject.Inject

class AudioDeviceStore @Inject constructor(
    context: Context,
    private val bluetoothManager: VeraBluetoothManager,
) {

    private var selectedDevice: AudioDeviceInfo? = null
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun start() {
        bluetoothManager.onStart()
    }

    fun stop() {
        bluetoothManager.onStop()
    }

    fun getDevices(): List<AudioDevice> =
        audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .map { deviceInfo ->
                AudioDevice(
                    id = deviceInfo.id,
                    type = deviceInfo.type.toAudioDeviceType(),
                )
            }
            .filter { it.type != AudioDeviceType.UNKNOWN }

    @SuppressLint("NewApi")
    fun getActiveDevice(): AudioDevice? =
        if (BuildConfigWrapper.sdkVersion() >= Build.VERSION_CODES.S) {
            getActiveDeviceCommunicationDevice()
        } else {
            getActiveDeviceDeprecated()
        }

    @SuppressLint("NewApi")
    fun selectDevice(device: AudioDevice): Boolean {
        Log.d("VeraAudioDeviceStore", "select device ${device.type}")
        return if (BuildConfigWrapper.sdkVersion() >= Build.VERSION_CODES.S) {
            selectDeviceAndroidSOrHigher(device)
        } else {
            selectDeviceFallback(device)
        }
    }

    private fun selectDeviceAndroidSOrHigher(device: AudioDevice): Boolean {
        selectedDevice = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .firstOrNull { it.id == device.id }
        return selectedDevice?.let { deviceInfo ->
            val result = audioManager.setCommunicationDevice(deviceInfo)
            if (!result) {
                Log.e("VeraAudioDeviceStore", "error while changing to $deviceInfo")
                false
            }
            when (device.type) {
                AudioDeviceType.SPEAKER,
                AudioDeviceType.EARPIECE,
                AudioDeviceType.HEADSET -> this@AudioDeviceStore.bluetoothManager.userDisableBluetooth()

                AudioDeviceType.BLUETOOTH -> this@AudioDeviceStore.bluetoothManager.userEnableBluetooth()
                else -> {}
            }
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            true
        } ?: false
    }

    private fun selectDeviceFallback(device: AudioDevice): Boolean {
        when (device.type) {
            AudioDeviceType.EARPIECE,
            AudioDeviceType.HEADSET -> {
                bluetoothManager.userDisableBluetooth()
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
                bluetoothManager.userEnableBluetooth()
                audioManager.apply {
                    isBluetoothScoOn = true
                    isSpeakerphoneOn = false
                    mode = AudioManager.MODE_IN_COMMUNICATION
                    startBluetoothSco()
                }
            }

            AudioDeviceType.SPEAKER -> {
                bluetoothManager.userDisableBluetooth()
                audioManager.apply {
                    isBluetoothScoOn = false
                    isSpeakerphoneOn = true
                    if (isBluetoothScoOn) {
                        stopBluetoothSco()
                    }
                    mode = AudioManager.MODE_IN_COMMUNICATION
                }
            }

            else -> {}
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getActiveDeviceCommunicationDevice(): AudioDevice? =
        audioManager.communicationDevice?.let {
            AudioDevice(
                id = it.id,
                type = it.type.toAudioDeviceType(),
            )
        }

    private fun getActiveDeviceDeprecated(): AudioDevice? =
        when {
            audioManager.isSpeakerphoneOn -> {
                AudioDevice(
                    id = AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
                    type = AudioDeviceInfo.TYPE_BUILTIN_SPEAKER.toAudioDeviceType(),
                )
            }

            audioManager.isBluetoothScoOn -> {
                AudioDevice(
                    id = AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                    type = AudioDeviceInfo.TYPE_BLUETOOTH_SCO.toAudioDeviceType(),
                )
            }

            else -> null
        }

    private fun Int.toAudioDeviceType(): AudioDeviceType =
        when (this) {
            AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> AudioDeviceType.EARPIECE
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> AudioDeviceType.SPEAKER
            AudioDeviceInfo.TYPE_WIRED_HEADSET,
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> AudioDeviceType.HEADSET

            AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> AudioDeviceType.BLUETOOTH
            else -> AudioDeviceType.UNKNOWN
        }
}
