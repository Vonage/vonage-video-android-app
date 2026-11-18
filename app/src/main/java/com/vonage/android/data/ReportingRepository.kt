package com.vonage.android.data

import com.vonage.android.vera.api.feedback.FeedbackApi
import com.vonage.android.vera.api.feedback.ReportDataRequest
import com.vonage.android.vera.api.feedback.ReportResponseData
import javax.inject.Inject

class ReportingRepository @Inject constructor(
    private val feedbackApi: FeedbackApi,
) {

    suspend fun sendReport(reportDataRequest: ReportDataRequest): Result<ReportResponseData> =
        feedbackApi.report(reportDataRequest)
}
