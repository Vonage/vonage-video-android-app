package com.vonage.android.vera.api

import com.vonage.android.vera.api.archiving.ArchivingApi
import com.vonage.android.vera.api.archiving.DefaultArchivingApi
import com.vonage.android.vera.api.captions.CaptionsApi
import com.vonage.android.vera.api.captions.DefaultCaptionsApi
import com.vonage.android.vera.api.feedback.DefaultFeedbackApi
import com.vonage.android.vera.api.feedback.FeedbackApi
import com.vonage.android.vera.api.http.AuthorizationInterceptor
import com.vonage.android.vera.api.http.UserAgentInterceptor
import com.vonage.android.vera.api.http.redactSecrets
import com.vonage.android.vera.api.session.DefaultSessionApi
import com.vonage.android.vera.api.session.SessionApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

/**
 * Client for the VERA backend. Every endpoint returns a [Result]; HTTP and transport details stay
 * inside this module.
 *
 * Build one per backend with [Builder]; all the APIs share its HTTP client.
 */
class VeraApiClient private constructor(retrofit: Retrofit) {

    /** v2 session bootstrap: `createSession` + `joinSession`. */
    val session: SessionApi = DefaultSessionApi(retrofit.create())

    /** v2 archiving (recordings and transcriptions), scoped by the session key. */
    val archiving: ArchivingApi = DefaultArchivingApi(retrofit.create())

    /** v2 live captions, scoped by the session key. */
    val captions: CaptionsApi = DefaultCaptionsApi(retrofit.create())

    /** Issue reporting. */
    val feedback: FeedbackApi = DefaultFeedbackApi(retrofit.create())

    /**
     * @param baseUrl Backend base URL. A trailing `/` is added when missing.
     */
    class Builder(private val baseUrl: String) {
        private var logBodies = false
        private var userAgent: String? = null
        private var authTokenProvider: AuthTokenProvider? = null
        private val interceptors = mutableListOf<Interceptor>()

        /** Logs request and response bodies (debug builds only); the session key and token are redacted. */
        fun logBodies(enabled: Boolean) = apply { logBodies = enabled }

        /** Sends this `User-Agent` header on every request. Null keeps OkHttp's default. */
        fun userAgent(value: String?) = apply { userAgent = value }

        /** Attaches `Authorization: Bearer <token>` when the provider returns a token. */
        fun authTokenProvider(provider: AuthTokenProvider?) = apply { authTokenProvider = provider }

        /** Adds an OkHttp interceptor that runs after the user agent and authorization ones. */
        fun addInterceptor(interceptor: Interceptor) = apply { interceptors += interceptor }

        fun build(): VeraApiClient {
            val client = OkHttpClient.Builder()
                // First, so it logs the request before the Authorization header is attached.
                .addInterceptor(
                    // Bodies carry the session key and client token: mask them before logging.
                    HttpLoggingInterceptor { message ->
                        HttpLoggingInterceptor.Logger.DEFAULT.log(message.redactSecrets())
                    }.apply {
                        level = if (logBodies) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                    }
                )
                .apply {
                    userAgent?.let { addInterceptor(UserAgentInterceptor(it)) }
                    authTokenProvider?.let { addInterceptor(AuthorizationInterceptor(it)) }
                    interceptors.forEach { addInterceptor(it) }
                }
                .build()

            val json = Json { ignoreUnknownKeys = true }

            val retrofit = Retrofit.Builder()
                .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()

            return VeraApiClient(retrofit)
        }
    }
}
