package com.vonage.android.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @POST("feedback/report")
    suspend fun report(@Body reportDataRequest: ReportDataRequest): Response<ReportResponse>

}
