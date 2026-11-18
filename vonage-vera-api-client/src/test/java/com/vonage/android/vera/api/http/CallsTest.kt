package com.vonage.android.vera.api.http

import com.vonage.android.vera.api.VeraApiException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import retrofit2.Response
import java.io.IOException

class CallsTest {

    @Test
    fun `execute returns body on success`() = runTest {
        val result = execute("failed") { Response.success("body") }

        assertEquals("body", result.getOrThrow())
    }

    @Test
    fun `execute wraps http error in VeraApiException`() = runTest {
        val result = execute<String>("failed") { Response.error(500, "boom".toResponseBody()) }

        val error = result.exceptionOrNull()
        assertTrue(error is VeraApiException)
        assertEquals("failed", error?.message)
    }

    @Test
    fun `execute returns transport errors as failure`() = runTest {
        val result = execute<String>("failed") { throw IOException("offline") }

        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun `execute rethrows cancellation`() = runTest {
        assertFailsWith<CancellationException> {
            execute<String>("failed") { throw CancellationException("cancelled") }
        }
    }

    @Test
    fun `executeIgnoringBody rethrows cancellation`() = runTest {
        assertFailsWith<CancellationException> {
            executeIgnoringBody("failed") { throw CancellationException("cancelled") }
        }
    }
}
