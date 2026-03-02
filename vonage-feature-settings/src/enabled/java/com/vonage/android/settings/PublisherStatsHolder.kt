package com.vonage.android.settings

import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.model.PublisherState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton holder for publisher and subscriber stats that bridges the active call's stats
 * to UI components (e.g., Settings screen) that don't have direct access to the call.
 */
@Singleton
class PublisherStatsHolder @Inject constructor() {

    private val _senderStatsEnabled = MutableStateFlow(true)
    val senderStatsEnabled: StateFlow<Boolean> = _senderStatsEnabled.asStateFlow()

    private val _videoStats = MutableStateFlow<PublisherState.VideoStats?>(null)
    val videoStats: StateFlow<PublisherState.VideoStats?> = _videoStats.asStateFlow()

    private val _audioStats = MutableStateFlow<PublisherState.AudioStats?>(null)
    val audioStats: StateFlow<PublisherState.AudioStats?> = _audioStats.asStateFlow()

    private val _subscriberStats = MutableStateFlow<List<SubscriberStatsSnapshot>>(emptyList())
    val subscriberStats: StateFlow<List<SubscriberStatsSnapshot>> = _subscriberStats.asStateFlow()

    fun updateSenderStatsEnabled(enabled: Boolean) {
        _senderStatsEnabled.value = enabled
    }

    fun updateVideoStats(stats: PublisherState.VideoStats?) {
        _videoStats.value = stats
    }

    fun updateAudioStats(stats: PublisherState.AudioStats?) {
        _audioStats.value = stats
    }

    fun updateSubscriberStats(stats: List<SubscriberStatsSnapshot>) {
        _subscriberStats.value = stats
    }

    fun clear() {
        _senderStatsEnabled.value = true
        _videoStats.value = null
        _audioStats.value = null
        _subscriberStats.value = emptyList()
    }
}

data class SubscriberStatsSnapshot(
    val name: String,
    val videoStats: ParticipantState.SubscriberVideoStats?,
    val audioStats: ParticipantState.SubscriberAudioStats?,
)
