package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLogInterceptor(
    private val file: File,
    private val maxFileSize: Long = DEFAULT_MAX_FILE_SIZE,
    private val dateFormat: String = DEFAULT_DATE_FORMAT,
) : LogInterceptor {

    private val lock = Any()

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

    private fun writeToFile(event: LogEvent) {
        try {
            val line = formatEvent(event) + "\n"
            synchronized(lock) {
                ensureFileExists()
                rotateIfNeeded()
                file.appendText(line)
            }
        } catch (_: IOException) {
            // Silently ignore — logging should never crash the app.
        }
    }

    private fun ensureFileExists() {
        file.parentFile?.mkdirs()
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    private fun rotateIfNeeded() {
        if (file.length() > maxFileSize) {
            file.writeText("")
        }
    }

    companion object {
        /** Default max file size: 5 MB. */
        const val DEFAULT_MAX_FILE_SIZE = 5L * 1024 * 1024

        /** Default timestamp format. */
        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
    }
}
