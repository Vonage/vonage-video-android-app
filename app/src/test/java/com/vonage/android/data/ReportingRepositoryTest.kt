package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.data.network.ReportDataRequest
import com.vonage.android.data.network.ReportResponse
import com.vonage.android.data.network.ReportResponseData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import retrofit2.Response
import kotlin.Result.Companion.success
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class ReportingRepositoryTest {

    val apiService: APIService = mockk()
    val sut = ReportingRepository(
        apiService = apiService,
    )

    @Test
    fun `given repository when report api success returns success`() = runTest {
        coEvery { apiService.report(reportDataRequest) } returns Response<ReportResponse>.success(
            ReportResponse(
                feedbackData = responseData,
            )
        )
        val response = sut.sendReport(
            ReportDataRequest(
                title = "any title",
                name = "any name",
                issue = "any issue",
                attachment = "base64 attachment"
            )
        )
        assertEquals(success(responseData), response)
    }

    @Test
    fun `given repository when report api success with empty returns failure`() = runTest {
        coEvery { apiService.report(reportDataRequest) } returns Response.success(null)
        val response = sut.sendReport(reportDataRequest)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when report api fails returns error`() = runTest {
        coEvery { apiService.report(reportDataRequest) } returns Response.error(
            500, ResponseBody.EMPTY
        )
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
