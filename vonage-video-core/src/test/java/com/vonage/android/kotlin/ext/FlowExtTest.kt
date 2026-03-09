package com.vonage.android.kotlin.ext

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowExtTest {

    @Test
    fun `chunked should collect and transform full chunks`() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5, 6)
        val chunked = flow.chunked(3) { it.sum() }

        chunked.test {
            assertEquals(6, awaitItem()) // 1+2+3
            assertEquals(15, awaitItem()) // 4+5+6
            awaitComplete()
        }
    }

    @Test
    fun `chunked should handle incomplete last chunk by not emitting`() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5)
        val chunked = flow.chunked(3) { it.sum() }

        chunked.test {
            assertEquals(6, awaitItem()) // 1+2+3
            // 4+5 is incomplete, should not emit
            awaitComplete()
        }
    }

    @Test
    fun `chunked should handle single chunk`() = runTest {
        val flow = flowOf(1, 2, 3)
        val chunked = flow.chunked(3) { it.joinToString(",") }

        chunked.test {
            assertEquals("1,2,3", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `chunked should handle empty flow`() = runTest {
        val flow = flowOf<Int>()
        val chunked = flow.chunked(3) { it.sum() }

        chunked.test {
            awaitComplete()
        }
    }

    @Test
    fun `chunked should handle size of 1`() = runTest {
        val flow = flowOf(1, 2, 3)
        val chunked = flow.chunked(1) { it.first() * 10 }

        chunked.test {
            assertEquals(10, awaitItem())
            assertEquals(20, awaitItem())
            assertEquals(30, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `chunked should apply custom transform`() = runTest {
        val flow = flowOf("a", "b", "c", "d", "e", "f")
        val chunked = flow.chunked(2) { list ->
            list.joinToString("").uppercase()
        }

        chunked.test {
            assertEquals("AB", awaitItem())
            assertEquals("CD", awaitItem())
            assertEquals("EF", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `chunked should handle multiple incomplete elements`() = runTest {
        val flow = flowOf(1, 2, 3, 4)
        val chunked = flow.chunked(5) { it.sum() }

        chunked.test {
            // Only 4 elements, less than chunk size of 5
            awaitComplete()
        }
    }
}
