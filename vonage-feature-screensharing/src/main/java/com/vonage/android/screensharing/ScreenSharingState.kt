package com.vonage.android.screensharing

/**
 * Represents the current screen sharing lifecycle state.
 */
enum class ScreenSharingState {
    /** Screen sharing is not active. */
    IDLE,

    /** Screen sharing is starting (permission/service setup in progress). */
    STARTING,

    /** Screen sharing is active. */
    SHARING,

    /** Screen sharing is stopping (cleanup in progress). */
    STOPPING,
}
