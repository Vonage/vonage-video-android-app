package com.vonage.audioselector.util

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log

/**
 * Default audio focus requester
 * Using focus gain AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE by default
 * because the system shouldn't play any notifications and media playback should have paused
 *
 * Tracks focus changes so callers can tell a transient loss (the system gives focus back with
 * AUDIOFOCUS_GAIN) apart from a permanent one (focus is abandoned and must be requested again).
 * On Android 17+ a request made while the app is in the background without a while-in-use
 * foreground service fails with AUDIOFOCUS_REQUEST_FAILED; that failure is logged, not swallowed.
 */
internal class AudioFocusRequester(
    private val audioManager: AudioManager,
) {

    enum class FocusState {
        /** Focus was never requested, was lost permanently, or was abandoned. */
        NONE,
        HELD,
        LOST_TRANSIENT,
    }

    private var audioFocusRequest: AudioFocusRequest? = null
    private var onFocusChange: (FocusState) -> Unit = {}

    @Volatile
    var state: FocusState = FocusState.NONE
        private set

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        Log.d(TAG, "audio focus change $focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> updateState(FocusState.HELD)

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> updateState(FocusState.LOST_TRANSIENT)

            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss: the system will not give focus back, release the request so it
                // can be made again later (e.g. when the call UI returns to the foreground).
                releaseRequest()
                updateState(FocusState.NONE)
            }

            else -> Unit
        }
    }

    /** [listener] is called for focus changes reported by the system, not for [request]/[abandon]. */
    fun setOnFocusChangeListener(listener: (FocusState) -> Unit) {
        onFocusChange = listener
    }

    fun request(
        focusGain: Int = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
    ): Boolean {
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest = AudioFocusRequest
                .Builder(focusGain)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener(focusChangeListener)
                .build()
            audioFocusRequest = focusRequest
            audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_VOICE_CALL,
                focusGain,
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
        if (granted) {
            state = FocusState.HELD
        } else {
            Log.w(TAG, "audio focus request failed")
            releaseRequest()
            state = FocusState.NONE
        }
        return granted
    }

    fun abandon() {
        releaseRequest()
        state = FocusState.NONE
    }

    private fun releaseRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            audioFocusRequest = null
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusChangeListener)
        }
    }

    /** Updates the state for a system-reported focus change and notifies the listener. */
    private fun updateState(newState: FocusState) {
        if (state == newState) return
        state = newState
        onFocusChange(newState)
    }

    private companion object {
        const val TAG = "AudioFocusRequester"
    }
}
