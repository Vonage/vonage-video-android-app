package com.vonage.android.data.network

import com.vonage.android.okta.VonageOktaAuth
import com.vonage.android.vera.api.AuthTokenProvider
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Supplies the Okta access token to backend requests when the user is authenticated via the
 * optional Okta feature. Returns null otherwise, so requests are sent without `Authorization`.
 */
class OktaAuthTokenProvider @Inject constructor(
    private val oktaAuth: VonageOktaAuth,
) : AuthTokenProvider {

    // Called by OkHttp on a background thread, so blocking here is safe.
    override fun currentToken(): String? = runBlocking { oktaAuth.currentToken() }
}
