package com.vonage.android.captions.data

import com.vonage.android.vera.api.captions.CaptionsApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CaptionsRepositoryTest {

    val captionsApi: CaptionsApi = mockk()
    val sut = CaptionsRepository(
        captionsApi = captionsApi,
    )

    @Test
    fun `given repository when enableCaptions api success returns success`() = runTest {
        coEvery { captionsApi.ensureCaptionsEnabled(SESSION_KEY) } returns Result.success("captions-id")
        val response = sut.enableCaptions(SESSION_KEY)
        assertEquals(Result.success("captions-id"), response)
    }

    @Test
    fun `given repository when captions already running returns success with null id`() = runTest {
        coEvery { captionsApi.ensureCaptionsEnabled(SESSION_KEY) } returns Result.success(null)
        val response = sut.enableCaptions(SESSION_KEY)
        assertEquals(Result.success(null), response)
    }

    @Test
    fun `given repository when enableCaptions api fails returns error`() = runTest {
        coEvery { captionsApi.ensureCaptionsEnabled(SESSION_KEY) } returns Result.failure(Exception("Network error"))
        val response = sut.enableCaptions(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    private companion object {
        const val SESSION_KEY = "any-session-key"
    }
}
