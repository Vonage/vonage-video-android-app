package com.vonage.android.audio.data

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.util.BuildConfigWrapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AudioDeviceStoreTest {

    private val context: Context = mockk(relaxed = true)
    private val audioManager = buildAudioManagerMock()
    private val sut = DefaultAudioDeviceStore(
        context = context,
        audioManager = audioManager,
    )

    private val earpiece = AudioDevice(1, AudioDeviceType.EARPIECE)
    private val speaker = AudioDevice(2, AudioDeviceType.SPEAKER)
    private val headset = AudioDevice(3, AudioDeviceType.HEADSET)
    private val bluetooth = AudioDevice(7, AudioDeviceType.BLUETOOTH)

    @Test
    fun `given audio device store when getDevices return supported mapped devices`() = runTest {
        every { audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS) } returns arrayOf(
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_WIRED_HEADSET),
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER),
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE),
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_BLUETOOTH_SCO),
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_FM), // not supported
        )
        val audioDevices = sut.getDevices()

        assertEquals(
            audioDevices,
            listOf(
                headset,
                speaker,
                earpiece,
                bluetooth,
            )
        )
    }

    @Test
    fun `given audio device store when getActiveDevice return device`() = runTest {
        every { audioManager.communicationDevice } returns audioDeviceInfoMock(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)
        val device = sut.getActiveDevice()
        assertEquals(speaker, device)
    }

    @Test
    fun `given audio device store when selectDevice then delegate to audio manager`() = runTest {
        givenSdkVersionS()
        val earpieceDeviceInfo = audioDeviceInfoMock(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE)
        every { audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS) } returns arrayOf(
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_WIRED_HEADSET),
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER),
            earpieceDeviceInfo,
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_BLUETOOTH_SCO),
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_FM), // not supported
        )
        every { audioManager.setCommunicationDevice(any()) } returns true
        val ret = sut.selectDevice(earpiece)

        verify { audioManager.setCommunicationDevice(earpieceDeviceInfo) }
        verify { audioManager.clearCommunicationDevice() }
        assertTrue(ret)
    }

    @Test
    fun `given audio device store when selectDevice not available then return false`() = runTest {
        givenSdkVersionS()
        val earpieceDeviceInfo = audioDeviceInfoMock(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE)
        every { audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS) } returns arrayOf(
            audioDeviceInfoMock(AudioDeviceInfo.TYPE_FM), // not supported
        )
        val ret = sut.selectDevice(earpiece)

        verify(exactly = 0) { audioManager.setCommunicationDevice(earpieceDeviceInfo) }
        assertFalse(ret)
    }

    private fun givenSdkVersionS() {
        mockkObject(BuildConfigWrapper)
        every { BuildConfigWrapper.sdkVersion() } returns 31
    }

    private fun audioDeviceInfoMock(
        defaultId: Int,
    ) =
        mockk<AudioDeviceInfo> {
            every { id } returns defaultId
            every { type } returns defaultId
        }

    private fun buildAudioManagerMock() =
        mockk<AudioManager>(relaxed = true) {
            every { mode } returns AudioManager.MODE_NORMAL
            every { isMicrophoneMute } returns true
            every { isSpeakerphoneOn } returns true
            every { getDevices(AudioManager.GET_DEVICES_OUTPUTS) } returns emptyArray()
        }
}
