package com.vonage.android.vera.api.feedback

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface FeedbackService {

    @POST("feedback/report")
    suspend fun report(@Body reportDataRequest: ReportDataRequest): Response<ReportResponse>

}
