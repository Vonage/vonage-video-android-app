package com.vonage.android.data.network.interceptor

import com.vonage.android.okta.VonageOktaAuth
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Adds an `Authorization: Bearer` header to backend requests when the user is
 * authenticated via the optional Okta feature.
 *
 * When no token is available (feature disabled, user signed out, or refresh failed)
 * the request is sent unchanged, so the app keeps working against backends that do
 * not enforce authentication yet.
 */
class AuthorizationInterceptor @Inject constructor(
    private val oktaAuth: VonageOktaAuth,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // OkHttp interceptors run on background threads, so blocking here is safe.
        val token = runBlocking { oktaAuth.currentToken() }
        val request = if (token != null) {
            chain.request()
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
