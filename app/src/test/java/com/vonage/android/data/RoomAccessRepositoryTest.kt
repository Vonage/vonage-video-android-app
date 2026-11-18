package com.vonage.android.data

import com.vonage.android.okta.VonageOktaAuth
import com.vonage.android.vera.api.session.SessionInfo
import com.vonage.android.vera.api.session.SessionApi
import com.vonage.android.vera.api.session.SessionUnauthorizedException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoomAccessRepositoryTest {

    private val sessionApi: SessionApi = mockk()
    private val oktaAuth: VonageOktaAuth = mockk()
    private val sut = RoomAccessRepository(
        sessionApi = sessionApi,
        oktaAuth = oktaAuth,
    )

    @Test
    fun `given authentication disabled then does not require authentication nor call backend`() = runTest {
        every { oktaAuth.isCapable } returns false

        assertFalse(sut.requiresAuthentication("room"))
        coVerify(exactly = 0) { sessionApi.getSession(any()) }
    }

    @Test
    fun `given authentication enabled when backend returns 401 then requires authentication`() = runTest {
        every { oktaAuth.isCapable } returns true
        coEvery { sessionApi.getSession("room") } returns Result.failure(SessionUnauthorizedException())

        assertTrue(sut.requiresAuthentication("room"))
    }

    @Test
    fun `given authentication enabled when backend returns session then does not require authentication`() =
        runTest {
            every { oktaAuth.isCapable } returns true
            coEvery { sessionApi.getSession("room") } returns Result.success(
                SessionInfo(
                    applicationId = "applicationId",
                    sessionId = "sessionId",
                    token = "token",
                    sessionKey = "sessionKey",
                )
            )

            assertFalse(sut.requiresAuthentication("room"))
        }

    @Test
    fun `given authentication enabled when backend returns other error then does not require authentication`() =
        runTest {
            every { oktaAuth.isCapable } returns true
            coEvery { sessionApi.getSession("room") } returns Result.failure(Exception("Failed getting session"))

            assertFalse(sut.requiresAuthentication("room"))
        }

    @Test
    fun `given authentication enabled when network fails then does not require authentication`() = runTest {
        every { oktaAuth.isCapable } returns true
        coEvery { sessionApi.getSession("room") } returns Result.failure(IOException("offline"))

        assertFalse(sut.requiresAuthentication("room"))
    }
}
