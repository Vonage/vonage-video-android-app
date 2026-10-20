package com.vonage.android.captions.data

import com.vonage.android.shared.network.SessionKeyRequest
import com.vonage.android.shared.network.TrpcResponse
import com.vonage.android.shared.network.TrpcResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CaptionsRepositoryTest {

    val apiService: CaptionsApi = mockk()
    val sut = CaptionsRepository(
        apiService = apiService,
    )

    @Test
    fun `given repository when enableCaptions api success returns success`() = runTest {
        coEvery { apiService.ensureCaptionsEnabled(REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(EnableCaptionsResponse("captions-id")))
        )
        val response = sut.enableCaptions(SESSION_KEY)
        assertEquals(Result.success("captions-id"), response)
    }

    @Test
    fun `given repository when captions already running returns success with null id`() = runTest {
        coEvery { apiService.ensureCaptionsEnabled(REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(EnableCaptionsResponse(null)))
        )
        val response = sut.enableCaptions(SESSION_KEY)
        assertEquals(Result.success(null), response)
    }

    @Test
    fun `given repository when enableCaptions api success with empty returns failure`() = runTest {
        coEvery { apiService.ensureCaptionsEnabled(REQUEST) } returns Response.success(null)
        val response = sut.enableCaptions(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when enableCaptions api fails returns error`() = runTest {
        coEvery { apiService.ensureCaptionsEnabled(REQUEST) } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.enableCaptions(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when enableCaptions api fails with exception returns error`() = runTest {
        coEvery { apiService.ensureCaptionsEnabled(REQUEST) } throws Exception("Network error")
        val response = sut.enableCaptions(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `ensure captions response decodes null captions id`() {
        val json = Json { ignoreUnknownKeys = true }
        val decoded = json.decodeFromString<TrpcResponse<EnableCaptionsResponse>>(
            """{"result":{"data":{"captionsId":null}}}"""
        )
        assertEquals(null, decoded.result.data.captionsId)
    }

    private companion object {
        const val SESSION_KEY = "any-session-key"
        val REQUEST = SessionKeyRequest(SESSION_KEY)
    }
}
