package com.vonage.android.data

import com.vonage.android.vera.api.feedback.FeedbackApi
import com.vonage.android.vera.api.feedback.ReportDataRequest
import com.vonage.android.vera.api.feedback.ReportResponseData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.Result.Companion.success
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ReportingRepositoryTest {

    val feedbackApi: FeedbackApi = mockk()
    val sut = ReportingRepository(
        feedbackApi = feedbackApi,
    )

    @Test
    fun `given repository when report api success returns success`() = runTest {
        coEvery { feedbackApi.report(reportDataRequest) } returns success(responseData)
        val response = sut.sendReport(reportDataRequest)
        assertEquals(success(responseData), response)
    }

    @Test
    fun `given repository when report api fails returns error`() = runTest {
        coEvery { feedbackApi.report(reportDataRequest) } returns Result.failure(Exception("Failed sending report"))
        val response = sut.sendReport(reportDataRequest)
        assertTrue(response.isFailure)
    }

    val reportDataRequest = ReportDataRequest(
        title = "any title",
        name = "any name",
        issue = "any issue",
        attachment = "base64 attachment"
    )
    val responseData = ReportResponseData(
        message = "ok nice, thanks!",
        ticketUrl = "https://jira.server.io/ticket-968",
        screenshotIncluded = false,
    )
}
