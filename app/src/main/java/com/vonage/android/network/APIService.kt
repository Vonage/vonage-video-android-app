package com.vonage.android.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {

    @GET("session/{room}")
    suspend fun getSession(@Path("room") room: String): Response<GetSessionResponse>

}