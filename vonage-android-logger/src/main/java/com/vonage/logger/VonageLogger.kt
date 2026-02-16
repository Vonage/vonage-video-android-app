package com.vonage.logger

import com.vonage.logger.interceptor.LogInterceptor

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
