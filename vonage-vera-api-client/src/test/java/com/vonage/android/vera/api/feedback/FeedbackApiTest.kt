package com.vonage.android.vera.api.feedback

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeedbackApiTest {

    private val service: FeedbackService = mockk()
    private val sut = DefaultFeedbackApi(service)

    @Test
    fun `given report success returns the feedback data`() = runTest {
        coEvery { service.report(reportDataRequest) } returns Response.success(
            ReportResponse(feedbackData = responseData)
        )
        assertEquals(Result.success(responseData), sut.report(reportDataRequest))
    }

    @Test
    fun `given report empty body returns failure`() = runTest {
        coEvery { service.report(reportDataRequest) } returns Response.success(null)
        assertTrue(sut.report(reportDataRequest).isFailure)
    }

    @Test
    fun `given report error response returns failure`() = runTest {
        coEvery { service.report(reportDataRequest) } returns Response.error(500, ResponseBody.EMPTY)
        assertTrue(sut.report(reportDataRequest).isFailure)
    }

    private val reportDataRequest = ReportDataRequest(
        title = "any title",
        name = "any name",
        issue = "any issue",
        attachment = "base64 attachment"
    )
    private val responseData = ReportResponseData(
        message = "ok nice, thanks!",
        ticketUrl = "https://jira.server.io/ticket-968",
        screenshotIncluded = false,
    )
}
