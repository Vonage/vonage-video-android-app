package com.vonage.android.vera.api

/**
 * Supplies the access token for backend requests. Returning null sends the request without the
 * `Authorization` header, so backends that do not enforce authentication keep working.
 *
 * Invoked by OkHttp on a background thread: implementations may block (e.g. `runBlocking { ... }`
 * around a suspending token refresh).
 */
fun interface AuthTokenProvider {

    /** Returns a valid access token, or null when the user is not authenticated. */
    fun currentToken(): String?
}
