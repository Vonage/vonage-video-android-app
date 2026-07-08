package com.vonage.android.meetingroom.internal.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

internal object MeetingRoomNetworkFactory {

    fun createRetrofit(baseUrl: String, isDebug: Boolean = false): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (isDebug) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()

        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
