package com.vonage.android.data.network

import kotlinx.serialization.Serializable

@Serializable
data class ReportDataRequest(
    val title: String,
    val name: String,
    val issue: String,
    val attachment: String,
)

@Serializable
data class ReportResponse(
    val feedbackData: ReportResponseData
)

@Serializable
data class ReportResponseData(
    val message: String,
    val ticketUrl: String,
    val screenshotIncluded: Boolean? = null,
)
