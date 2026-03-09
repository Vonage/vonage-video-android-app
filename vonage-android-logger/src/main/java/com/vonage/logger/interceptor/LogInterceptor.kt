package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent

/**
 * Intercepts a [LogEvent] before it is delivered.
 *
 * Return the (possibly modified) event to continue processing,
 * or `null` to drop the event and stop the pipeline.
 */
fun interface LogInterceptor {
    fun intercept(event: LogEvent): LogEvent?
}
