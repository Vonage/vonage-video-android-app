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
        val newBytes = newContent.toByteArray(Charsets.UTF_8)
        if (target.length() + newBytes.size <= maxFileSizeBytes) {
            target.appendText(newContent, Charsets.UTF_8)
            return
        }

        // Each line is stored as its raw UTF-8 bytes; size accounting adds 1 byte per trailing newline.
        val newlineBytes = 1L  // "\n" is always 1 byte in UTF-8
        val lineQueue = ArrayDeque<ByteArray>()
        var totalBytes = 0L

        if (target.exists() && target.length() > 0) {
            target.forEachLine(Charsets.UTF_8) { line ->
                val lb = line.toByteArray(Charsets.UTF_8)
                lineQueue.addLast(lb)
                totalBytes += lb.size + newlineBytes
            }
        }

        // Enqueue incoming lines and update the counter incrementally — no repeated joins.
        newContent.removeSuffix("\n").split("\n").forEach { line ->
            val lb = line.toByteArray(Charsets.UTF_8)
            lineQueue.addLast(lb)
            totalBytes += lb.size + newlineBytes
        }

        // Drop oldest lines one-by-one; each removal is O(1).
        while (lineQueue.size > 1 && totalBytes > maxFileSizeBytes) {
            val removed = lineQueue.removeFirst()
            totalBytes -= removed.size + newlineBytes
        }

        // Edge case: single remaining line still exceeds the cap — keep only its tail bytes.
        if (lineQueue.size == 1 && totalBytes > maxFileSizeBytes) {
            val trimmed = trimToByteLimit(
                String(lineQueue.first(), Charsets.UTF_8),
                maxFileSizeBytes - newlineBytes
            )
            lineQueue[0] = trimmed.toByteArray(Charsets.UTF_8)
        }

        // Write in one streaming pass — no giant intermediate String allocation.
        val newline = byteArrayOf('\n'.code.toByte())
        target.outputStream().buffered().use { out ->
            for (lb in lineQueue) {
                out.write(lb)
                out.write(newline)
            }
        }
    }

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
            file.isFile && file.name.startsWith("$baseName-") && file.name.endsWith(".json.log")
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
