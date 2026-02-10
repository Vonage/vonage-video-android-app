package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class FileLogInterceptorTest {

    private lateinit var logFile: File

    @Before
    fun setUp() {
        logFile = File.createTempFile("vonage-logger-test", ".log")
        logFile.writeText("")
    }

    @After
    fun tearDown() {
        logFile.delete()
    }

    // ---- Formatting ----

    @Test
    fun `formatEvent produces expected format without throwable`() {
        val interceptor = FileLogInterceptor(logFile)
        val event = LogEvent(LogLevel.DEBUG, "MyTag", "hello world")

        val line = interceptor.formatEvent(event, timestamp = 1_000_000_000_000L)

        assertTrue(line.contains("[DEBUG]"))
        assertTrue(line.contains("MyTag"))
        assertTrue(line.contains("hello world"))
        assertFalse(line.contains("\n"))
    }

    @Test
    fun `formatEvent includes throwable on new line`() {
        val interceptor = FileLogInterceptor(logFile)
        val error = RuntimeException("boom")
        val event = LogEvent(LogLevel.ERROR, "Err", "failed", error)

        val line = interceptor.formatEvent(event, timestamp = 1_000_000_000_000L)

        assertTrue(line.contains("[ERROR]"))
        assertTrue(line.contains("Err: failed"))
        assertTrue(line.contains("RuntimeException"))
        assertTrue(line.contains("boom"))
    }

    // ---- File writing ----

    @Test
    fun `intercept writes log line to file`() {
        val interceptor = FileLogInterceptor(logFile)
        val event = LogEvent(LogLevel.INFO, "Tag", "test message")

        val chain = mockk<LogInterceptor.Chain>()
        every { chain.event() } returns event
        every { chain.proceed(event) } returns event

        interceptor.intercept(chain)

        val content = logFile.readText()
        assertTrue(content.contains("[INFO]"))
        assertTrue(content.contains("Tag: test message"))
    }

    @Test
    fun `intercept proceeds chain after writing`() {
        val interceptor = FileLogInterceptor(logFile)
        val event = LogEvent(LogLevel.DEBUG, "Tag", "msg")

        val chain = mockk<LogInterceptor.Chain>()
        every { chain.event() } returns event
        every { chain.proceed(event) } returns event

        interceptor.intercept(chain)

        verify(exactly = 1) { chain.proceed(event) }
    }

    @Test
    fun `multiple events are appended on separate lines`() {
        val interceptor = FileLogInterceptor(logFile)

        val events = listOf(
            LogEvent(LogLevel.DEBUG, "T", "first"),
            LogEvent(LogLevel.INFO, "T", "second"),
            LogEvent(LogLevel.WARN, "T", "third"),
        )

        events.forEach { event ->
            val chain = mockk<LogInterceptor.Chain>()
            every { chain.event() } returns event
            every { chain.proceed(event) } returns event
            interceptor.intercept(chain)
        }

        val lines = logFile.readLines().filter { it.isNotBlank() }
        assertEquals(3, lines.size)
        assertTrue(lines[0].contains("first"))
        assertTrue(lines[1].contains("second"))
        assertTrue(lines[2].contains("third"))
    }

    // ---- File creation ----

    @Test
    fun `creates file if it does not exist`() {
        logFile.delete()
        assertFalse(logFile.exists())

        val interceptor = FileLogInterceptor(logFile)
        val event = LogEvent(LogLevel.INFO, "Tag", "create me")

        val chain = mockk<LogInterceptor.Chain>()
        every { chain.event() } returns event
        every { chain.proceed(event) } returns event

        interceptor.intercept(chain)

        assertTrue(logFile.exists())
        assertTrue(logFile.readText().contains("create me"))
    }

    // ---- Rotation ----

    @Test
    fun `rotates file when max size exceeded`() {
        val smallMax = 50L // 50 bytes
        val interceptor = FileLogInterceptor(logFile, maxFileSize = smallMax)

        // Write enough to exceed the limit
        logFile.writeText("x".repeat(100))
        assertTrue(logFile.length() > smallMax)

        val event = LogEvent(LogLevel.DEBUG, "T", "after rotation")
        val chain = mockk<LogInterceptor.Chain>()
        every { chain.event() } returns event
        every { chain.proceed(event) } returns event

        interceptor.intercept(chain)

        // File should have been rotated — only the new event remains
        val content = logFile.readText()
        assertTrue(content.contains("after rotation"))
        assertFalse(content.contains("x".repeat(50)))
    }

    @Test
    fun `does not rotate when under max size`() {
        val interceptor = FileLogInterceptor(logFile, maxFileSize = 10_000)

        logFile.writeText("existing content\n")

        val event = LogEvent(LogLevel.INFO, "T", "new entry")
        val chain = mockk<LogInterceptor.Chain>()
        every { chain.event() } returns event
        every { chain.proceed(event) } returns event

        interceptor.intercept(chain)

        val content = logFile.readText()
        assertTrue(content.contains("existing content"))
        assertTrue(content.contains("new entry"))
    }

    // ---- Constants ----

    @Test
    fun `default constants have expected values`() {
        assertEquals(5L * 1024 * 1024, FileLogInterceptor.DEFAULT_MAX_FILE_SIZE)
        assertEquals("yyyy-MM-dd HH:mm:ss.SSS", FileLogInterceptor.DEFAULT_DATE_FORMAT)
    }
}
