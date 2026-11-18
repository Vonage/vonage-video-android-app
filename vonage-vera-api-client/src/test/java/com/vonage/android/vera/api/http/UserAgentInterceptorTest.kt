package com.vonage.android.vera.api.http

import io.mockk.clearAllMocks
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

class UserAgentInterceptorTest {

    private val interceptor = UserAgentInterceptor(USER_AGENT)
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
    fun `intercept should add User-Agent header`() {
        interceptor.intercept(mockChain)

        verify {
            mockChain.proceed(
                match { request -> request.header("User-Agent") == USER_AGENT },
            )
        }
    }

    @Test
    fun `intercept should preserve original request url`() {
        interceptor.intercept(mockChain)

        verify {
            mockChain.proceed(
                match { request ->
                    request.url.toString() == "https://example.com/"
                },
            )
        }
    }

    @Test
    fun `intercept should return response from chain proceed`() {
        val result = interceptor.intercept(mockChain)

        assertEquals(mockResponse, result)
    }

    private companion object {
        const val USER_AGENT = "VeraNativeAndroid/1.0.0 android 14"
    }
}
