package com.vonage.logger.interceptor

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class FileLogInterceptor(
    private val logDir: File,
    private val baseName: String = DEFAULT_BASE_NAME,
    private val retentionDays: Int = DEFAULT_RETENTION_DAYS,
    private val maxFileSizeBytes: Long = DEFAULT_MAX_FILE_SIZE_BYTES,
    private val enabledProvider: () -> Boolean = { true },
    private val minLogLevelProvider: () -> LogLevel = { LogLevel.VERBOSE },
) : LogInterceptor {

    private val lock = Any()
    private val gson = GsonBuilder().disableHtmlEscaping().create()

    internal val fileDateFormat = SimpleDateFormat(FILE_DATE_FORMAT, Locale.US)

    override fun intercept(event: LogEvent): LogEvent {
        if (!enabledProvider() || event.level.ordinal < minLogLevelProvider().ordinal) return event
        writeToFile(event)
        return event
    }

    /**
     * Formats a [LogEvent] into a single-line JSON object.
     *
     * Visible for testing.
     */
    internal fun formatEvent(event: LogEvent): String {
        //use Epoch millis for easier parsing and sorting in log management tools
        val obj = JsonObject()
        obj.addProperty("guid", UUID.randomUUID().toString())
        obj.addProperty("clientSystemTime", event.timestamp)
        obj.addProperty("level", event.level.name.lowercase())
        obj.addProperty("userAgent", event.thread)
        obj.addProperty("source", event.tag)
        obj.addProperty("action", event.message)
        if (event.throwable != null) {
            val sw = StringWriter()
            event.throwable.printStackTrace(PrintWriter(sw))
            obj.addProperty("throwable", sw.toString().trimEnd())
        }
        return gson.toJson(obj)
    }

    internal fun logFileForDate(timestamp: Long = System.currentTimeMillis()): File {
        val dateStr = fileDateFormat.format(Date(timestamp))
        return File(logDir, "$baseName-$dateStr.json.log")
    }

    private fun writeToFile(event: LogEvent) {
        try {
            val line = formatEvent(event) + "\n"
            synchronized(lock) {
                logDir.mkdirs()
                val target = logFileForDate(event.timestamp)
                if (!target.exists()) target.createNewFile()
                appendWithSizeLimit(target, line)
                purgeOldLogs(event.timestamp)
            }
        } catch (_: IOException) {
            // Silently ignore — logging should never crash the app.
        }
    }

    private fun appendWithSizeLimit(target: File, newContent: String) {
        if (target.length() + newContent.toByteArray(Charsets.UTF_8).size <= maxFileSizeBytes) {
            target.appendText(newContent, Charsets.UTF_8)
            return
        }

        val incomingLines = newContent
            .removeSuffix("\n")
            .lineSequence()
            .toList()

        val combinedLines = buildList {
            if (target.exists()) addAll(target.readLines(Charsets.UTF_8))
            addAll(incomingLines)
        }.toMutableList()

        while (combinedLines.isNotEmpty() && textSizeInBytes(combinedLines) > maxFileSizeBytes) {
            combinedLines.removeAt(0)
        }

        val trimmedLines = if (combinedLines.isEmpty()) {
            emptyList()
        } else {
            val lastLine = combinedLines.last()
            if (combinedLines.size == 1 && textSizeInBytes(combinedLines) > maxFileSizeBytes) {
                listOf(trimToByteLimit(lastLine, maxFileSizeBytes))
            } else {
                combinedLines
            }
        }

        val content = if (trimmedLines.isEmpty()) {
            ""
        } else {
            trimmedLines.joinToString(separator = "\n", postfix = "\n")
        }

        target.writeText(content, Charsets.UTF_8)
    }

    private fun textSizeInBytes(lines: List<String>): Long =
        if (lines.isEmpty()) 0L else lines.joinToString(separator = "\n", postfix = "\n").toByteArray(Charsets.UTF_8).size.toLong()

    private fun trimToByteLimit(text: String, byteLimit: Long): String {
        if (text.toByteArray(Charsets.UTF_8).size <= byteLimit) return text

        var startIndex = text.length
        var keptBytes = 0L

        while (startIndex > 0) {
            val codePoint = text.codePointBefore(startIndex)
            val charCount = Character.charCount(codePoint)
            val chunk = String(Character.toChars(codePoint))
            val chunkBytes = chunk.toByteArray(Charsets.UTF_8).size.toLong()
            if (keptBytes + chunkBytes > byteLimit) break
            keptBytes += chunkBytes
            startIndex -= charCount
        }

        return text.substring(startIndex)
    }

    private fun purgeOldLogs(now: Long) {
        val cutoff = now - TimeUnit.DAYS.toMillis(retentionDays.toLong())
        logDir.listFiles { file ->
            file.isFile && file.name.startsWith(baseName) && file.name.endsWith(".json.log")
        }?.forEach { file ->
            val dateStr = file.name
                .removePrefix("$baseName-")
                .removeSuffix(".json.log")
            runCatching {
                val fileDate = fileDateFormat.parse(dateStr)?.time ?: return@forEach
                if (fileDate < cutoff) file.delete()
            }
        }
    }

    companion object {
        const val DEFAULT_BASE_NAME = "app"

        const val DEFAULT_RETENTION_DAYS = 3

        const val DEFAULT_MAX_FILE_SIZE_BYTES = 50L * 1024 * 1024

        /** Default timestamp format (ISO-8601). */
        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

        internal const val FILE_DATE_FORMAT = "yyyy-MM-dd"
    }
}
