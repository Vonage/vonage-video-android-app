package com.vonage.android.meetingroom.internal.util

/**
 * Lightweight holder for the current Activity context, scoped to the meeting room session.
 * Replaces Hilt's [ActivityContextProvider] without requiring DI.
 */
internal class ActivityContextHolder {

    private var currentContext: android.content.Context? = null

    fun setActivityContext(context: android.content.Context) {
        currentContext = context
    }

    fun requireActivityContext(): android.content.Context =
        currentContext ?: error("Activity context not set in ActivityContextHolder")

    fun clearActivityContext() {
        currentContext = null
    }
}
