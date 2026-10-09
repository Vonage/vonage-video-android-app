package com.vonage.android.meetingroom.internal.data

import com.vonage.android.meetingroom.api.MeetingRoomAuthTokenProvider
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

internal object MeetingRoomNetworkFactory {

    fun createRetrofit(
        baseUrl: String,
        isDebug: Boolean = false,
        authTokenProvider: MeetingRoomAuthTokenProvider? = null,
    ): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (isDebug) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                }
            )
            .apply {
                if (authTokenProvider != null) {
                    addInterceptor { chain ->
                        val token = authTokenProvider.currentToken()
                        val request = if (token != null) {
                            chain.request()
                                .newBuilder()
                                .header("Authorization", "Bearer $token")
                                .build()
                        } else {
                            chain.request()
                        }
                        chain.proceed(request)
                    }
                }
            }
            .build()

        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
