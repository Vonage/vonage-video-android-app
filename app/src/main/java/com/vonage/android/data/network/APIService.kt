package com.vonage.android.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @GET("session/{room}")
    suspend fun getSession(@Path("room") room: String): Response<GetSessionResponse>

    @POST("session/{room}/enableCaptions")
    suspend fun enableCaptions(@Path("room") room: String): Response<EnableCaptionsResponse>

    @POST("session/{room}/{captionsId}/disableCaptions")
    suspend fun disableCaptions(
        @Path("room") room: String,
        @Path("captionsId") captionsId: String,
    ): Response<DisableCaptionsResponse>

    @POST("feedback/report")
    suspend fun report(@Body reportDataRequest: ReportDataRequest): Response<ReportResponse>

}
