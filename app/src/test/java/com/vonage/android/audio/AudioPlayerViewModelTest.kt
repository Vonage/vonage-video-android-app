package com.vonage.android.audio

import androidx.lifecycle.ViewModelStore
import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AudioPlayerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val audioPlayerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle)
    private val audioPlayer: AudioPlayer = mockk(relaxed = true) {
        every { audioPlayerState } returns this@AudioPlayerViewModelTest.audioPlayerState
    }

    private lateinit var sut: AudioPlayerViewModel

    @Before
    fun setUp() {
        sut = AudioPlayerViewModel(
            audioPlayer = audioPlayer,
        )
    }

    @Test
    fun `given viewmodel when created then state is idle`() = runTest {
        sut.state.test {
            assertEquals(AudioPlayerState.Idle, awaitItem())
        }
    }

    @Test
    fun `given viewmodel when speaker test toggled then delegates to audio player`() {
        sut.onSpeakerTestToggle()

        verify { audioPlayer.toggle() }
    }

    @Test
    fun `given viewmodel when stop then delegates to audio player`() {
        sut.stop()

        verify { audioPlayer.stop() }
    }

    @Test
    fun `given audio player is playing then viewmodel state reflects playing`() = runTest {
        val playing = AudioPlayerState.Playing(durationMs = 5000)
        audioPlayerState.value = playing

        sut.state.test {
            assertEquals(playing, awaitItem())
        }
    }

    @Test
    fun `given audio player transitions from playing to idle then viewmodel state reflects idle`() = runTest {
        audioPlayerState.value = AudioPlayerState.Playing(durationMs = 3000)

        sut.state.test {
            assertEquals(AudioPlayerState.Playing(durationMs = 3000), awaitItem())

            audioPlayerState.value = AudioPlayerState.Idle
            assertEquals(AudioPlayerState.Idle, awaitItem())
        }
    }

    @Test
    fun `given viewmodel when cleared then releases audio player`() {
        val store = ViewModelStore()
        store.put("AudioPlayerViewModel", sut)
        store.clear()

        verify { audioPlayer.release() }
    }
}
