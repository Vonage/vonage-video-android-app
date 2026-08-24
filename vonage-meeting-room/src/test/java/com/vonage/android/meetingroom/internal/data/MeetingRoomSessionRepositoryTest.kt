package com.vonage.android.meetingroom.internal.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MeetingRoomSessionRepositoryTest {

    private val apiService: MeetingRoomApiService = mockk()
    private val sut = MeetingRoomSessionRepository(apiService)

    @Test
    fun `given api success returns GetSessionResponse`() = runTest {
        val response = GetSessionResponse(
            apiKey = "apiKey",
            sessionId = "sessionId",
            token = "token",
            captionsId = null,
        )
        coEvery { apiService.getSession(any()) } returns Response.success(response)

        val result = sut.getSession("any-room-name")

        assertEquals(Result.success(response), result)
    }

    @Test
    fun `given api success with captionsId returns GetSessionResponse with captionsId`() = runTest {
        val response = GetSessionResponse(
            apiKey = "apiKey",
            sessionId = "sessionId",
            token = "token",
            captionsId = "captionsId",
        )
        coEvery { apiService.getSession(any()) } returns Response.success(response)

        val result = sut.getSession("any-room-name")

        assertEquals(Result.success(response), result)
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
        coEvery { apiService.getSession(any()) } returns Response.error(500, ResponseBody.EMPTY)

        val result = sut.getSession("any-room-name")

        assertTrue(result.isFailure)
        assertEquals("Failed getting session", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given api throws exception returns failure`() = runTest {
        coEvery { apiService.getSession(any()) } throws RuntimeException("network error")

        val result = sut.getSession("any-room-name")

        assertTrue(result.isFailure)
        assertEquals("network error", result.exceptionOrNull()?.message)
    }
}
