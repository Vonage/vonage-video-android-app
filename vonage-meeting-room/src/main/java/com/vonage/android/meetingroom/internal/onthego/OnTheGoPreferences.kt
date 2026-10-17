package com.vonage.android.meetingroom.internal.onthego

/**
 * Stores whether the app may suggest On-the-Go mode when motion is detected.
 *
 * Listeners run synchronously on the thread that changes the value, so a
 * preference change stops motion detection before the setter returns.
 */
internal interface OnTheGoPreferences {
    var suggestWhenMoving: Boolean

    fun addListener(listener: (Boolean) -> Unit)

    fun removeListener(listener: (Boolean) -> Unit)

    fun close() {}
}
