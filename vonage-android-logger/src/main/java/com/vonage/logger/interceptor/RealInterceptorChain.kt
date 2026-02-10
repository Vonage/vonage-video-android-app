package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent

internal class RealInterceptorChain(
    private val interceptors: List<LogInterceptor>,
    private val index: Int,
    private val event: LogEvent,
) : LogInterceptor.Chain {

    override fun event(): LogEvent = event

    override fun proceed(event: LogEvent): LogEvent {
        check(index < interceptors.size) {
            "No more interceptors in the chain."
        }
        val next = RealInterceptorChain(interceptors, index + 1, event)
        val interceptor = interceptors[index]
        return interceptor.intercept(next)
    }
}
