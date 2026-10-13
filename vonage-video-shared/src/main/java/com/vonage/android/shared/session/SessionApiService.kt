package com.vonage.android.shared.session

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Backend endpoint returning the Vonage Video session credentials for a room.
 *
 * Each caller creates it from its own Retrofit instance (`retrofit.create(SessionApiService::class.java)`)
 * so its own interceptors (e.g. the `Authorization` header) apply.
 */
interface SessionApiService {

    @GET("session/{room}")
    suspend fun getSession(@Path("room") room: String): Response<GetSessionResponse>
}
