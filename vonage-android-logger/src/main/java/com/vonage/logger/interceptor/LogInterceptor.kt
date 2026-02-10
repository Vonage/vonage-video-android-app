package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent

interface LogInterceptor {

    fun intercept(chain: Chain): LogEvent

    interface Chain {
        fun event(): LogEvent
        fun proceed(event: LogEvent): LogEvent
    }
}
