package com.vonage.audioselector.util

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build

/**
 * Default audio focus requester
 * Using focus gain AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE by default
 * because the system shouldn't play any notifications and media playback should have paused
 */
class AudioFocusRequester constructor(
    private val audioManager: AudioManager,
) {

    private var audioFocusRequest: AudioFocusRequest? = null

    fun request(
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
