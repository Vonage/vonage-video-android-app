package com.vonage.android.data.network.interceptor

import android.os.Build
import com.vonage.android.BuildConfig
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class VeraHeaderRequestDecoratorTest {

    private val decorator = VeraHeaderRequestDecorator()
    private val mockChain: Interceptor.Chain = mockk(relaxed = true)
    private val mockResponse: Response = mockk(relaxed = true)

    @Before
    fun setUp() {
        val requestBuilder = Request.Builder().url("https://example.com")
        every { mockChain.request() } returns requestBuilder.build()
        every { mockChain.proceed(any()) } returns mockResponse
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `intercept should add User-Agent header with correct format`() {
        decorator.intercept(mockChain)

        verify {
            mockChain.proceed(
                match { request ->
                    val userAgent = request.header("User-Agent")
                    userAgent == "VeraNativeAndroid/${BuildConfig.VERSION_NAME} android ${Build.VERSION.RELEASE}"
                },
            )
        }
    }

    @Test
    fun `intercept should preserve original request url`() {
        decorator.intercept(mockChain)

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
        val result = decorator.intercept(mockChain)

        assertEquals(mockResponse, result)
    }

    @Test
    fun `User-Agent header should start with VeraNativeAndroid`() {
        decorator.intercept(mockChain)

        verify {
            mockChain.proceed(
                match { request ->
                    request.header("User-Agent")?.startsWith("VeraNativeAndroid/") == true
                },
            )
        }
    }
}
