package com.vonage.android.meetingroom.internal.onthego

/**
 * Integrator-facing configuration for On-the-Go mode.
 *
 * @param isEnabled Master switch. When false the host shows nothing and motion detection never starts.
 * @param autoSuggest When false, On-the-Go can only be entered manually.
 * @param suggestionTimeoutMs How long the suggestion banner stays up before it counts as "Not now".
 */
internal data class OnTheGoConfiguration(
    val isEnabled: Boolean = true,
    val autoSuggest: Boolean = true,
    val suggestionTimeoutMs: Long = DEFAULT_SUGGESTION_TIMEOUT_MS,
) {
    companion object {
        const val DEFAULT_SUGGESTION_TIMEOUT_MS = 10_000L
    }
}
