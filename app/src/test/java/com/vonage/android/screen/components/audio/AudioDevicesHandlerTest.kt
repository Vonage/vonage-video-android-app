package com.vonage.android.screen.components.audio

import com.vonage.audioselector.AudioDeviceSelector
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import com.vonage.audioselector.AudioDeviceSelector.AudioDeviceType
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AudioDevicesHandlerTest {

    private val mockAudioDeviceSelector: AudioDeviceSelector = mockk(relaxed = true)
    private lateinit var handler: AudioDevicesHandler

    private val availableDevicesFlow = MutableStateFlow(persistentListOf<AudioDevice>())
    private val activeDeviceFlow = MutableStateFlow<AudioDevice?>(null)

    @Before
    fun setUp() {
        every { mockAudioDeviceSelector.availableDevices } returns availableDevicesFlow
        every { mockAudioDeviceSelector.activeDevice } returns activeDeviceFlow
        handler = AudioDevicesHandler(mockAudioDeviceSelector)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `start should delegate to audioDeviceSelector`() {
        handler.start()

        verify(exactly = 1) { mockAudioDeviceSelector.start() }
    }

    @Test
    fun `stop should delegate to audioDeviceSelector`() {
        handler.stop()

        verify(exactly = 1) { mockAudioDeviceSelector.stop() }
    }

    @Test
    fun `audioDevicesState should expose availableDevices from selector`() {
        val state = handler.audioDevicesState

        assertSame(availableDevicesFlow, state.availableDevices)
    }

    @Test
    fun `audioDevicesState should expose activeDevice from selector`() {
        val state = handler.audioDevicesState

        assertSame(activeDeviceFlow, state.activeDevice)
    }

    @Test
    fun `audioDevicesState selectDevice should delegate to selector`() {
        val device = AudioDevice(id = 1, type = AudioDeviceType.SPEAKER)

        handler.audioDevicesState.selectDevice(device)

        verify(exactly = 1) { mockAudioDeviceSelector.selectDevice(device) }
    }

    @Test
    fun `audioDevicesState should return same instance on multiple accesses`() {
        val first = handler.audioDevicesState
        val second = handler.audioDevicesState

        assertSame(first, second)
    }

    @Test
    fun `audioDevicesState should reflect updated available devices`() {
        val devices = persistentListOf(
            AudioDevice(id = 1, type = AudioDeviceType.SPEAKER),
            AudioDevice(id = 2, type = AudioDeviceType.BLUETOOTH),
        )
        availableDevicesFlow.value = devices

        val result = handler.audioDevicesState.availableDevices.value

        assertEquals(devices, result)
    }

    @Test
    fun `audioDevicesState should reflect updated active device`() {
        val device = AudioDevice(id = 3, type = AudioDeviceType.EARPIECE)
        activeDeviceFlow.value = device

        val result = handler.audioDevicesState.activeDevice.value

        assertEquals(device, result)
    }
}
