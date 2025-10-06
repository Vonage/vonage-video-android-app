package com.vonage.android.kotlin.internal

import com.opentok.android.Subscriber

private const val SPEAKING_TIME_THRESHOLD = 100L
private const val NOT_SPEAKING_TIME_THRESHOLD = 800L
private const val SPEAKING_VOLUME = 0.1

interface TalkingStateListener {
    fun onTalkingStateChanged(isTalking: Boolean)
}

class SubscriberTalkingTracker(
    subscriber: Subscriber?,
    private val isActiveSpeaker: Boolean = false
) {

    private var isTalking = false
    private var timestamp = System.currentTimeMillis()
    private var listener: TalkingStateListener? = null

    fun setTalkingStateListener(listener: TalkingStateListener) {
        this.listener = listener
    }

    fun onAudioLevelUpdated(audioLevel: Float) {
        handleAudioLevel(audioLevel)
    }

    private fun handleAudioLevel(audioLevel: Float) {
        val now = System.currentTimeMillis()

        if (audioLevel > SPEAKING_VOLUME) {
            if (!isTalking) {
                isTalking = true
                timestamp = now
            } else if (now - timestamp > SPEAKING_TIME_THRESHOLD) {
                isTalking = true
                timestamp = now
                listener?.onTalkingStateChanged(true)
            }
        } else if (isTalking && now - timestamp > NOT_SPEAKING_TIME_THRESHOLD) {
            isTalking = false
            listener?.onTalkingStateChanged(false)
        }
    }
}
