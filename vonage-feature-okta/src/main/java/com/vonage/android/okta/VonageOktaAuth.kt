package com.vonage.android.okta

import android.content.Context
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

/**
 * Public contract of the optional Okta authentication feature.
 *
 * The `enabled` flavor performs browser-based OIDC sign-in via the Okta mobile SDK;
 * the `disabled` flavor is an inert stub that always reports [AuthState.NotAuthenticated]
 * and provides no token, so consumers (e.g. the backend `Authorization` header
 * interceptor) need no feature-flag branching.
 */
@Stable
interface VonageOktaAuth {

    /** True when the Okta feature is compiled in (okta enabled flavor). */
    val isCapable: Boolean

    /** Reactive authentication state. */
    val authState: StateFlow<AuthState>

    /**
     * Restores a previously stored session (if any) and publishes the resulting [authState].
     */
    suspend fun restoreSession()

    /**
     * Starts the browser-based sign-in flow.
     *
     * @param context An Activity context used to launch the Custom Tab.
     */
    suspend fun signIn(context: Context): Result<Unit>

    /**
     * Removes the stored credential and resets [authState] to [AuthState.NotAuthenticated].
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Returns a valid access token for backend requests, refreshing it when expired.
     * Returns null when the user is not authenticated or the token cannot be refreshed —
     * callers should then send the request without an `Authorization` header so the app
     * keeps working against backends that do not enforce authentication yet.
     */
    suspend fun currentToken(): String?
}
