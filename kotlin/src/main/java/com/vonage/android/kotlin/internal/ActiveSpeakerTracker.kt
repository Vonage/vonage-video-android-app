package com.vonage.android.kotlin.internal

import android.os.Handler
import android.os.Looper
import android.util.Log

typealias SubscriberAudioLevels = MutableMap<String, Float>

data class ActiveSpeakerInfo(
    val streamId: String?,
    val movingAvg: Float
)

data class ActiveSpeakerChangedPayload(
    val previousActiveSpeaker: ActiveSpeakerInfo,
    val newActiveSpeaker: ActiveSpeakerInfo
)

interface ActiveSpeakerListener {
    fun onActiveSpeakerChanged(payload: ActiveSpeakerChangedPayload)
    fun onResetActiveSpeaker()
}

class ActiveSpeakerTracker(
    private val throttleTimeMs: Long = 2000L
) {
    private val subscriberAudioLevelsBySubscriberId: SubscriberAudioLevels = mutableMapOf()
    private var activeSpeaker: ActiveSpeakerInfo = ActiveSpeakerInfo(null, 0F)
    private var listener: ActiveSpeakerListener? = null

    private val handler = Handler(Looper.getMainLooper())
    private var calculateActiveSpeakerRunnable: Runnable? = null
    private var calculatePending = false

    fun setActiveSpeakerListener(listener: ActiveSpeakerListener) {
        this.listener = listener
    }

    fun onSubscriberDestroyed(subscriberId: String) {
        subscriberAudioLevelsBySubscriberId.remove(subscriberId)
        if (activeSpeaker.streamId == subscriberId) {
            activeSpeaker = ActiveSpeakerInfo(null, 0F)
        }
        calculateActiveSpeaker()
    }

    fun onSubscriberAudioLevelUpdated(streamId: String, movingAvg: Float) {
        subscriberAudioLevelsBySubscriberId[streamId] = movingAvg
        calculateActiveSpeaker()
    }

    private fun calculateActiveSpeaker() {
        if (calculatePending) return
        calculatePending = true
        if (calculateActiveSpeakerRunnable == null) {
            calculateActiveSpeakerRunnable = Runnable {
                _calculateActiveSpeaker()
                calculatePending = false
            }
        }
        handler.postDelayed(calculateActiveSpeakerRunnable!!, throttleTimeMs)
    }

    private fun _calculateActiveSpeaker() {
        var maxMovingAvg = 0F
        var maxSubscriberId: String? = null
        for ((subscriberId, movingAvg) in subscriberAudioLevelsBySubscriberId) {
            if (movingAvg > maxMovingAvg) {
                maxMovingAvg = movingAvg
                maxSubscriberId = subscriberId
            }
        }
        val newActiveSpeaker = ActiveSpeakerInfo(maxSubscriberId, maxMovingAvg)
        Log.d("YYY2", "newActiveSpeaker = ${newActiveSpeaker.streamId}")
        if (newActiveSpeaker.streamId != activeSpeaker.streamId && newActiveSpeaker.movingAvg > 0.2) {
            val previousActiveSpeaker = activeSpeaker.copy()
            activeSpeaker = newActiveSpeaker
            listener?.onActiveSpeakerChanged(
                ActiveSpeakerChangedPayload(previousActiveSpeaker, newActiveSpeaker)
            )
        }
    }
}
