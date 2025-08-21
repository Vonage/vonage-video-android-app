package com.vonage.android.audio

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.audio.data.DefaultAudioDeviceStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AudioDeviceSelectorTest {

    private val context: Context = mockk(relaxed = true)
    private val audioDeviceStore: DefaultAudioDeviceStore = mockk()
    private val sut = AudioDeviceSelector(
        context = context,
        audioDeviceStore = audioDeviceStore,
    )

    private val headset = AudioDevice(1, AudioDeviceType.HEADSET)
    private val speaker = AudioDevice(2, AudioDeviceType.SPEAKER)

    @Test
    fun `given audio selector when init then return available devices`() {
        runTest {
            val audioDevices = listOf(
                headset,
                speaker,
            )
            every { audioDeviceStore.getDevices() } returns audioDevices
            every { audioDeviceStore.getActiveDevice() } returns null

            sut.availableDevices.test {
                sut.init()
                awaitItem() // initial state
                assertEquals(
                    audioDevices,
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `given audio selector when init then return active device`() = runTest {
        val audioDevices = listOf(
            headset,
            speaker,
        )
        every { audioDeviceStore.getDevices() } returns audioDevices
        every { audioDeviceStore.getActiveDevice() } returns speaker

        sut.activeDevice.test {
            sut.init()
            awaitItem() // initial state
            assertEquals(
                speaker,
                awaitItem()
            )
        }
    }

    @Test
    fun `given audio selector when select device success then return active device`() = runTest {
        val audioDevices = listOf(
            headset,
            speaker,
        )
        every { audioDeviceStore.getDevices() } returns audioDevices
        every { audioDeviceStore.getActiveDevice() } returnsMany listOf(
            speaker,
            headset,
        )
        every { audioDeviceStore.selectDevice(headset) } returns true

        sut.activeDevice.test {
            sut.init()
            awaitItem() // initial state
            assertEquals(
                speaker,
                awaitItem()
            )
            sut.selectDevice(headset)
            assertEquals(
                headset,
                awaitItem()
            )
        }
    }

    @Test
    fun `given audio selector when select device fails then do nothing`() = runTest {
        val audioDevices = listOf(
            headset,
            speaker,
        )
        every { audioDeviceStore.getDevices() } returns audioDevices
        every { audioDeviceStore.getActiveDevice() } returns headset
        every { audioDeviceStore.selectDevice(headset) } returns false

        sut.activeDevice.test {
            sut.init()
            awaitItem() // initial state
            awaitItem() // initial active device
            sut.selectDevice(headset)
            expectNoEvents()
        }
    }
}
