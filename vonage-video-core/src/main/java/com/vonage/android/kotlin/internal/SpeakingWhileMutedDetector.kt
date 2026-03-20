package com.vonage.android.kotlin.internal

import com.vonage.android.kotlin.internal.SpeakingWhileMutedDetector.Companion.AUDIO_LEVEL_THRESHOLD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan

/**
 * Detects when the user is speaking while their microphone is muted.
 *
 * Combines the mute state and audio level flows to determine if the user
 * is attempting to speak with a muted microphone. Uses hysteresis to avoid
 * flickering on/off rapidly.
 */
internal class SpeakingWhileMutedDetector(
    private val isMicEnabled: StateFlow<Boolean>,
    private val audioLevel: StateFlow<Float>,
) {

    /**
     * Flow that emits `true` when the user appears to be speaking while muted.
     *
     * The detection uses:
     * - Mute state: only triggers when mic is disabled
     * - Audio level threshold: requires sustained audio above [AUDIO_LEVEL_THRESHOLD]
     * - Consecutive sample counting with hysteresis to prevent rapid toggling
     */
    val isSpeakingWhileMuted: Flow<Boolean> =
        combine(isMicEnabled, audioLevel) { micEnabled, level ->
            !micEnabled && level > AUDIO_LEVEL_THRESHOLD
        }
            .scan(DetectionState()) { state, isLoudWhileMuted -> state.update(isLoudWhileMuted) }
            .map { it.isSpeakingWhileMuted }
            .distinctUntilChanged()

    private data class DetectionState(
        val isSpeakingWhileMuted: Boolean = false,
        val consecutiveCount: Int = 0,
    ) {
        fun update(isLoudWhileMuted: Boolean): DetectionState =
            if (isLoudWhileMuted) {
                val newCount = consecutiveCount + 1
                if (newCount >= TRIGGER_THRESHOLD) {
                    copy(isSpeakingWhileMuted = true, consecutiveCount = newCount)
                } else {
                    copy(consecutiveCount = newCount)
                }
            } else {
                if (isSpeakingWhileMuted) {
                    val newCount = consecutiveCount - 1
                    if (newCount <= RESET_THRESHOLD) {
                        copy(isSpeakingWhileMuted = false, consecutiveCount = 0)
                    } else {
                        copy(consecutiveCount = newCount)
                    }
                } else {
                    copy(consecutiveCount = 0)
                }
            }
    }

    companion object {
        /** Audio level above which the user is considered to be speaking */
        const val AUDIO_LEVEL_THRESHOLD = 0.1f

        /** Number of consecutive "loud while muted" samples to trigger the indicator */
        const val TRIGGER_THRESHOLD = 3

        /** Consecutive count at or below which the indicator resets */
        const val RESET_THRESHOLD = 0
    }
}
