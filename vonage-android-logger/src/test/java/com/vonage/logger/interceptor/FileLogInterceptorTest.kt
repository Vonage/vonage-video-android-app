package com.vonage.logger.interceptor

import com.google.gson.JsonParser
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
import java.util.UUID
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
    fun `formatEvent produces valid JSON without throwable`() {
        val interceptor = FileLogInterceptor(logDir)
        val event = LogEvent(
            LogLevel.DEBUG, "MyTag", "hello world",
            timestamp = 1_000_000_000_000L, thread = "test-thread"
        )

        val line = interceptor.formatEvent(event)
        val json = JsonParser.parseString(line).asJsonObject

        assertTrue(line.startsWith("{"))
        assertTrue(line.endsWith("}"))
        assertTrue(json.has("guid"))
        UUID.fromString(json.get("guid").asString)
        assertTrue(line.contains("\"clientSystemTime\":\"1000000000000\""))
        assertTrue(line.contains("\"level\":\"DEBUG\""))
        assertTrue(line.contains("\"userAgent\":\"test-thread\""))
        assertTrue(line.contains("\"source\":\"MyTag\""))
        assertTrue(line.contains("\"action\":\"hello world\""))
        assertFalse(line.contains("\n"))
    }

    @Test
    fun `formatEvent includes throwable field`() {
        val interceptor = FileLogInterceptor(logDir)
        val error = RuntimeException("boom")
        val event = LogEvent(
            LogLevel.ERROR, "Err", "failed", error,
            timestamp = 1_000_000_000_000L, thread = "err-thread"
        )

        val line = interceptor.formatEvent(event)

        assertTrue(line.contains("\"level\":\"ERROR\""))
        assertTrue(line.contains("\"userAgent\":\"err-thread\""))
        assertTrue(line.contains("\"source\":\"Err\""))
        assertTrue(line.contains("\"action\":\"failed\""))
        assertTrue(line.contains("\"throwable\""))
        assertTrue(line.contains("RuntimeException"))
        assertTrue(line.contains("boom"))
    }

    @Test
    fun `formatEvent generates a different guid for each event`() {
        val interceptor = FileLogInterceptor(logDir)

        val firstGuid = JsonParser.parseString(
            interceptor.formatEvent(LogEvent(LogLevel.INFO, "Tag", "first")),
        ).asJsonObject.get("guid").asString
        val secondGuid = JsonParser.parseString(
            interceptor.formatEvent(LogEvent(LogLevel.INFO, "Tag", "second")),
        ).asJsonObject.get("guid").asString

        assertFalse(firstGuid == secondGuid)
    }

    // ---- File writing ----

    @Test
    fun `intercept writes JSON log line to today's file`() {
        val interceptor = FileLogInterceptor(logDir)
        val event = LogEvent(LogLevel.INFO, "Tag", "test message")

        interceptor.intercept(event)

        val logFile = interceptor.logFileForDate(event.timestamp)
        assertTrue(logFile.exists())
        val content = logFile.readText()
        assertTrue(content.contains("\"level\":\"INFO\""))
        assertTrue(content.contains("\"source\":\"Tag\""))
        assertTrue(content.contains("\"action\":\"test message\""))
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
        assertTrue(lines[0].contains("\"action\":\"first\""))
        assertTrue(lines[1].contains("\"action\":\"second\""))
        assertTrue(lines[2].contains("\"action\":\"third\""))
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
        val expectedName = "vonage-${dateFormat.format(now)}.json.log"
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
    fun `events below min log level are not written to file`() {
        val interceptor = FileLogInterceptor(
            logDir = logDir,
            minLogLevelProvider = { LogLevel.WARN },
        )
        val event = LogEvent(LogLevel.INFO, "Tag", "filtered")

        interceptor.intercept(event)

        val logFile = interceptor.logFileForDate(event.timestamp)
        assertFalse(logFile.exists())
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
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSZ", FileLogInterceptor.DEFAULT_DATE_FORMAT)
        assertEquals("yyyy-MM-dd", FileLogInterceptor.FILE_DATE_FORMAT)
    }
}
