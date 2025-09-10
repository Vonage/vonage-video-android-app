package com.vonage.android.data.network

data class FeedbackData(
    val title: String,
    val name: String,
    val issue: String,
    val attachment: String,
)

data class ReportResponse(
    val feedbackData: ReportResponseData
)

data class ReportResponseData(
    val message: String,
    val ticketUrl: String,
    val screenshotIncluded: Boolean?,
)
