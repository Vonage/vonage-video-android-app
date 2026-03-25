package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class FileLogInterceptor(
    private val logDir: File,
    private val baseName: String = DEFAULT_BASE_NAME,
    private val retentionDays: Int = DEFAULT_RETENTION_DAYS,
    private val maxFileSizeBytes: Long = DEFAULT_MAX_FILE_SIZE_BYTES,
    private val dateFormat: String = DEFAULT_DATE_FORMAT,
) : LogInterceptor {

    private val lock = Any()

    internal val fileDateFormat = SimpleDateFormat(FILE_DATE_FORMAT, Locale.US)

    override fun intercept(event: LogEvent): LogEvent {
        writeToFile(event)
        return event
    }

    /**
     * Formats a [LogEvent] into a human-readable log line.
     *
     * Visible for testing.
     */
    internal fun formatEvent(event: LogEvent): String {
        val time = SimpleDateFormat(dateFormat, Locale.US).format(Date(event.timestamp))
        val base = "$time [${event.thread}] [${event.level}] ${event.tag}: ${event.message}"
        return if (event.throwable != null) {
            "$base\n${event.throwable}"
        } else {
            base
        }
    }

    internal fun logFileForDate(timestamp: Long = System.currentTimeMillis()): File {
        val dateStr = fileDateFormat.format(Date(timestamp))
        return File(logDir, "$baseName-$dateStr.log")
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
            file.isFile && file.name.startsWith(baseName) && file.name.endsWith(".log")
        }?.forEach { file ->
            val dateStr = file.name
                .removePrefix("$baseName-")
                .removeSuffix(".log")
            runCatching {
                val fileDate = fileDateFormat.parse(dateStr)?.time ?: return@forEach
                if (fileDate < cutoff) file.delete()
            }
        }
    }

    companion object {
        const val DEFAULT_BASE_NAME = "app"

        const val DEFAULT_RETENTION_DAYS = 7

        const val DEFAULT_MAX_FILE_SIZE_BYTES = 50L * 1024 * 1024

        /** Default timestamp format. */
        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"

        internal const val FILE_DATE_FORMAT = "yyyy-MM-dd"
    }
}
