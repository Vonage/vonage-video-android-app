package com.vonage.android.kotlin.ext

/**
 * Toggles a boolean value.
 *
 * Returns the logical negation of the boolean. Useful for toggling UI states
 * like camera/microphone enabled/disabled.
 *
 * @return The opposite boolean value
 */
fun Boolean.toggle() = !this
