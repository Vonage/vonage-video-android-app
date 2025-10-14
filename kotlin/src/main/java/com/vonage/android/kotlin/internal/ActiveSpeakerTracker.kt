package com.vonage.android.kotlin.internal

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
    throttleTimeMs: Long = 2000L,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val subscriberAudioLevelsBySubscriberId: SubscriberAudioLevels = mutableMapOf()

    private val _activeSpeaker = MutableStateFlow(ActiveSpeakerInfo(null, 0F))

    private val _activeSpeakerChanges = MutableSharedFlow<ActiveSpeakerChangedPayload>()
    val activeSpeakerChanges: SharedFlow<ActiveSpeakerChangedPayload> = _activeSpeakerChanges.asSharedFlow()

    private val _calculateTrigger = MutableSharedFlow<Unit>()

    init {
        _calculateTrigger
            .debounce(throttleTimeMs)
            .onEach { internalCalculateActiveSpeaker() }
            .launchIn(coroutineScope)
    }

    fun onSubscriberDestroyed(subscriberId: String) {
        subscriberAudioLevelsBySubscriberId.remove(subscriberId)
        if (_activeSpeaker.value.streamId == subscriberId) {
            _activeSpeaker.value = ActiveSpeakerInfo(null, 0F)
        }
        calculateActiveSpeaker()
    }

    fun onSubscriberAudioLevelUpdated(streamId: String, movingAvg: Float) {
        subscriberAudioLevelsBySubscriberId[streamId] = movingAvg
        calculateActiveSpeaker()
    }

    private fun calculateActiveSpeaker() {
        _calculateTrigger.tryEmit(Unit)
    }

    private fun internalCalculateActiveSpeaker() {
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

        if (newActiveSpeaker.streamId != currentActiveSpeaker.streamId
            && newActiveSpeaker.movingAvg > ACTIVE_SPEAKER_AUDIO_LEVEL_THRESHOLD
        ) {
            val previousActiveSpeaker = currentActiveSpeaker.copy()
            _activeSpeaker.value = newActiveSpeaker

            val payload = ActiveSpeakerChangedPayload(previousActiveSpeaker, newActiveSpeaker)
            _activeSpeakerChanges.tryEmit(payload)
        }
    }

    private companion object {
        const val ACTIVE_SPEAKER_AUDIO_LEVEL_THRESHOLD = 0.2
    }
}
