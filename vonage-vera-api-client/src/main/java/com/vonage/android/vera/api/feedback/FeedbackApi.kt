package com.vonage.android.vera.api.feedback

import com.vonage.android.vera.api.http.execute

interface FeedbackApi {

    /** Files an issue report and returns the created ticket. */
    suspend fun report(request: ReportDataRequest): Result<ReportResponseData>
}

internal class DefaultFeedbackApi(
    private val service: FeedbackService,
) : FeedbackApi {

    override suspend fun report(request: ReportDataRequest): Result<ReportResponseData> =
        execute("Failed sending report") { service.report(request) }
            .map { it.feedbackData }
}
