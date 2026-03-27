package com.vonage.logger.interceptor

import android.util.Log
import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel

class AndroidLogInterceptor(
    private val enabledProvider: () -> Boolean = { true },
    private val minLogLevelProvider: () -> LogLevel = { LogLevel.VERBOSE },
) : LogInterceptor {

    override fun intercept(event: LogEvent): LogEvent {
        if (!enabledProvider() || event.level.ordinal < minLogLevelProvider().ordinal) return event
        when (event.level) {
            LogLevel.VERBOSE -> log(Log::v, event)
            LogLevel.DEBUG -> log(Log::d, event)
            LogLevel.INFO -> log(Log::i, event)
            LogLevel.WARN -> log(Log::w, event)
            LogLevel.ERROR -> log(Log::e, event)
        }
        return event
    }

    private fun log(
        logFn: (String, String, Throwable?) -> Int,
        event: LogEvent,
    ) {
        logFn(event.tag, event.message, event.throwable)
    }
}
