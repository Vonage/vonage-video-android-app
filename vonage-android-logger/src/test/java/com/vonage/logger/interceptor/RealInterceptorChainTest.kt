package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class RealInterceptorChainTest {

    @Test
    fun `proceed calls interceptors in order`() {
        val order = mutableListOf<Int>()

        val interceptors = listOf(
            object : LogInterceptor {
                override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                    order.add(1)
                    return chain.proceed(chain.event())
                }
            },
            object : LogInterceptor {
                override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                    order.add(2)
                    return chain.event()
                }
            },
        )

        val event = LogEvent(LogLevel.DEBUG, "Tag", "message")
        val chain = RealInterceptorChain(interceptors, 0, event)
        chain.proceed(event)

        assertEquals(listOf(1, 2), order)
    }

    @Test
    fun `proceed passes modified event to next interceptor`() {
        val interceptors = listOf(
            object : LogInterceptor {
                override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                    val modified = chain.event().copy(message = "modified")
                    return chain.proceed(modified)
                }
            },
            object : LogInterceptor {
                override fun intercept(chain: LogInterceptor.Chain): LogEvent {
                    assertEquals("modified", chain.event().message)
                    return chain.event()
                }
            },
        )

        val event = LogEvent(LogLevel.DEBUG, "Tag", "original")
        val chain = RealInterceptorChain(interceptors, 0, event)
        chain.proceed(event)
    }

    @Test(expected = IllegalStateException::class)
    fun `proceed throws when no more interceptors`() {
        val event = LogEvent(LogLevel.DEBUG, "Tag", "message")
        val chain = RealInterceptorChain(emptyList(), 0, event)
        chain.proceed(event)
    }

    @Test
    fun `event returns the current event`() {
        val event = LogEvent(LogLevel.WARN, "MyTag", "hello")
        val chain = RealInterceptorChain(emptyList(), 0, event)

        assertEquals(event, chain.event())
    }
}
