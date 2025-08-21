package com.vonage.android.audio.data

import com.vonage.android.audio.AudioDeviceSelector.AudioDevice

interface AudioDeviceStore {
    fun getDevices(): List<AudioDevice>
    fun getActiveDevice(): AudioDevice?
    fun selectDevice(device: AudioDevice): Boolean
}
