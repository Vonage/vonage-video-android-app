package com.vonage.android.data

import com.vonage.android.okta.VonageOktaAuth
import com.vonage.android.vera.api.session.SessionApi
import com.vonage.android.vera.api.session.SessionUnauthorizedException
import javax.inject.Inject

/**
 * Decides whether the user must sign in before creating or joining a room.
 *
 * Only probes the backend when authentication is enabled in the build config
 * (`authSettings.allowAuthentication`); a `401` from the session endpoint is the sole
 * trigger for asking the user to sign in. Backends without the authentication middleware
 * keep working without forcing a sign-in.
 */
class RoomAccessRepository @Inject constructor(
    private val sessionApi: SessionApi,
    private val oktaAuth: VonageOktaAuth,
) {

    suspend fun requiresAuthentication(roomName: String): Boolean {
        if (!oktaAuth.isCapable) return false
        // The Authorization header (if any) is attached by the API client, so an
        // expired, non-refreshable session is also reported as unauthorized. Any other failure
        // (network, server error) is surfaced later by the meeting room itself.
        return sessionApi.getSession(roomName).exceptionOrNull() is SessionUnauthorizedException
    }
}
