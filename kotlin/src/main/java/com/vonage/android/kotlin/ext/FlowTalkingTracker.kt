package com.vonage.android.kotlin.ext

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan

/**
 * Transforms a flow of audio levels into a flow of talking states.
 *
 * Uses hysteresis to prevent rapid toggling between talking and not talking states.
 * A person is considered talking when audio level exceeds the threshold for a sustained
 * period, and stops talking only after being quiet for a longer period.
 *
 * Thresholds:
 * - Speaking volume: > 0.1
 * - Time to register as talking: > 100ms
 * - Time to register as not talking: > 800ms
 *
 * @param debounceMillis Debounce time to prevent rapid state changes (default 100ms)
 * @return Flow emitting true when talking, false when not talking
 */
@OptIn(FlowPreview::class)
fun Flow<Float>.mapTalking(debounceMillis: Long = 100): Flow<Boolean> =
    scan(TalkingState()) { state, audioLevel -> state.update(audioLevel) }
        .map { it.isTalking }
        .debounce(debounceMillis)
        .distinctUntilChanged()

/**
 * Internal state holder for talking state with hysteresis.
 *
 * Tracks whether a participant is currently talking and the timestamp of the last state change.
 * Implements time-based hysteresis to prevent rapid state changes.
 *
 * @property isTalking Current talking state
 * @property timestamp Time of last significant audio event in milliseconds
 */
private data class TalkingState(
    val isTalking: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
) {
    /**
     * Updates the talking state based on the current audio level.
     *
     * Uses different time thresholds for starting and stopping talking to create
     * hysteresis and prevent rapid toggling.
     *
     * @param audioLevel The current audio level (0.0 to 1.0)
     * @return Updated TalkingState
     */
    fun update(audioLevel: Float): TalkingState {
        val now = System.currentTimeMillis()
        return when {
            audioLevel > SPEAKING_VOLUME -> {
                if (now - timestamp > SPEAKING_TIME_THRESHOLD) {
                    copy(isTalking = true, timestamp = now)
                } else this
            }

            isTalking && now - timestamp > NOT_SPEAKING_TIME_THRESHOLD -> {
                copy(isTalking = false, timestamp = now)
            }

            else -> this
        }
    }
}

/** Time threshold to register as speaking (milliseconds) */
const val SPEAKING_TIME_THRESHOLD = 100L

/** Time threshold to register as not speaking (milliseconds) - longer to prevent flapping */
const val NOT_SPEAKING_TIME_THRESHOLD = 800L

/** Audio level threshold to be considered speaking (0.0 to 1.0) */
const val SPEAKING_VOLUME = 0.1f
