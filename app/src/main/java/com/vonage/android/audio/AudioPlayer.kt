package com.vonage.android.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Stable
import com.vonage.android.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AudioPlayer @Inject constructor(
    @ApplicationContext context: Context
) {

    private val _audioPlayerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle)
    val audioPlayerState: StateFlow<AudioPlayerState> = _audioPlayerState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.sample)

    init {
        mediaPlayer?.setOnCompletionListener { _ ->
            stop()
        }
    }

    fun play() {
        mediaPlayer?.let { player ->
            player.start()
            _audioPlayerState.value = AudioPlayerState.Playing(
                durationMs = player.duration
            )
        }
    }

    fun stop() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            }
            player.seekTo(0)
        }
        _audioPlayerState.value = AudioPlayerState.Idle
    }

    fun toggle() {
        mediaPlayer?.let { player ->
            when (player.isPlaying) {
                true -> stop()
                false -> play()
            }
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _audioPlayerState.value = AudioPlayerState.Idle
    }
}

@Stable
sealed interface AudioPlayerState {
    data object Idle : AudioPlayerState
    data class Playing(
        val durationMs: Int,
    ) : AudioPlayerState
}
