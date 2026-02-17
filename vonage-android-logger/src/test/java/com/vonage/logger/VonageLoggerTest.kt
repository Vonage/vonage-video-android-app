package com.vonage.logger

import com.vonage.logger.interceptor.LogInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VonageLoggerTest {

    private fun collectingInterceptor(events: MutableList<LogEvent>): LogInterceptor =
        LogInterceptor { event ->
            events.add(event)
            event
        }

    @Test
    fun `log dispatches event through interceptor`() {
        val events = mutableListOf<LogEvent>()

        val logger = VonageLogger.Builder()
            .addInterceptor(collectingInterceptor(events))
            .build()

        logger.d("TestTag", "hello")

        assertEquals(1, events.size)
        assertEquals(LogLevel.DEBUG, events[0].level)
        assertEquals("TestTag", events[0].tag)
        assertEquals("hello", events[0].message)
    }

    @Test
    fun `tag is set per call`() {
        val events = mutableListOf<LogEvent>()

        val logger = VonageLogger.Builder()
            .addInterceptor(collectingInterceptor(events))
            .build()

        logger.i("TagA", "first")
        logger.i("TagB", "second")

        assertEquals(2, events.size)
        assertEquals("TagA", events[0].tag)
        assertEquals("TagB", events[1].tag)
    }

    @Test
    fun `all log level methods produce correct levels`() {
        val events = mutableListOf<LogEvent>()

        val logger = VonageLogger.Builder()
            .addInterceptor(collectingInterceptor(events))
            .build()

        logger.v("T", "v")
        logger.d("T", "d")
        logger.i("T", "i")
        logger.w("T", "w")
        logger.e("T", "e")

        assertEquals(5, events.size)
        assertEquals(LogLevel.VERBOSE, events[0].level)
        assertEquals(LogLevel.DEBUG, events[1].level)
        assertEquals(LogLevel.INFO, events[2].level)
        assertEquals(LogLevel.WARN, events[3].level)
        assertEquals(LogLevel.ERROR, events[4].level)
    }

    @Test
    fun `throwable is passed into LogEvent`() {
        val events = mutableListOf<LogEvent>()
        val exception = RuntimeException("boom")

        val logger = VonageLogger.Builder()
            .addInterceptor(collectingInterceptor(events))
            .build()

        logger.e("T", "something failed", exception)

        assertEquals(1, events.size)
        assertEquals(exception, events[0].throwable)
        assertEquals("something failed", events[0].message)
    }

    @Test
    fun `message without throwable has null throwable`() {
        val events = mutableListOf<LogEvent>()

        val logger = VonageLogger.Builder()
            .addInterceptor(collectingInterceptor(events))
            .build()

        logger.i("T", "heartbeat")

        assertEquals(1, events.size)
        assertEquals("heartbeat", events[0].message)
        assertNull(events[0].throwable)
    }

    @Test
    fun `interceptors are called in order`() {
        val callOrder = mutableListOf<String>()

        val first = LogInterceptor { event ->
            callOrder.add("first")
            event
        }
        val second = LogInterceptor { event ->
            callOrder.add("second")
            event
        }
        val third = LogInterceptor { event ->
            callOrder.add("third")
            event
        }

        val logger = VonageLogger.Builder()
            .addInterceptor(first)
            .addInterceptor(second)
            .addInterceptor(third)
            .build()

        logger.d("T", "test")

        assertEquals(listOf("first", "second", "third"), callOrder)
    }

    @Test
    fun `interceptor can transform event`() {
        val transforming = LogInterceptor { event ->
            event.copy(tag = "Transformed")
        }

        val events = mutableListOf<LogEvent>()
        val collecting = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(transforming)
            .addInterceptor(collecting)
            .build()

        logger.d("Original", "test")

        assertEquals(1, events.size)
        assertEquals("Transformed", events[0].tag)
    }

    @Test
    fun `interceptor can short-circuit by returning null`() {
        val blocker = LogInterceptor { null }

        val events = mutableListOf<LogEvent>()
        val collecting = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(blocker)
            .addInterceptor(collecting)
            .build()

        logger.d("T", "should not reach second interceptor")

        assertTrue(events.isEmpty())
    }

    @Test
    fun `no interceptors does not crash`() {
        val logger = VonageLogger.Builder().build()

        // Should not throw
        logger.d("T", "nothing listening")
    }

    @Test
    fun `log method with explicit level works`() {
        val events = mutableListOf<LogEvent>()

        val logger = VonageLogger.Builder()
            .addInterceptor(collectingInterceptor(events))
            .build()

        logger.log(LogLevel.WARN, "T", "explicit warn")

        assertEquals(1, events.size)
        assertEquals(LogLevel.WARN, events[0].level)
        assertEquals("explicit warn", events[0].message)
    }

    @Test
    fun `event contains timestamp and thread`() {
        val events = mutableListOf<LogEvent>()

        val logger = VonageLogger.Builder()
            .addInterceptor(collectingInterceptor(events))
            .build()

        val before = System.currentTimeMillis()
        logger.d("T", "timestamped")
        val after = System.currentTimeMillis()

        assertEquals(1, events.size)
        assertTrue(events[0].timestamp in before..after)
        assertEquals(Thread.currentThread().name, events[0].thread)
    }
}
