package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.data.network.FeedbackData
import com.vonage.android.data.network.ReportResponseData
import javax.inject.Inject

class ReportingRepository @Inject constructor(
    private val apiService: APIService,
) {

    suspend fun sendReport(feedbackData: FeedbackData): Result<ReportResponseData> =
        runCatching {
            val response = apiService.report(feedbackData)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.feedbackData)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed sending report"))
            }
        }
}
