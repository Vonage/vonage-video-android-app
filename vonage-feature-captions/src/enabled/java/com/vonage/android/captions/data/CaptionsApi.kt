package com.vonage.android.captions.data

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface CaptionsApi {

    @POST("session/{room}/enableCaptions")
    suspend fun enableCaptions(@Path("room") room: String): Response<EnableCaptionsResponse>

}
