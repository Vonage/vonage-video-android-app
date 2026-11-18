package com.vonage.android.vera.api.http

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class UserAgentInterceptor(
    private val userAgent: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
            .newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(request)
    }
}
