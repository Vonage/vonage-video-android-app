package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class FileLogInterceptorTest {

    private lateinit var logDir: File

    @Before
    fun setUp() {
        logDir = Files.createTempDirectory("vonage-logger-test").toFile()
    }

    @After
    fun tearDown() {
        logDir.deleteRecursively()
    }

    // ---- Formatting ----

    @Test
    fun `formatEvent produces expected format without throwable`() {
        val interceptor = FileLogInterceptor(logDir)
        val event = LogEvent(
            LogLevel.DEBUG, "MyTag", "hello world",
            timestamp = 1_000_000_000_000L, thread = "test-thread"
        )

        val line = interceptor.formatEvent(event)

        assertTrue(line.contains("[DEBUG]"))
        assertTrue(line.contains("[test-thread]"))
        assertTrue(line.contains("MyTag"))
        assertTrue(line.contains("hello world"))
        assertFalse(line.contains("\n"))
    }

    @Test
    fun `formatEvent includes throwable on new line`() {
        val interceptor = FileLogInterceptor(logDir)
        val error = RuntimeException("boom")
        val event = LogEvent(
            LogLevel.ERROR, "Err", "failed", error,
            timestamp = 1_000_000_000_000L, thread = "err-thread"
        )

        val line = interceptor.formatEvent(event)

        assertTrue(line.contains("[ERROR]"))
        assertTrue(line.contains("[err-thread]"))
        assertTrue(line.contains("Err: failed"))
        assertTrue(line.contains("RuntimeException"))
        assertTrue(line.contains("boom"))
    }

    // ---- File writing ----

    @Test
    fun `intercept writes log line to today's file`() {
        val interceptor = FileLogInterceptor(logDir)
        val event = LogEvent(LogLevel.INFO, "Tag", "test message")

        interceptor.intercept(event)

        val logFile = interceptor.logFileForDate(event.timestamp)
        assertTrue(logFile.exists())
        val content = logFile.readText()
        assertTrue(content.contains("[INFO]"))
        assertTrue(content.contains("Tag: test message"))
    }

    @Test
    fun `intercept returns the event`() {
        val interceptor = FileLogInterceptor(logDir)
        val event = LogEvent(LogLevel.DEBUG, "Tag", "msg")

        val result = interceptor.intercept(event)

        assertEquals(event, result)
    }

    @Test
    fun `multiple events are appended on separate lines in the same daily file`() {
        val interceptor = FileLogInterceptor(logDir)
        val now = System.currentTimeMillis()

        val events = listOf(
            LogEvent(LogLevel.DEBUG, "T", "first",  timestamp = now),
            LogEvent(LogLevel.INFO,  "T", "second", timestamp = now),
            LogEvent(LogLevel.WARN,  "T", "third",  timestamp = now),
        )

        events.forEach { interceptor.intercept(it) }

        val logFile = interceptor.logFileForDate(now)
        val lines = logFile.readLines().filter { it.isNotBlank() }
        assertEquals(3, lines.size)
        assertTrue(lines[0].contains("first"))
        assertTrue(lines[1].contains("second"))
        assertTrue(lines[2].contains("third"))
    }

    // ---- Daily rotation ----

    @Test
    fun `creates a separate file for each day`() {
        val interceptor = FileLogInterceptor(logDir)
        val day1 = System.currentTimeMillis()
        val day2 = day1 + TimeUnit.DAYS.toMillis(1)

        interceptor.intercept(LogEvent(LogLevel.INFO, "T", "day1 message", timestamp = day1))
        interceptor.intercept(LogEvent(LogLevel.INFO, "T", "day2 message", timestamp = day2))

        val file1 = interceptor.logFileForDate(day1)
        val file2 = interceptor.logFileForDate(day2)

        assertTrue("Day-1 log file should exist", file1.exists())
        assertTrue("Day-2 log file should exist", file2.exists())
        assertTrue(file1.readText().contains("day1 message"))
        assertTrue(file2.readText().contains("day2 message"))
        assertFalse(file1.name == file2.name)
    }

    @Test
    fun `logFileForDate returns file named with baseName and date`() {
        val interceptor = FileLogInterceptor(logDir, baseName = "vonage")
        val dateFormat = SimpleDateFormat(FileLogInterceptor.FILE_DATE_FORMAT, Locale.US)
        val now = System.currentTimeMillis()
        val expectedName = "vonage-${dateFormat.format(now)}.log"

        val file = interceptor.logFileForDate(now)

        assertEquals(expectedName, file.name)
        assertEquals(logDir, file.parentFile)
    }

    // ---- Retention / purge ----

    @Test
    fun `log files older than retentionDays are deleted`() {
        val interceptor = FileLogInterceptor(logDir, retentionDays = 7)
        val now = System.currentTimeMillis()

        // Pre-create a file that is 8 days old
        val oldFile = interceptor.logFileForDate(now - TimeUnit.DAYS.toMillis(8))
        oldFile.createNewFile()
        assertTrue("Old file should exist before purge", oldFile.exists())

        // Writing a new event triggers purge
        interceptor.intercept(LogEvent(LogLevel.INFO, "T", "new entry", timestamp = now))

        assertFalse("Old file should have been purged", oldFile.exists())
    }

    @Test
    fun `log files within retention window are kept`() {
        val interceptor = FileLogInterceptor(logDir, retentionDays = 7)
        val now = System.currentTimeMillis()

        // A file exactly on the boundary (7 days ago) should be kept
        val recentFile = interceptor.logFileForDate(now - TimeUnit.DAYS.toMillis(6))
        recentFile.createNewFile()

        interceptor.intercept(LogEvent(LogLevel.INFO, "T", "today", timestamp = now))

        assertTrue("Recent file should still exist", recentFile.exists())
    }

    @Test
    fun `creates log directory if it does not exist`() {
        val nestedDir = File(logDir, "sub/logs")
        assertFalse(nestedDir.exists())

        val interceptor = FileLogInterceptor(nestedDir)
        interceptor.intercept(LogEvent(LogLevel.INFO, "Tag", "create dirs"))

        assertTrue(nestedDir.exists())
        assertTrue(nestedDir.isDirectory)
    }

    @Test
    fun `when file exceeds max size oldest lines are trimmed and newest line is kept`() {
        val sizingInterceptor = FileLogInterceptor(logDir)
        val firstEvent = LogEvent(LogLevel.INFO, "Tag", "first")
        val secondEvent = LogEvent(LogLevel.INFO, "Tag", "second", timestamp = firstEvent.timestamp)
        val firstLineSize = (sizingInterceptor.formatEvent(firstEvent) + "\n")
            .toByteArray(Charsets.UTF_8)
            .size
            .toLong()
        val secondLineSize = (sizingInterceptor.formatEvent(secondEvent) + "\n")
            .toByteArray(Charsets.UTF_8)
            .size
            .toLong()

        val interceptor = FileLogInterceptor(
            logDir = logDir,
            maxFileSizeBytes = maxOf(firstLineSize, secondLineSize),
        )

        interceptor.intercept(firstEvent)
        interceptor.intercept(secondEvent)

        val logFile = interceptor.logFileForDate(firstEvent.timestamp)
        val content = logFile.readText()

        assertFalse(content.contains("first"))
        assertTrue(content.contains("second"))
    }

    // ---- Constants ----

    @Test
    fun `default constants have expected values`() {
        assertEquals("app", FileLogInterceptor.DEFAULT_BASE_NAME)
        assertEquals(7, FileLogInterceptor.DEFAULT_RETENTION_DAYS)
        assertEquals(50L * 1024 * 1024, FileLogInterceptor.DEFAULT_MAX_FILE_SIZE_BYTES)
        assertEquals("yyyy-MM-dd HH:mm:ss.SSS", FileLogInterceptor.DEFAULT_DATE_FORMAT)
        assertEquals("yyyy-MM-dd", FileLogInterceptor.FILE_DATE_FORMAT)
    }
}




