package com.vonage.android.audio.data

import android.media.AudioManager
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.audio.data.bluetooth.VeraBluetoothManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CurrentDeviceTest {

    private val bluetoothManager: VeraBluetoothManager = mockk(relaxed = true)
    private val audioManager: AudioManager = mockk(relaxed = true)
    private val getDevices: GetDevices = mockk()
    private val sut = CurrentDevice(
        bluetoothManager = bluetoothManager,
        audioManager = audioManager,
        getDevices = getDevices,
    )

    private val bluetooth = AudioDevice(1, AudioDeviceType.BLUETOOTH)
    private val wiredHeadset = AudioDevice(2, AudioDeviceType.WIRED_HEADSET)
    private val earpiece = AudioDevice(3, AudioDeviceType.EARPIECE)
    private val speaker = AudioDevice(4, AudioDeviceType.SPEAKER)

    @Test
    fun `given no user selected device when getCurrentActiveDevice returns first available device`() = runTest {
        every { getDevices.invoke() } returns listOf(wiredHeadset, speaker)
        assertEquals(
            wiredHeadset,
            sut.getCurrentActiveDevice(),
        )
    }

    @Test
    fun `given no user selected device when getCurrentActiveDevice returns null when no available devices`() = runTest {
        every { getDevices.invoke() } returns listOf()
        assertEquals(
            null,
            sut.getCurrentActiveDevice(),
        )
    }

    @Test
    fun `given user selected returns user selected`() = runTest {
        sut.userSelectDevice(speaker)
        assertEquals(
            speaker,
            sut.getCurrentActiveDevice(),
        )
    }

    @Test
    fun `given default audio device when user select then returns user selected`() = runTest {
        every { getDevices.invoke() } returns listOf(wiredHeadset, speaker)
        sut.getCurrentActiveDevice()
        sut.userSelectDevice(speaker)
        assertEquals(
            speaker,
            sut.getCurrentActiveDevice(),
        )
    }

    @Test
    fun `given default audio device when user select and reset then returns default`() = runTest {
        every { getDevices.invoke() } returns listOf(wiredHeadset, speaker)
        sut.getCurrentActiveDevice()
        sut.userSelectDevice(speaker)
        sut.reset()
        assertEquals(
            wiredHeadset,
            sut.getCurrentActiveDevice(),
        )
    }

    @Test
    fun `should change to bluetooth`() = runTest {
        sut.userSelectDevice(bluetooth)

        verify { bluetoothManager.startBluetooth() }
        verify { audioManager.isSpeakerphoneOn = false }
    }

    @Test
    fun `should change to wired headset`() = runTest {
        sut.userSelectDevice(wiredHeadset)

        verify { bluetoothManager.stopBluetooth() }
        verify { audioManager.isSpeakerphoneOn = false }
    }

    @Test
    fun `should change to earpiece`() = runTest {
        sut.userSelectDevice(earpiece)

        verify { bluetoothManager.stopBluetooth() }
        verify { audioManager.isSpeakerphoneOn = false }
    }

    @Test
    fun `should change to speaker`() = runTest {
        sut.userSelectDevice(speaker)

        verify { bluetoothManager.stopBluetooth() }
        verify { audioManager.isSpeakerphoneOn = true }
    }
}