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
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AudioDeviceSelectorTest {

    val testDispatcher = StandardTestDispatcher()

    private val context: Context = mockk(relaxed = true) {
        every { getSystemService(Context.AUDIO_SERVICE) } returns mockk<AudioManager>(relaxed = true)
        every { getSystemService(Context.BLUETOOTH_SERVICE) } returns mockk<BluetoothManager>(relaxed = true)
    }
    private val audioFocusRequester: AudioFocusRequester = mockk(relaxed = true)
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

    @BeforeEach
    fun setUpDispatchers() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `given audio selector when start then return available devices`() = runTest {
        val audioDevices = listOf(headset, speaker)
        every { audioFocusRequester.request() } returns true
        every { getDevices.invoke() } returns audioDevices
        givenBluetoothManger(VeraBluetoothManager.BluetoothState.Disconnected)
        givenCurrentActiveDevice(headset)

        sut.availableDevices.test {
            sut.start()
            awaitItem() // initial state
            assertEquals(audioDevices, awaitItem())
        }

        verify { audioFocusRequester.request() }
        verify { bluetoothManager.onStart() }
    }

    @Test
    fun `given audio selector when start then return active device`() = runTest {
        val audioDevices = listOf(headset, speaker)
        every { audioFocusRequester.request() } returns true
        every { getDevices.invoke() } returns audioDevices
        givenBluetoothManger(VeraBluetoothManager.BluetoothState.Disconnected)
        givenCurrentActiveDevice(headset)

        sut.activeDevice.test {
            sut.start()
            awaitItem() // initial state
            assertEquals(headset, awaitItem())
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

    @Test
    fun `given audio selector when stop then abandon audio focus`() = runTest {
        givenBluetoothManger(VeraBluetoothManager.BluetoothState.Connected)

        sut.stop()

        verify { audioFocusRequester.abandon() }
    }

    @Test
    fun `given not started when restoreAudioFocus then focus is not requested`() = runTest {
        every { audioFocusRequester.state } returns AudioFocusRequester.FocusState.NONE

        sut.restoreAudioFocus()
        testScheduler.advanceUntilIdle()

        verify(exactly = 0) { audioFocusRequester.request() }
    }

    @Test
    fun `given focus lost permanently when restoreAudioFocus then request focus and reapply device`() = runTest {
        givenStarted()
        every { audioFocusRequester.state } returns AudioFocusRequester.FocusState.NONE

        sut.restoreAudioFocus()
        testScheduler.advanceUntilIdle()

        verify(exactly = 2) { audioFocusRequester.request() }
        verify { currentDevice.reapplyActiveDevice() }
    }

    @Test
    fun `given focus held when restoreAudioFocus then focus is not requested again`() = runTest {
        givenStarted()
        every { audioFocusRequester.state } returns AudioFocusRequester.FocusState.HELD

        sut.restoreAudioFocus()
        testScheduler.advanceUntilIdle()

        verify(exactly = 1) { audioFocusRequester.request() }
    }

    @Test
    fun `given focus lost transiently when restoreAudioFocus then focus is not requested again`() = runTest {
        givenStarted()
        every { audioFocusRequester.state } returns AudioFocusRequester.FocusState.LOST_TRANSIENT

        sut.restoreAudioFocus()
        testScheduler.advanceUntilIdle()

        verify(exactly = 1) { audioFocusRequester.request() }
    }

    @Test
    fun `given started when focus is regained then reapply active device`() = runTest {
        val listener = givenStarted()

        listener(AudioFocusRequester.FocusState.HELD)
        testScheduler.advanceUntilIdle()

        verify { currentDevice.reapplyActiveDevice() }
        assertEquals(speaker, sut.activeDevice.value)
    }

    @Test
    fun `given started when focus is lost then device is not reapplied`() = runTest {
        val listener = givenStarted()

        listener(AudioFocusRequester.FocusState.LOST_TRANSIENT)
        listener(AudioFocusRequester.FocusState.NONE)
        testScheduler.advanceUntilIdle()

        verify(exactly = 0) { currentDevice.reapplyActiveDevice() }
    }

    @Test
    fun `given started when stop then bluetooth changes are no longer collected`() = runTest {
        val bluetoothStates = MutableStateFlow<VeraBluetoothManager.BluetoothState>(
            VeraBluetoothManager.BluetoothState.Disconnected,
        )
        givenStarted(bluetoothStates)

        sut.stop()
        bluetoothStates.value = VeraBluetoothManager.BluetoothState.Connected
        testScheduler.advanceUntilIdle()

        verify(exactly = 1) { currentDevice.reset() }
    }

    @Test
    fun `given started twice when bluetooth changes then devices are refreshed once`() = runTest {
        val bluetoothStates = MutableStateFlow<VeraBluetoothManager.BluetoothState>(
            VeraBluetoothManager.BluetoothState.Disconnected,
        )
        givenStarted(bluetoothStates)
        sut.start()
        testScheduler.advanceUntilIdle()

        bluetoothStates.value = VeraBluetoothManager.BluetoothState.Connected
        testScheduler.advanceUntilIdle()

        // one reset per start for the initial state, plus one for the change
        verify(exactly = 3) { currentDevice.reset() }
    }

    private fun givenStarted(
        bluetoothStates: MutableStateFlow<VeraBluetoothManager.BluetoothState> =
            MutableStateFlow(VeraBluetoothManager.BluetoothState.Disconnected),
    ): (AudioFocusRequester.FocusState) -> Unit {
        val listener = slot<(AudioFocusRequester.FocusState) -> Unit>()
        every { audioFocusRequester.setOnFocusChangeListener(capture(listener)) } returns Unit
        every { audioFocusRequester.request() } returns true
        every { getDevices.invoke() } returns listOf(headset, speaker)
        every { currentDevice.reapplyActiveDevice() } returns speaker
        givenBluetoothManger(VeraBluetoothManager.BluetoothState.Disconnected)
        every { bluetoothManager.bluetoothStates } returns bluetoothStates
        givenCurrentActiveDevice(headset)
        sut.start()
        testDispatcher.scheduler.advanceUntilIdle()
        return listener.captured
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
