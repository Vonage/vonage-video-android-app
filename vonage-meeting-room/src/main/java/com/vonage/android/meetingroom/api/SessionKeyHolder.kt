package com.vonage.android.meetingroom.api

/**
 * Read-only access to the session key JWT the backend issues when a session is created.
 *
 * The key scopes every v2 backend call for the session (archiving, captions). Host apps read it
 * after the call ends, e.g. to list the session recordings on a goodbye screen.
 *
 * Treat the value as a secret: do not log it or persist it in navigation arguments.
 */
interface SessionKeyProvider {

    /** The current session key, or null before a session has been created. */
    val sessionKey: String?
}

/**
 * Thread-safe holder for the session key JWT.
 *
 * The SDK clears it when a meeting room starts and writes it once the session is created, so
 * it never holds the key of a previous room after a failed join. Pass a shared instance through
 * [MeetingRoomBuilder.sessionKeyHolder] to read the key from the host app.
 */
class SessionKeyHolder : SessionKeyProvider {

    @Volatile
    override var sessionKey: String? = null
}
