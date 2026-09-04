package com.vonage.logger

import com.vonage.logger.interceptor.AndroidLogInterceptor
import com.vonage.logger.interceptor.LogInterceptor

/**
 * A simple, direct-call logger that dispatches [LogEvent]s through
 * a list of [LogInterceptor]s.
 *
 * ```kotlin
 * val logger = VonageLogger(AndroidLogInterceptor())
 * logger.d("MyApp", "User logged in")
 * logger.e("MyApp", "Something failed", exception)
 * ```
 */
class VonageLogger(
    private vararg val interceptors: LogInterceptor,
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
}

val vonageLogger: VonageLogger = VonageLogger(AndroidLogInterceptor())
