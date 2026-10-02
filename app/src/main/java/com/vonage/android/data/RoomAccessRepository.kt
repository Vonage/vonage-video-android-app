package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.okta.VonageOktaAuth
import java.io.IOException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject

/**
 * Decides whether the user must sign in before creating or joining a room.
 *
 * Authentication is only requested when the optional Okta feature is compiled in **and**
 * the backend actually rejects the session request with `401 Unauthorized`. Backends
 * without the authentication middleware keep working without forcing a sign-in.
 */
class RoomAccessRepository @Inject constructor(
    private val apiService: APIService,
    private val oktaAuth: VonageOktaAuth,
) {

    suspend fun requiresAuthentication(roomName: String): Boolean {
        if (!oktaAuth.isCapable) return false
        return try {
            // The Authorization header (if any) is attached by AuthorizationInterceptor,
            // so an expired, non-refreshable session is also reported as unauthorized.
            apiService.getSession(roomName).code() == HTTP_UNAUTHORIZED
        } catch (_: IOException) {
            // Connectivity problems are surfaced later by the meeting room itself.
            false
        }
    }
}
