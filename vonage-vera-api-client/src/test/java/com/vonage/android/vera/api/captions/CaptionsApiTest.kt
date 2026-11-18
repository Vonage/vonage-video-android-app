package com.vonage.android.vera.api.captions

import com.vonage.android.vera.api.http.SessionKeyRequest
import com.vonage.android.vera.api.http.TrpcResponse
import com.vonage.android.vera.api.http.TrpcResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CaptionsApiTest {

    private val service: CaptionsService = mockk()
    private val sut = DefaultCaptionsApi(service)

    @Test
    fun `given ensureCaptionsEnabled success returns the captions id`() = runTest {
        coEvery { service.ensureCaptionsEnabled(REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(EnableCaptionsResponse("captions-id")))
        )
        assertEquals(Result.success("captions-id"), sut.ensureCaptionsEnabled(SESSION_KEY))
    }

    @Test
    fun `given captions already running returns success with null id`() = runTest {
        coEvery { service.ensureCaptionsEnabled(REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(EnableCaptionsResponse(null)))
        )
        assertEquals(Result.success(null), sut.ensureCaptionsEnabled(SESSION_KEY))
    }

    @Test
    fun `given ensureCaptionsEnabled empty body returns failure`() = runTest {
        coEvery { service.ensureCaptionsEnabled(REQUEST) } returns Response.success(null)
        assertTrue(sut.ensureCaptionsEnabled(SESSION_KEY).isFailure)
    }

    @Test
    fun `given ensureCaptionsEnabled error response returns failure`() = runTest {
        coEvery { service.ensureCaptionsEnabled(REQUEST) } returns Response.error(500, ResponseBody.EMPTY)
        assertTrue(sut.ensureCaptionsEnabled(SESSION_KEY).isFailure)
    }

    @Test
    fun `given ensureCaptionsEnabled throws returns failure`() = runTest {
        coEvery { service.ensureCaptionsEnabled(REQUEST) } throws Exception("Network error")
        assertTrue(sut.ensureCaptionsEnabled(SESSION_KEY).isFailure)
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
