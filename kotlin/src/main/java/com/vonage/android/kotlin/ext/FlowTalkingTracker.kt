package com.vonage.android.kotlin.ext

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan

@OptIn(FlowPreview::class)
fun Flow<Float>.mapTalking(debounceMillis: Long = 100): Flow<Boolean> =
    scan(TalkingState()) { state, audioLevel ->
        state.update(audioLevel)
    }.map { it.isTalking }.debounce(debounceMillis).distinctUntilChanged()

/**
 * Internal state holder for talking state
 */
private data class TalkingState(
    val isTalking: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
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

const val SPEAKING_TIME_THRESHOLD = 100L
const val NOT_SPEAKING_TIME_THRESHOLD = 800L
const val SPEAKING_VOLUME = 0.1f
