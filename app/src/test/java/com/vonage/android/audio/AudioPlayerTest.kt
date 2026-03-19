package com.vonage.android.audio

import android.content.Context
import android.media.MediaPlayer
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AudioPlayerTest {

    private val context: Context = mockk(relaxed = true)
    private val mediaPlayer: MediaPlayer = mockk(relaxed = true)
    private val onCompletionListenerSlot = slot<MediaPlayer.OnCompletionListener>()

    private lateinit var sut: AudioPlayer

    @Before
    fun setUp() {
        mockkStatic(MediaPlayer::class)
        every { MediaPlayer.create(any(), any<Int>()) } returns mediaPlayer
        every { mediaPlayer.setOnCompletionListener(capture(onCompletionListenerSlot)) } returns Unit

        sut = AudioPlayer(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(MediaPlayer::class)
    }

    @Test
    fun `given audio player when created then state is idle`() = runTest {
        sut.audioPlayerState.test {
            assertEquals(AudioPlayerState.Idle, awaitItem())
        }
    }

    @Test
    fun `given audio player when play then state is playing with duration`() = runTest {
        every { mediaPlayer.duration } returns 5000

        sut.play()

        sut.audioPlayerState.test {
            assertEquals(AudioPlayerState.Playing(durationMs = 5000), awaitItem())
        }

        verify { mediaPlayer.start() }
    }

    @Test
    fun `given audio player when stop then state is idle`() = runTest {
        sut.play()
        sut.stop()

        sut.audioPlayerState.test {
            assertEquals(AudioPlayerState.Idle, awaitItem())
        }

        verify { mediaPlayer.stop() }
        verify { mediaPlayer.prepare() }
    }

    @Test
    fun `given audio player is playing when toggle then stops`() = runTest {
        every { mediaPlayer.isPlaying } returns true

        sut.toggle()

        sut.audioPlayerState.test {
            assertEquals(AudioPlayerState.Idle, awaitItem())
        }

        verify { mediaPlayer.stop() }
    }

    @Test
    fun `given audio player is idle when toggle then plays`() = runTest {
        every { mediaPlayer.isPlaying } returns false
        every { mediaPlayer.duration } returns 3000

        sut.toggle()

        sut.audioPlayerState.test {
            assertEquals(AudioPlayerState.Playing(durationMs = 3000), awaitItem())
        }

        verify { mediaPlayer.start() }
    }

    @Test
    fun `given audio player when playback completes then state returns to idle`() = runTest {
        every { mediaPlayer.duration } returns 5000

        sut.play()

        assertTrue(onCompletionListenerSlot.isCaptured)
        onCompletionListenerSlot.captured.onCompletion(mediaPlayer)

        sut.audioPlayerState.test {
            assertEquals(AudioPlayerState.Idle, awaitItem())
        }
    }
}
