package com.vonage.android.audio.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import javax.inject.Inject

/**
 * Default audio focus requester
 * Using focus gain AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
 * because the system shouldn't play any notifications and media playback should have paused
 */
class AudioFocusRequester @Inject constructor() {

    private var audioFocusRequest: AudioFocusRequest? = null

    fun request(
        context: Context
    ): Boolean =
        request(audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)

    fun request(
        audioManager: AudioManager,
        focusGain: Int = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
    ): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest
                .Builder(focusGain)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAcceptsDelayedFocusGain(false)
                .build()
            audioFocusRequest?.let { focusRequest ->
                audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } ?: false
        } else {
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
}