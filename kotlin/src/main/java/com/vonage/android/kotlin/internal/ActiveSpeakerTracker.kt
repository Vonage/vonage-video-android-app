package com.vonage.android.kotlin.internal

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample

typealias SubscriberAudioLevels = MutableMap<String, Float>

data class ActiveSpeakerInfo(
    val streamId: String?,
    val movingAvg: Float
)

data class ActiveSpeakerChangedPayload(
    val previousActiveSpeaker: ActiveSpeakerInfo,
    val newActiveSpeaker: ActiveSpeakerInfo
)

@OptIn(FlowPreview::class)
class ActiveSpeakerTracker(
    throttleTimeMs: Long = 1000L,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val subscriberAudioLevelsBySubscriberId: SubscriberAudioLevels = mutableMapOf()

    private val _activeSpeaker = MutableStateFlow(ActiveSpeakerInfo(null, 0F))

    private val _activeSpeakerChanges = MutableSharedFlow<ActiveSpeakerChangedPayload>()
    val activeSpeakerChanges: SharedFlow<ActiveSpeakerChangedPayload> = _activeSpeakerChanges.asSharedFlow()

    private val _calculateTrigger = MutableSharedFlow<Unit>()

    init {
        _calculateTrigger
            //.debounce(throttleTimeMs)
            .sample(throttleTimeMs)
            .onEach {
                Log.d("ActiveSpeakerTracker", "calculate onEach")
                internalCalculateActiveSpeaker()
            }
            .launchIn(coroutineScope)
    }

    suspend fun onSubscriberDestroyed(subscriberId: String) {
        subscriberAudioLevelsBySubscriberId.remove(subscriberId)
        if (_activeSpeaker.value.streamId == subscriberId) {
            _activeSpeaker.value = ActiveSpeakerInfo(null, 0F)
        }
        calculateActiveSpeaker()
    }

    suspend fun onSubscriberAudioLevelUpdated(streamId: String, movingAvg: Float) {
        Log.d("ActiveSpeakerTracker", "audio level updated $streamId -> $movingAvg")
        subscriberAudioLevelsBySubscriberId[streamId] = movingAvg
        calculateActiveSpeaker()
    }

    private suspend fun calculateActiveSpeaker() {
        _calculateTrigger.emit(Unit)
    }

    private suspend fun internalCalculateActiveSpeaker() {
        var maxMovingAvg = 0F
        var maxSubscriberId: String? = null
        for ((subscriberId, movingAvg) in subscriberAudioLevelsBySubscriberId) {
            if (movingAvg > maxMovingAvg) {
                maxMovingAvg = movingAvg
                maxSubscriberId = subscriberId
            }
        }
        val newActiveSpeaker = ActiveSpeakerInfo(maxSubscriberId, maxMovingAvg)
        val currentActiveSpeaker = _activeSpeaker.value

        Log.d("ActiveSpeakerTracker", "calculate ${currentActiveSpeaker.streamId} || ${newActiveSpeaker.streamId}")
//        if (newActiveSpeaker.streamId != currentActiveSpeaker.streamId
//            && newActiveSpeaker.movingAvg > ACTIVE_SPEAKER_AUDIO_LEVEL_THRESHOLD
//        ) {
        if (newActiveSpeaker.movingAvg > ACTIVE_SPEAKER_AUDIO_LEVEL_THRESHOLD
        ) {
            val previousActiveSpeaker = currentActiveSpeaker.copy()
            _activeSpeaker.value = newActiveSpeaker

            val payload = ActiveSpeakerChangedPayload(previousActiveSpeaker, newActiveSpeaker)
            val r = _activeSpeakerChanges.emit(payload)
            Log.d("ActiveSpeakerTracker", "emit change $r")
        }
    }

    private companion object {
        const val ACTIVE_SPEAKER_AUDIO_LEVEL_THRESHOLD = 0.1
    }
}
