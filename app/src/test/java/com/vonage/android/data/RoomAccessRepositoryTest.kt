package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.okta.VonageOktaAuth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoomAccessRepositoryTest {

    private val apiService: APIService = mockk()
    private val oktaAuth: VonageOktaAuth = mockk()
    private val sut = RoomAccessRepository(
        apiService = apiService,
        oktaAuth = oktaAuth,
    )

    @Test
    fun `given authentication disabled then does not require authentication nor call backend`() = runTest {
        every { oktaAuth.isCapable } returns false

        assertFalse(sut.requiresAuthentication("room"))
        coVerify(exactly = 0) { apiService.getSession(any()) }
    }

    @Test
    fun `given authentication enabled when backend returns 401 then requires authentication`() = runTest {
        every { oktaAuth.isCapable } returns true
        coEvery { apiService.getSession("room") } returns Response.error(401, "".toResponseBody())

        assertTrue(sut.requiresAuthentication("room"))
    }

    @Test
    fun `given authentication enabled when backend returns success then does not require authentication`() =
        runTest {
            every { oktaAuth.isCapable } returns true
            coEvery { apiService.getSession("room") } returns Response.success(Unit)

            assertFalse(sut.requiresAuthentication("room"))
        }

    @Test
    fun `given authentication enabled when backend returns other error then does not require authentication`() =
        runTest {
            every { oktaAuth.isCapable } returns true
            coEvery { apiService.getSession("room") } returns Response.error(500, "".toResponseBody())

            assertFalse(sut.requiresAuthentication("room"))
        }

    @Test
    fun `given authentication enabled when network fails then does not require authentication`() = runTest {
        every { oktaAuth.isCapable } returns true
        coEvery { apiService.getSession("room") } throws IOException("offline")

        assertFalse(sut.requiresAuthentication("room"))
    }
}
