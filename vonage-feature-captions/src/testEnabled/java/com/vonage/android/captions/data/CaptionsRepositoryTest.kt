package com.vonage.android.captions.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Test
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
        coEvery { apiService.enableCaptions("any-room-name") } returns Response.success(
            EnableCaptionsResponse("captions-id")
        )
        val response = sut.enableCaptions("any-room-name")
        assertEquals(Result.Companion.success("captions-id"), response)
    }

    @Test
    fun `given repository when enableCaptions api success with empty returns failure`() = runTest {
        coEvery { apiService.enableCaptions("any-room-name") } returns Response.success(null)
        val response = sut.enableCaptions("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when enableCaptions api fails returns error`() = runTest {
        coEvery { apiService.enableCaptions("any-room-name") } returns Response.error(
            500, ResponseBody.Companion.EMPTY
        )
        val response = sut.enableCaptions("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when enableCaptions api fails with exception returns error`() = runTest {
        coEvery { apiService.enableCaptions("any-room-name") } throws Exception("Network error")
        val response = sut.enableCaptions("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when disableCaptions api success returns success`() = runTest {
        coEvery {
            apiService.disableCaptions(
                "any-room-name",
                "captions-id"
            )
        } returns Response.success(
            DisableCaptionsResponse("Everything went OK :)")
        )
        val response = sut.disableCaptions("any-room-name", "captions-id")
        assertEquals(Result.Companion.success("Everything went OK :)"), response)
    }

    @Test
    fun `given repository when disableCaptions api success with empty returns success`() = runTest {
        coEvery {
            apiService.disableCaptions(
                "any-room-name",
                "captions-id"
            )
        } returns Response.success(null)
        val response = sut.disableCaptions("any-room-name", "captions-id")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when disableCaptions api fails returns error`() = runTest {
        coEvery {
            apiService.disableCaptions(
                "any-room-name",
                "captions-id"
            )
        } returns Response.error(
            500, ResponseBody.Companion.EMPTY
        )
        val response = sut.disableCaptions("any-room-name", "captions-id")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when disableCaptions api fails with exception returns error`() = runTest {
        coEvery {
            apiService.disableCaptions(
                "any-room-name",
                "captions-id"
            )
        } throws Exception("Network error")
        val response = sut.disableCaptions("any-room-name", "captions-id")
        assertTrue(response.isFailure)
    }
}
