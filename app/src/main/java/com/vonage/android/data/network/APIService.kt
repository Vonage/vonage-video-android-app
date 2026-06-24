package com.vonage.android.data.network

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {

    @POST("feedback/report")
    suspend fun report(@Body reportDataRequest: ReportDataRequest): Response<ReportResponse>

    @POST("client-logs/batch")
    suspend fun sendClientLogs(@Body requestBody: RequestBody): Response<Unit>

}
