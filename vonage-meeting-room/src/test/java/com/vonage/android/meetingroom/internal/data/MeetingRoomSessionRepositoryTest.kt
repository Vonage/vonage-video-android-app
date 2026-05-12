package com.vonage.android.meetingroom.internal.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MeetingRoomSessionRepositoryTest {

    private val apiService: MeetingRoomApiService = mockk()
    private val sut = MeetingRoomSessionRepository(apiService)

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
        coEvery { apiService.getSession(any()) } returns Response.error(500, ResponseBody.EMPTY)

        val result = sut.getSession("any-room-name")

        assertTrue(result.isFailure)
        assertEquals("Failed getting session", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given api throws exception returns failure`() = runTest {
        coEvery { apiService.getSession(any()) } throws Exception("Network error")

        val result = sut.getSession("any-room-name")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
