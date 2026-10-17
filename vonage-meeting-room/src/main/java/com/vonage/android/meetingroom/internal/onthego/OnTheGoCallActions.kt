package com.vonage.android.meetingroom.internal.onthego

/**
 * Actions the controller performs on the host call.
 * State changes always flow back through [OnTheGoCallSnapshot].
 */
internal class OnTheGoCallActions(
    val toggleMic: () -> Unit,
    val toggleCamera: () -> Unit,
    val endCall: () -> Unit,
)
