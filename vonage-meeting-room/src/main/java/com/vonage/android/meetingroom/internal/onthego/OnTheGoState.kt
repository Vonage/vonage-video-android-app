package com.vonage.android.meetingroom.internal.onthego

/** State of the On-the-Go mode state machine. */
internal enum class OnTheGoState {
    /** Normal call UI is visible. */
    IDLE,

    /** The suggestion banner is visible. */
    SUGGESTED,

    /** The audio-first On-the-Go screen is visible. */
    ACTIVE,
}
