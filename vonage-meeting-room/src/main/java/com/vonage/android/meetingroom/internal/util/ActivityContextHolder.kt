package com.vonage.android.meetingroom.internal.util

import java.lang.ref.WeakReference

/**
 * Lightweight holder for the current Activity context, scoped to the meeting room session.
 * Replaces Hilt's [ActivityContextProvider] without requiring DI.
 */
internal class ActivityContextHolder {

    private var contextRef: WeakReference<android.content.Context>? = null

    fun setActivityContext(context: android.content.Context) {
        contextRef = WeakReference(context)
    }

    fun requireActivityContext(): android.content.Context =
        contextRef?.get() ?: error("Activity context not set in ActivityContextHolder")

    fun clearActivityContext() {
        contextRef = null
    }
}
