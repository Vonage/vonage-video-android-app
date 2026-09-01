package com.vonage.android.data.network.interceptor

import com.vonage.android.okta.VonageOktaAuth
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AuthorizationInterceptorTest {

    private val oktaAuth: VonageOktaAuth = mockk()
    private val interceptor = AuthorizationInterceptor(oktaAuth)
    private val mockChain: Interceptor.Chain = mockk(relaxed = true)
    private val mockResponse: Response = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        val requestBuilder = Request.Builder().url("https://example.com")
        every { mockChain.request() } returns requestBuilder.build()
        every { mockChain.proceed(any()) } returns mockResponse
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `intercept should add Bearer token when authenticated`() {
        coEvery { oktaAuth.currentToken() } returns "access-token"

        interceptor.intercept(mockChain)

        verify {
            mockChain.proceed(
                match { request ->
                    request.header("Authorization") == "Bearer access-token"
                },
            )
        }
    }

    @Test
    fun `intercept should not add Authorization header when token is null`() {
        coEvery { oktaAuth.currentToken() } returns null

        interceptor.intercept(mockChain)

        verify {
            mockChain.proceed(
                match { request ->
                    request.header("Authorization") == null
                },
            )
        }
    }

    @Test
    fun `intercept should return response from chain proceed`() {
        coEvery { oktaAuth.currentToken() } returns null

        val result = interceptor.intercept(mockChain)

        assertEquals(mockResponse, result)
    }
}
