package com.vonage.android.data.network

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @GET("session/{room}")
    suspend fun getSession(@Path("room") room: String): Response<GetSessionResponse>

    @POST("feedback/report")
    suspend fun report(@Body reportDataRequest: ReportDataRequest): Response<ReportResponse>

    @POST("client-logs")
    suspend fun sendClientLogs(@Body requestBody: RequestBody): Response<Unit>

}
