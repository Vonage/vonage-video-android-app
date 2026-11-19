package com.vonage.audioselector.util

import android.media.AudioManager
import com.vonage.audioselector.util.AudioFocusRequester.FocusState
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Unit tests run with Build.VERSION.SDK_INT = 0, so this covers the pre-O (listener based) path.
@Suppress("DEPRECATION")
class AudioFocusRequesterTest {

    private val listenerSlot = slot<AudioManager.OnAudioFocusChangeListener>()
    private val audioManager: AudioManager = mockk(relaxed = true)
    private val changes = mutableListOf<FocusState>()
    private val sut = AudioFocusRequester(audioManager).apply {
        setOnFocusChangeListener { changes += it }
    }

    private fun givenRequestResult(result: Int) {
        every { audioManager.requestAudioFocus(capture(listenerSlot), any(), any()) } returns result
    }

    @Test
    fun `given granted request THEN focus is held and listener is not notified`() {
        givenRequestResult(AudioManager.AUDIOFOCUS_REQUEST_GRANTED)

        assertTrue(sut.request())

        assertEquals(FocusState.HELD, sut.state)
        assertTrue(changes.isEmpty())
        verify {
            audioManager.requestAudioFocus(
                any(),
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
            )
        }
    }

    @Test
    fun `given failed request THEN focus is none`() {
        givenRequestResult(AudioManager.AUDIOFOCUS_REQUEST_FAILED)

        assertFalse(sut.request())

        assertEquals(FocusState.NONE, sut.state)
    }

    @Test
    fun `given transient loss THEN state is lost transient and focus is kept`() {
        givenRequestResult(AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        sut.request()

        listenerSlot.captured.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)

        assertEquals(FocusState.LOST_TRANSIENT, sut.state)
        assertEquals(listOf(FocusState.LOST_TRANSIENT), changes)
        verify(exactly = 0) { audioManager.abandonAudioFocus(any()) }
    }

    @Test
    fun `given duck loss THEN state is lost transient`() {
        givenRequestResult(AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        sut.request()

        listenerSlot.captured.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)

        assertEquals(FocusState.LOST_TRANSIENT, sut.state)
    }

    @Test
    fun `given transient loss WHEN gain THEN focus is held again`() {
        givenRequestResult(AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        sut.request()
        listenerSlot.captured.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)

        listenerSlot.captured.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN)

        assertEquals(FocusState.HELD, sut.state)
        assertEquals(listOf(FocusState.LOST_TRANSIENT, FocusState.HELD), changes)
    }

    @Test
    fun `given permanent loss THEN focus is abandoned and state is none`() {
        givenRequestResult(AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        sut.request()

        listenerSlot.captured.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS)

        assertEquals(FocusState.NONE, sut.state)
        assertEquals(listOf(FocusState.NONE), changes)
        verify { audioManager.abandonAudioFocus(listenerSlot.captured) }
    }

    @Test
    fun `given held focus WHEN abandon THEN focus is released without notifying`() {
        givenRequestResult(AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        sut.request()

        sut.abandon()

        assertEquals(FocusState.NONE, sut.state)
        assertTrue(changes.isEmpty())
        verify { audioManager.abandonAudioFocus(listenerSlot.captured) }
    }
}
