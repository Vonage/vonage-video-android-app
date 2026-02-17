package com.vonage.audioselector

import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioManager
import app.cash.turbine.test
import com.vonage.audioselector.data.CurrentDevice
import com.vonage.audioselector.data.GetDevices
import com.vonage.audioselector.data.bluetooth.VeraBluetoothManager
import com.vonage.audioselector.util.AudioFocusRequester
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AudioDeviceSelectorTest {

    val testDispatcher = StandardTestDispatcher()

    private val context: Context = mockk(relaxed = true) {
        every { getSystemService(Context.AUDIO_SERVICE) } returns mockk<AudioManager>(relaxed = true)
        every { getSystemService(Context.BLUETOOTH_SERVICE) } returns mockk<BluetoothManager>(relaxed = true)
    }
    private val audioFocusRequester: AudioFocusRequester = mockk()
    private val bluetoothManager: VeraBluetoothManager = mockk()
    private val getDevices: GetDevices = mockk()
    private val currentDevice: CurrentDevice = mockk()
    private val sut = AudioDeviceSelector(
        context = context,
        audioFocusRequester = audioFocusRequester,
        bluetoothManager = bluetoothManager,
        getDevices = getDevices,
        currentDevice = currentDevice,
        dispatcher = testDispatcher,
    )

    private val headset = AudioDeviceSelector.AudioDevice(1, AudioDeviceSelector.AudioDeviceType.WIRED_HEADSET)
    private val speaker = AudioDeviceSelector.AudioDevice(2, AudioDeviceSelector.AudioDeviceType.SPEAKER)

    @Before
    fun setUpDispatchers() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `given audio selector when start then return available devices`() = runTest {
        val audioDevices = listOf(
            headset,
            speaker,
        )
        every { audioFocusRequester.request() } returns true
        every { getDevices.invoke() } returns audioDevices
        givenBluetoothManger(VeraBluetoothManager.BluetoothState.Disconnected)
        givenCurrentActiveDevice(headset)

        sut.availableDevices.test {
            sut.start()
            awaitItem() // initial state
            assertEquals(
                audioDevices,
                awaitItem()
            )
        }

        verify { audioFocusRequester.request() }
        verify { bluetoothManager.onStart() }
    }

    @Test
    fun `given audio selector when start then return active device`() = runTest {
        val audioDevices = listOf(
            headset,
            speaker,
        )
        every { audioFocusRequester.request() } returns true
        every { getDevices.invoke() } returns audioDevices
        givenBluetoothManger(VeraBluetoothManager.BluetoothState.Disconnected)
        givenCurrentActiveDevice(headset)

        sut.activeDevice.test {
            sut.start()
            awaitItem() // initial state
            assertEquals(
                headset,
                awaitItem()
            )
        }

        verify { audioFocusRequester.request() }
        verify { bluetoothManager.onStart() }
    }

    @Test
    fun `given audio selector when select device then state is updated`() = runTest {
        every { currentDevice.userSelectDevice(speaker) } returns Unit

        sut.activeDevice.test {
            sut.selectDevice(speaker)
            awaitItem() // initial state
            assertEquals(speaker, awaitItem())
        }
    }

    @Test
    fun `given audio selector when stop then stop bluetooth manager`() = runTest {
        givenBluetoothManger(VeraBluetoothManager.BluetoothState.Connected)

        sut.stop()

        verify { bluetoothManager.onStop() }
    }

    private fun givenCurrentActiveDevice(audioDevice: AudioDeviceSelector.AudioDevice) {
        every { currentDevice.getCurrentActiveDevice() } returns audioDevice
        every { currentDevice.reset() } returns Unit
    }

    private fun givenBluetoothManger(bluetoothState: VeraBluetoothManager.BluetoothState) {
        every { bluetoothManager.onStart() } returns Unit
        every { bluetoothManager.onStop() } returns Unit
        every { bluetoothManager.bluetoothStates } returns MutableStateFlow(bluetoothState)
    }
}
