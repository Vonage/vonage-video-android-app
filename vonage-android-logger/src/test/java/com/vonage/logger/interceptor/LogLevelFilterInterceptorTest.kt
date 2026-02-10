package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class LogLevelFilterInterceptorTest {

    @Test
    fun `events at or above minLevel pass through`() {
        val passed = mutableListOf<LogEvent>()
        val terminal = collectingInterceptor(passed)

        val interceptors = listOf(
            LogLevelFilterInterceptor(LogLevel.WARN),
            terminal,
        )

        dispatch(interceptors, LogLevel.WARN, "warning")
        dispatch(interceptors, LogLevel.ERROR, "error")

        assertEquals(2, passed.size)
        assertEquals(LogLevel.WARN, passed[0].level)
        assertEquals(LogLevel.ERROR, passed[1].level)
    }

    @Test
    fun `events below minLevel are filtered out`() {
        val passed = mutableListOf<LogEvent>()
        val terminal = collectingInterceptor(passed)

        val interceptors = listOf(
            LogLevelFilterInterceptor(LogLevel.WARN),
            terminal,
        )

        dispatch(interceptors, LogLevel.VERBOSE, "verbose")
        dispatch(interceptors, LogLevel.DEBUG, "debug")
        dispatch(interceptors, LogLevel.INFO, "info")

        assertEquals(0, passed.size)
    }

    @Test
    fun `filter with VERBOSE passes all events`() {
        val passed = mutableListOf<LogEvent>()
        val terminal = collectingInterceptor(passed)

        val interceptors = listOf(
            LogLevelFilterInterceptor(LogLevel.VERBOSE),
            terminal,
        )

        LogLevel.entries.forEach { level ->
            dispatch(interceptors, level, level.name)
        }

        assertEquals(LogLevel.entries.size, passed.size)
    }

    @Test
    fun `filter with ERROR only passes ERROR events`() {
        val passed = mutableListOf<LogEvent>()
        val terminal = collectingInterceptor(passed)

        val interceptors = listOf(
            LogLevelFilterInterceptor(LogLevel.ERROR),
            terminal,
        )

        LogLevel.entries.forEach { level ->
            dispatch(interceptors, level, level.name)
        }

        assertEquals(1, passed.size)
        assertEquals(LogLevel.ERROR, passed[0].level)
    }

    // ---- Helpers ----

    private fun dispatch(
        interceptors: List<LogInterceptor>,
        level: LogLevel,
        message: String,
    ) {
        val event = LogEvent(level, "Test", message)
        val chain = RealInterceptorChain(interceptors, 0, event)
        chain.proceed(event)
    }

    private fun collectingInterceptor(events: MutableList<LogEvent>): LogInterceptor {
        return object : LogInterceptor {
            override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                events.add(chain.event())
                return chain.event()
            }
        }
    }
}
