package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel

class LogLevelFilterInterceptor(
    private val minLevel: LogLevel,
) : LogInterceptor {

    override fun intercept(chain: LogInterceptor.Chain): LogEvent {
        val event = chain.event()
        return if (event.level.ordinal >= minLevel.ordinal) {
            chain.proceed(event)
        } else {
            event
        }
    }
}
