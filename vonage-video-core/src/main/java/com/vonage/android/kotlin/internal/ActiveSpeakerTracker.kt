package com.vonage.android.kotlin.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample

/** Type alias for mapping subscriber IDs to their audio levels */
typealias SubscriberAudioLevels = MutableMap<String, Float>

/**
 * Information about an active speaker.
 *
 * @property streamId The stream ID of the speaker, or null if no one is speaking
 * @property movingAvg The moving average audio level
 */
data class ActiveSpeakerInfo(
    val streamId: String?,
    val movingAvg: Float
)

/**
 * Payload for active speaker change events.
 *
 * @property previousActiveSpeaker The previously active speaker
 * @property newActiveSpeaker The newly active speaker
 */
data class ActiveSpeakerChangedPayload(
    val previousActiveSpeaker: ActiveSpeakerInfo,
    val newActiveSpeaker: ActiveSpeakerInfo
)

/**
 * Tracks active speaker based on audio levels from all participants.
 *
 * Continuously monitors audio levels and determines the active speaker based on
 * a threshold. Emits events when the active speaker changes.
 *
 * @param throttleTimeMs Minimum time between speaker change calculations (default 1000ms)
 * @param coroutineScope Scope for running audio level calculations
 */
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
            .sample(throttleTimeMs)
            .onEach { internalCalculateActiveSpeaker() }
            .launchIn(coroutineScope)
    }

    /**
     * Notifies the tracker that a subscriber has been removed.
     *
     * @param subscriberId The ID of the removed subscriber
     */
    suspend fun onSubscriberDestroyed(subscriberId: String) {
        subscriberAudioLevelsBySubscriberId.remove(subscriberId)
        if (_activeSpeaker.value.streamId == subscriberId) {
            _activeSpeaker.value = ActiveSpeakerInfo(null, 0F)
        }
        calculateActiveSpeaker()
    }

    /**
     * Updates the audio level for a subscriber.
     *
     * @param streamId The subscriber's stream ID
     * @param movingAvg The moving average audio level
     */
    suspend fun onSubscriberAudioLevelUpdated(streamId: String, movingAvg: Float) {
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

        if (newActiveSpeaker.streamId != currentActiveSpeaker.streamId
            && newActiveSpeaker.movingAvg > ACTIVE_SPEAKER_AUDIO_LEVEL_THRESHOLD) {
            val previousActiveSpeaker = currentActiveSpeaker.copy()
            _activeSpeaker.value = newActiveSpeaker

            val payload = ActiveSpeakerChangedPayload(previousActiveSpeaker, newActiveSpeaker)
            _activeSpeakerChanges.emit(payload)
        }
    }

    private companion object {
        const val ACTIVE_SPEAKER_AUDIO_LEVEL_THRESHOLD = 0.1
    }
}
