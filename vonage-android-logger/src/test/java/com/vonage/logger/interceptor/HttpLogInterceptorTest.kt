package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ExecutorService

class HttpLogInterceptorTest {

    private val testUrl = "https://logs.example.com/v1/ingest"

    /** Extracts a string value for [key] from a raw JSON string. */
    private fun jsonValue(json: String, key: String): String? {
        val pattern = "\"$key\":\"((?:[^\"\\\\]|\\\\.)*)\"".toRegex()
        return pattern.find(json)?.groupValues?.get(1)
    }

    private fun jsonContainsKey(json: String, key: String): Boolean {
        return "\"$key\"".toRegex().containsMatchIn(json)
    }

    // ---- JSON serialisation ----

    @Test
    fun `eventToJson includes level, tag and message`() {
        val event = LogEvent(LogLevel.DEBUG, "MyTag", "hello world")
        val json = HttpLogInterceptor.eventToJson(event)

        assertEquals("DEBUG", jsonValue(json, "level"))
        assertEquals("MyTag", jsonValue(json, "tag"))
        assertEquals("hello world", jsonValue(json, "message"))
        assertFalse(jsonContainsKey(json, "throwable"))
    }

    @Test
    fun `eventToJson includes throwable when present`() {
        val error = RuntimeException("boom")
        val event = LogEvent(LogLevel.ERROR, "Err", "failed", error)
        val json = HttpLogInterceptor.eventToJson(event)

        assertEquals("ERROR", jsonValue(json, "level"))
        assertTrue(jsonValue(json, "throwable")!!.contains("boom"))
    }

    @Test
    fun `eventToJson omits throwable key when null`() {
        val event = LogEvent(LogLevel.INFO, "Tag", "msg", throwable = null)
        val json = HttpLogInterceptor.eventToJson(event)

        assertFalse(jsonContainsKey(json, "throwable"))
    }

    @Test
    fun `eventToJson handles all log levels`() {
        LogLevel.entries.forEach { level ->
            val event = LogEvent(level, "T", "m")
            val json = HttpLogInterceptor.eventToJson(event)
            assertEquals(level.name, jsonValue(json, "level"))
        }
    }

    // ---- Interceptor chain behaviour ----

    @Test
    fun `intercept submits work to executor and proceeds chain`() {
        val executor = mockk<ExecutorService>()
        val runnableSlot = slot<Runnable>()
        every { executor.submit(capture(runnableSlot)) } returns mockk()

        val interceptor = HttpLogInterceptor(testUrl, executor)
        val event = LogEvent(LogLevel.DEBUG, "Tag", "msg")

        val chain = mockk<LogInterceptor.Chain>()
        every { chain.event() } returns event
        every { chain.proceed(event) } returns event

        val result = interceptor.intercept(chain)

        assertEquals(event, result)
        verify { chain.proceed(event) }
        assertTrue(runnableSlot.isCaptured)
    }

    @Test
    fun `intercept does not block on network call`() {
        val executor = mockk<ExecutorService>()
        every { executor.submit(any<Runnable>()) } returns mockk()

        val interceptor = HttpLogInterceptor(testUrl, executor)
        val event = LogEvent(LogLevel.INFO, "Tag", "msg")

        val chain = mockk<LogInterceptor.Chain>()
        every { chain.event() } returns event
        every { chain.proceed(event) } returns event

        interceptor.intercept(chain)

        // Verify submit was called (work is delegated, not run inline)
        verify(exactly = 1) { executor.submit(any<Runnable>()) }
    }

    // ---- Network failure resilience ----

    @Test
    fun `postEventSync returns failure code for invalid url`() {
        val interceptor = HttpLogInterceptor(
            url = "https://invalid.host.that.does.not.exist.example.com/log",
            connectTimeout = 500,
            readTimeout = 500,
        )
        val event = LogEvent(LogLevel.DEBUG, "Tag", "msg")

        val result = interceptor.postEventSync(event)

        assertEquals(HttpLogInterceptor.FAILURE_CODE, result)
    }

    // ---- Constants ----

    @Test
    fun `constants have expected values`() {
        assertEquals("POST", HttpLogInterceptor.POST_METHOD)
        assertEquals("Accept", HttpLogInterceptor.HEADER_ACCEPT)
        assertEquals("Content-type", HttpLogInterceptor.HEADER_CONTENT_TYPE)
        assertEquals("application/json", HttpLogInterceptor.CONTENT_TYPE_JSON)
        assertEquals(5_000, HttpLogInterceptor.DEFAULT_TIMEOUT)
        assertEquals(-1, HttpLogInterceptor.FAILURE_CODE)
    }
}
