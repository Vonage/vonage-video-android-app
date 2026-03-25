package com.vonage.logger

import android.content.Context
import com.vonage.logger.interceptor.AndroidLogInterceptor
import com.vonage.logger.interceptor.FileLogInterceptor
import com.vonage.logger.interceptor.LogInterceptor
import java.io.File

/**
 * A simple, direct-call logger that dispatches [LogEvent]s through
 * a list of [LogInterceptor]s.
 *
 * ```kotlin
 * val logger = VonageLogger.Builder()
 *     .addInterceptor(AndroidLogInterceptor())
 *     .build()
 *
 * logger.d("MyApp", "User logged in")
 * logger.e("MyApp", "Something failed", exception)
 * ```
 */
class VonageLogger private constructor(
    private val interceptors: List<LogInterceptor>,
) {

    fun v(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.VERBOSE, tag, message, throwable)

    fun d(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.DEBUG, tag, message, throwable)

    fun i(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.INFO, tag, message, throwable)

    fun w(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.WARN, tag, message, throwable)

    fun e(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.ERROR, tag, message, throwable)

    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
        var event: LogEvent? = LogEvent(level, tag, message, throwable)
        for (interceptor in interceptors) {
            event = interceptor.intercept(event ?: return)
        }
    }

    /**
     * ```kotlin
     * val logger = VonageLogger.Builder()
     *     .addInterceptor(AndroidLogInterceptor())
     *     .build()
     * ```
     */
    class Builder {
        private val interceptors = mutableListOf<LogInterceptor>()

        fun addInterceptor(interceptor: LogInterceptor): Builder = apply {
            interceptors.add(interceptor)
        }

        fun build(): VonageLogger = VonageLogger(interceptors.toList())
    }
}

object DefaultVonageLogger {
    @Volatile
    var log: VonageLogger = VonageLogger.Builder()
        .addInterceptor(AndroidLogInterceptor())
        .build()
        private set

    /**
     * Initializes the default logger with both Logcat and file outputs.
     *
     * File logs rotate daily and retain [retentionDays] days.
     */
    fun init(
        context: Context,
        retentionDays: Int = FileLogInterceptor.DEFAULT_RETENTION_DAYS,
        baseName: String = FileLogInterceptor.DEFAULT_BASE_NAME,
    ) {
        val logDir = File(context.filesDir, "logs")
        log = VonageLogger.Builder()
            .addInterceptor(AndroidLogInterceptor())
            .addInterceptor(
                FileLogInterceptor(
                    logDir = logDir,
                    baseName = baseName,
                    retentionDays = retentionDays,
                ),
            )
            .build()
    }
}

val vonageLogger: VonageLogger
    get() = DefaultVonageLogger.log
