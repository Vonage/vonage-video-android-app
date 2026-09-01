package com.vonage.android.okta

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.okta.data.BrowserSignInProvider
import com.vonage.android.okta.data.SignInResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EnabledVonageOktaAuthTest {

    private val browserSignIn: BrowserSignInProvider = mockk()
    private val context: Context = mockk(relaxed = true)
    private val auth = EnabledVonageOktaAuth(browserSignIn)

    @Test
    fun `given enabled flavor when isCapable returns true`() {
        assertTrue(auth.isCapable)
    }

    @Test
    fun `given fresh instance when authState returns NotAuthenticated`() = runTest {
        assertEquals(AuthState.NotAuthenticated, auth.authState.value)
    }

    @Test
    fun `given successful sign-in when signIn returns success and publishes Authenticated`() = runTest {
        coEvery { browserSignIn.signIn(context) } returns
            Result.success(SignInResult(accessToken = "token", userName = "Jane Doe"))

        auth.authState.test {
            assertEquals(AuthState.NotAuthenticated, awaitItem())

            val result = auth.signIn(context)

            assertTrue(result.isSuccess)
            assertEquals(AuthState.Authenticated(AuthenticatedUser(name = "Jane Doe")), awaitItem())
        }
    }

    @Test
    fun `given failed sign-in when signIn returns failure and keeps NotAuthenticated`() = runTest {
        coEvery { browserSignIn.signIn(context) } returns Result.failure(Exception("cancelled"))

        val result = auth.signIn(context)

        assertTrue(result.isFailure)
        assertEquals(AuthState.NotAuthenticated, auth.authState.value)
    }

    @Test
    fun `given authenticated user when signOut removes credential and resets state`() = runTest {
        coEvery { browserSignIn.signIn(context) } returns
            Result.success(SignInResult(accessToken = "token", userName = "Jane Doe"))
        coEvery { browserSignIn.removeCredential() } returns Result.success(Unit)
        auth.signIn(context)

        val result = auth.signOut()

        assertTrue(result.isSuccess)
        assertEquals(AuthState.NotAuthenticated, auth.authState.value)
        coVerify { browserSignIn.removeCredential() }
    }

    @Test
    fun `given failed credential removal when signOut keeps Authenticated state`() = runTest {
        coEvery { browserSignIn.signIn(context) } returns
            Result.success(SignInResult(accessToken = "token", userName = "Jane Doe"))
        coEvery { browserSignIn.removeCredential() } returns Result.failure(Exception("storage error"))
        auth.signIn(context)

        val result = auth.signOut()

        assertTrue(result.isFailure)
        assertTrue(auth.authState.value.isAuthenticated)
    }

    @Test
    fun `given stored session when restoreSession publishes Authenticated`() = runTest {
        coEvery { browserSignIn.restoreSession() } returns
            SignInResult(accessToken = "token", userName = "Jane Doe")

        auth.restoreSession()

        assertEquals(AuthState.Authenticated(AuthenticatedUser(name = "Jane Doe")), auth.authState.value)
    }

    @Test
    fun `given no stored session when restoreSession publishes NotAuthenticated`() = runTest {
        coEvery { browserSignIn.restoreSession() } returns null

        auth.restoreSession()

        assertFalse(auth.authState.value.isAuthenticated)
    }

    @Test
    fun `given provider token when currentToken returns it`() = runTest {
        coEvery { browserSignIn.currentToken() } returns "access-token"

        assertEquals("access-token", auth.currentToken())
    }

    @Test
    fun `given no provider token when currentToken returns null`() = runTest {
        coEvery { browserSignIn.currentToken() } returns null

        assertNull(auth.currentToken())
    }
}
