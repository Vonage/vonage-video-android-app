package com.vonage.android.audio.data

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.util.BuildConfigWrapper

class AudioDeviceStore(
    context: Context,
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager,
) {
    private var selectedDevice: AudioDeviceInfo? = null

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

    fun selectDevice(device: AudioDevice): Boolean {
        if (BuildConfigWrapper.sdkVersion() >= Build.VERSION_CODES.S) {
            selectedDevice = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                .firstOrNull { it.id == device.id }
            return selectedDevice?.let {
                audioManager.setCommunicationDevice(it)
            } ?: false
        } else {
            when (device.type) {
                AudioDeviceType.EARPIECE,
                AudioDeviceType.HEADSET -> {
                    audioManager.setSpeakerphoneOn(false)
                    audioManager.setBluetoothScoOn(false)
                }

                AudioDeviceType.BLUETOOTH -> {
                    audioManager.setBluetoothScoOn(true)
                }

                AudioDeviceType.SPEAKER -> {
                    audioManager.setSpeakerphoneOn(true)
                    audioManager.setBluetoothScoOn(false)
                }

                AudioDeviceType.UNKNOWN -> {
                    // Can not select unknown device
                }
            }
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
            audioManager.isSpeakerphoneOn or audioManager.isMicrophoneMute.not() -> {
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