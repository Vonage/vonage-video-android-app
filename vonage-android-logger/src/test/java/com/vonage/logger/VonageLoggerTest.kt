package com.vonage.logger

import com.vonage.logger.annotations.Debug
import com.vonage.logger.annotations.Error
import com.vonage.logger.annotations.Info
import com.vonage.logger.annotations.Log
import com.vonage.logger.annotations.Tag
import com.vonage.logger.annotations.Verbose
import com.vonage.logger.annotations.Warn
import com.vonage.logger.interceptor.LogInterceptor
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VonageLoggerTest {

    interface SimpleLogger {
        @Debug
        fun onEvent(message: String)
    }

    @Tag("CustomTag")
    interface TaggedLogger {
        @Info
        fun onTaggedEvent()
    }

    interface MultiLevelLogger {
        @Verbose
        fun verbose(msg: String)

        @Debug
        fun debug(msg: String)

        @Info
        fun info(msg: String)

        @Warn
        fun warn(msg: String)

        @Error
        fun error(msg: String)
    }

    interface ErrorLogger {
        @Error
        fun onError(message: String, throwable: Throwable)
    }

    interface LogAnnotationLogger {
        @Log(LogLevel.WARN)
        fun customLevel(data: String)
    }

    // This interface intentionally has a method without a log annotation
    interface InvalidLogger {
        fun noAnnotation()
    }

    interface MultiArgLogger {
        @Debug
        fun onMultiArg(first: String, second: Int, third: Boolean)
    }

    interface NoArgLogger {
        @Info
        fun heartbeat()
    }

    @Test
    fun `create dispatches event through interceptor`() {
        val capturedEvent = slot<LogEvent>()
        val interceptor = mockk<LogInterceptor>()
        every { interceptor.intercept(any()) } answers {
            val chain = firstArg<LogInterceptor.Chain>()
            capturedEvent.captured = chain.event()
            chain.event()
        }

        val logger = VonageLogger.Builder()
            .addInterceptor(interceptor)
            .build()
            .create(SimpleLogger::class.java)

        logger.onEvent("hello")

        assertTrue(capturedEvent.isCaptured)
        assertEquals(LogLevel.DEBUG, capturedEvent.captured.level)
        assertEquals("SimpleLogger", capturedEvent.captured.tag)
        assertTrue(capturedEvent.captured.message.contains("hello"))
    }

    @Test
    fun `custom tag annotation overrides default tag`() {
        val events = mutableListOf<LogEvent>()
        val interceptor = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(interceptor)
            .build()
            .create(TaggedLogger::class.java)

        logger.onTaggedEvent()

        assertEquals(1, events.size)
        assertEquals("CustomTag", events[0].tag)
    }

    @Test
    fun `all log level annotations produce correct levels`() {
        val events = mutableListOf<LogEvent>()
        val interceptor = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(interceptor)
            .build()
            .create(MultiLevelLogger::class.java)

        logger.verbose("v")
        logger.debug("d")
        logger.info("i")
        logger.warn("w")
        logger.error("e")

        assertEquals(5, events.size)
        assertEquals(LogLevel.VERBOSE, events[0].level)
        assertEquals(LogLevel.DEBUG, events[1].level)
        assertEquals(LogLevel.INFO, events[2].level)
        assertEquals(LogLevel.WARN, events[3].level)
        assertEquals(LogLevel.ERROR, events[4].level)
    }

    @Test
    fun `Log annotation with explicit level works`() {
        val events = mutableListOf<LogEvent>()
        val interceptor = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(interceptor)
            .build()
            .create(LogAnnotationLogger::class.java)

        logger.customLevel("test")

        assertEquals(1, events.size)
        assertEquals(LogLevel.WARN, events[0].level)
    }

    @Test
    fun `throwable parameter is extracted into LogEvent throwable field`() {
        val events = mutableListOf<LogEvent>()
        val interceptor = collectingInterceptor(events)

        val exception = RuntimeException("boom")
        val logger = VonageLogger.Builder()
            .addInterceptor(interceptor)
            .build()
            .create(ErrorLogger::class.java)

        logger.onError("something failed", exception)

        assertEquals(1, events.size)
        assertEquals(exception, events[0].throwable)
        assertTrue(events[0].message.contains("something failed"))
        // Throwable should NOT appear in the message text
        assertTrue(!events[0].message.contains("boom"))
    }

    @Test
    fun `no-arg method produces readable message without arguments`() {
        val events = mutableListOf<LogEvent>()
        val interceptor = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(interceptor)
            .build()
            .create(NoArgLogger::class.java)

        logger.heartbeat()

        assertEquals(1, events.size)
        assertEquals("heartbeat []", events[0].message)
        assertNull(events[0].throwable)
    }

    @Test
    fun `multi-arg method includes all args in message`() {
        val events = mutableListOf<LogEvent>()
        val interceptor = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(interceptor)
            .build()
            .create(MultiArgLogger::class.java)

        logger.onMultiArg("hello", 42, true)

        assertEquals(1, events.size)
        assertTrue(events[0].message.contains("hello"))
        assertTrue(events[0].message.contains("42"))
        assertTrue(events[0].message.contains("true"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with non-interface class throws`() {
        VonageLogger.Builder()
            .build()
            .create(String::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with unannotated method throws`() {
        VonageLogger.Builder()
            .build()
            .create(InvalidLogger::class.java)
    }

    @Test
    fun `interceptors are called in order`() {
        val callOrder = mutableListOf<String>()

        val first = object : LogInterceptor {
            override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                callOrder.add("first")
                return chain.proceed(chain.event())
            }
        }
        val second = object : LogInterceptor {
            override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                callOrder.add("second")
                return chain.proceed(chain.event())
            }
        }
        val third = object : LogInterceptor {
            override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                callOrder.add("third")
                return chain.event()
            }
        }

        val logger = VonageLogger.Builder()
            .addInterceptor(first)
            .addInterceptor(second)
            .addInterceptor(third)
            .build()
            .create(SimpleLogger::class.java)

        logger.onEvent("test")

        assertEquals(listOf("first", "second", "third"), callOrder)
    }

    @Test
    fun `interceptor can transform event`() {
        val transforming = object : LogInterceptor {
            override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                val modified = chain.event().copy(tag = "Transformed")
                return chain.proceed(modified)
            }
        }

        val events = mutableListOf<LogEvent>()
        val collecting = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(transforming)
            .addInterceptor(collecting)
            .build()
            .create(SimpleLogger::class.java)

        logger.onEvent("test")

        assertEquals(1, events.size)
        assertEquals("Transformed", events[0].tag)
    }

    @Test
    fun `interceptor can short-circuit the chain`() {
        val blocker = object : LogInterceptor {
            override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                // Don't call chain.proceed — short-circuit
                return chain.event()
            }
        }

        val events = mutableListOf<LogEvent>()
        val collecting = collectingInterceptor(events)

        val logger = VonageLogger.Builder()
            .addInterceptor(blocker)
            .addInterceptor(collecting)
            .build()
            .create(SimpleLogger::class.java)

        logger.onEvent("should not reach second interceptor")

        // Second interceptor should never be called
        assertTrue(events.isEmpty())
    }

    @Test
    fun `no interceptors does not crash`() {
        val logger = VonageLogger.Builder()
            .build()
            .create(SimpleLogger::class.java)

        // Should not throw
        logger.onEvent("nothing listening")
    }

    @Test
    fun `toString returns descriptive string`() {
        val logger = VonageLogger.Builder()
            .build()
            .create(SimpleLogger::class.java)

        assertEquals("VonageLogger proxy for SimpleLogger", logger.toString())
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
