package com.vonage.android.shared.session

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SessionRepositoryTest {

    private val apiService: SessionApiService = mockk()
    private val sut = SessionRepository(apiService)

    @Test
    fun `given api success returns mapped SessionInfo`() = runTest {
        coEvery { apiService.getSession(any()) } returns Response.success(
            GetSessionResponse(
                apiKey = "apiKey",
                sessionId = "sessionId",
                token = "token",
                captionsId = null,
            )
        )

        val result = sut.getSession("any-room-name")

        assertEquals(
            Result.success(
                SessionInfo(
                    apiKey = "apiKey",
                    sessionId = "sessionId",
                    token = "token",
                    captionsId = null,
                )
            ),
            result,
        )
    }

    @Test
    fun `given api success with captionsId returns mapped SessionInfo with captionsId`() = runTest {
        coEvery { apiService.getSession(any()) } returns Response.success(
            GetSessionResponse(
                apiKey = "apiKey",
                sessionId = "sessionId",
                token = "token",
                captionsId = "captionsId",
            )
        )

        val result = sut.getSession("any-room-name")

        assertEquals(
            Result.success(
                SessionInfo(
                    apiKey = "apiKey",
                    sessionId = "sessionId",
                    token = "token",
                    captionsId = "captionsId",
                )
            ),
            result,
        )
    }

    @Test
    fun `given api success with null body returns failure`() = runTest {
        coEvery { apiService.getSession(any()) } returns Response.success(null)

        val result = sut.getSession("any-room-name")

        assertTrue(result.isFailure)
        assertEquals("Empty response", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given api error response returns failure`() = runTest {
        coEvery { apiService.getSession(any()) } returns Response.error(500, "".toResponseBody())

        val result = sut.getSession("any-room-name")

        assertTrue(result.isFailure)
        assertEquals("Failed getting session", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given api unauthorized response returns SessionUnauthorizedException`() = runTest {
        coEvery { apiService.getSession(any()) } returns Response.error(401, "".toResponseBody())

        val result = sut.getSession("any-room-name")

        assertIs<SessionUnauthorizedException>(result.exceptionOrNull())
    }

    @Test
    fun `given api throws exception returns failure`() = runTest {
        coEvery { apiService.getSession(any()) } throws Exception("Network error")

        val result = sut.getSession("any-room-name")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
