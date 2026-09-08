package com.vonage.android.okta.data

import android.content.Context

/**
 * Result of a successful browser sign-in or session restore.
 */
data class SignInResult(
    val accessToken: String,
    val userName: String?,
)

/**
 * Abstracts the Okta browser sign-in and credential storage for testability.
 *
 * The default implementation is [OktaBrowserSignInProvider]; tests supply a mock
 * so [com.vonage.android.okta.EnabledVonageOktaAuth] can be verified without the SDK.
 */
interface BrowserSignInProvider {

    /** Performs browser-based sign-in and stores the resulting credential. */
    suspend fun signIn(context: Context): Result<SignInResult>

    /**
     * Returns a valid access token, refreshing it when expired.
     * Returns null when no credential is stored or the refresh fails.
     */
    suspend fun currentToken(): String?

    /** Removes the stored credential. */
    suspend fun removeCredential(): Result<Unit>

    /** Restores a previously stored session. Returns null when nothing is stored. */
    suspend fun restoreSession(): SignInResult?
}
