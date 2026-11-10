package com.vonage.android.vera.api.http

import com.vonage.android.vera.api.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds an `Authorization: Bearer` header to backend requests when [tokenProvider] returns a token.
 *
 * When no token is available (authentication disabled, user signed out, or refresh failed)
 * the request is sent unchanged, so the app keeps working against backends that do
 * not enforce authentication yet.
 */
internal class AuthorizationInterceptor(
    private val tokenProvider: AuthTokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // OkHttp interceptors run on background threads, so a blocking provider is safe here.
        val token = tokenProvider.currentToken()
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
