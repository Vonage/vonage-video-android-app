package com.vonage.android.meetingroom.api

/**
 * Supplies an access token for the SDK's backend requests (session fetch,
 * archiving, captions).
 *
 * When set via [MeetingRoomBuilder.authTokenProvider], every request carries an
 * `Authorization: Bearer <token>` header. Returning null sends the request
 * without the header, so backends that do not enforce authentication keep working.
 *
 * Invoked by OkHttp on a background thread — implementations may block (e.g.
 * `runBlocking { ... }` around a suspending token refresh).
 */
fun interface MeetingRoomAuthTokenProvider {

    /** Returns a valid access token, or null when the user is not authenticated. */
    fun currentToken(): String?
}
