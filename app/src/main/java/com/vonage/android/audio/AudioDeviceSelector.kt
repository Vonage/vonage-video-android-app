package com.vonage.android.audio

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioDeviceSelector(
    private val context: Context,
) {

    data class AudioDevice(
        val id: Int,
        val type: AudioDeviceType,
        val audioDeviceInfo: AudioDeviceInfo? = null,
    )

    enum class AudioDeviceType {
        EARPIECE,
        BLUETOOTH,
        SPEAKER,
        HEADSET,
        UNKNOWN,
    }

    private val _availableDevices = MutableStateFlow<List<AudioDevice>>(emptyList())
    val availableDevices: StateFlow<List<AudioDevice>> = _availableDevices.asStateFlow()

    private val _activeDevice = MutableStateFlow<AudioDevice?>(null)
    val activeDevice: StateFlow<AudioDevice?> = _activeDevice.asStateFlow()

    lateinit var audioManager: AudioManager
    var selectedDevice: AudioDeviceInfo? = null

    fun bind() {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        populateAvailableDevices()
        setActiveDevice()
    }

    fun selectDevice(device: AudioDevice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            selectedDevice = device.audioDeviceInfo
            selectedDevice?.let {
                audioManager.setCommunicationDevice(it)
            }
            _activeDevice.value = AudioDevice(
                id = device.id,
                type = device.type,
                audioDeviceInfo = device.audioDeviceInfo,
            )
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

                AudioDeviceType.UNKNOWN -> Log.e(TAG, "Can not select unknown device")
            }
        }
    }

    private fun populateAvailableDevices() {
        audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .map { deviceInfo ->
                Log.d(TAG, "Device found: $deviceInfo")
                AudioDevice(
                    id = deviceInfo.id,
                    type = mapDeviceInfoTypeToAudioDeviceType(deviceInfo.type),
                    audioDeviceInfo = deviceInfo,
                )
            }
            .filter { it.type != AudioDeviceType.UNKNOWN }
            .also {
                _availableDevices.value = it
            }
    }

    private fun setActiveDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.communicationDevice?.let {
                _activeDevice.value = AudioDevice(
                    id = it.id,
                    type = mapDeviceInfoTypeToAudioDeviceType(it.type),
                    audioDeviceInfo = it,
                )
            }
        } else {
            if (audioManager.isSpeakerphoneOn) {
                _activeDevice.value = AudioDevice(
                    id = AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
                    type = mapDeviceInfoTypeToAudioDeviceType(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)
                )
            }
            if (audioManager.isBluetoothScoOn) {
                _activeDevice.value = AudioDevice(
                    id = AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                    type = mapDeviceInfoTypeToAudioDeviceType(AudioDeviceInfo.TYPE_BLUETOOTH_SCO)
                )
            }
        }
    }

    private fun mapDeviceInfoTypeToAudioDeviceType(deviceInfoType: Int): AudioDeviceType =
        when (deviceInfoType) {
            AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> AudioDeviceType.EARPIECE
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> AudioDeviceType.SPEAKER
            AudioDeviceInfo.TYPE_WIRED_HEADSET,
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> AudioDeviceType.HEADSET

            AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> AudioDeviceType.BLUETOOTH
            else -> {
                Log.d(TAG, "Unknown AudioDeviceType $deviceInfoType")
                AudioDeviceType.UNKNOWN
            }
        }

    companion object {
        const val TAG = "AudioDeviceSelector"
    }
}
