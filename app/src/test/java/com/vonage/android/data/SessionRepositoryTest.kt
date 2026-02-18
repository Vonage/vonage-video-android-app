package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.data.network.GetSessionResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionRepositoryTest {

    val apiService: APIService = mockk()
    val sut = SessionRepository(
        apiService = apiService,
    )

    @Test
    fun `given repository when api success returns success`() = runTest {
        coEvery { apiService.getSession(any()) } returns Response<GetSessionResponse>.success(
            GetSessionResponse(
                apiKey = "apiKey",
                sessionId = "sessionId",
                token = "token",
                captionsId = null,
            )
        )
        val response = sut.getSession("any-room-name")
        assertEquals(
            Result.success(
                SessionInfo(
                    apiKey = "apiKey",
                    sessionId = "sessionId",
                    token = "token",
                    captionsId = null,
                )
            ), response
        )
    }

    @Test
    fun `given repository when api success with empty returns success`() = runTest {
        coEvery { apiService.getSession(any()) } returns Response.success(null)
        val response = sut.getSession("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when api fails returns error`() = runTest {
        coEvery { apiService.getSession(any()) } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.getSession("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when api fails with exception returns error`() = runTest {
        coEvery { apiService.getSession(any()) } throws Exception("Network error")
        val response = sut.getSession("any-room-name")
        assertTrue(response.isFailure)
    }
}
