package com.vonage.android.audio

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
) : ViewModel() {

    val state = audioPlayer.audioPlayerState

    fun onSpeakerTestToggle() {
        audioPlayer.toggle()
    }

    fun stop() {
        audioPlayer.stop()
    }

    override fun onCleared() {
        audioPlayer.release()
        super.onCleared()
    }
}
