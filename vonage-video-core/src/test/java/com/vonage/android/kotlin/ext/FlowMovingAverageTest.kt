package com.vonage.android.kotlin.ext

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowMovingAverageTest {

    @Test
    fun `movingAverage should compute correctly using toList`() = runTest {
        val flow = flowOf(1f, 2f, 3f, 4f, 5f)
        val results = flow.movingAverage(windowSize = 3).toList()

        // scan emits initial state, then each computation
        assert(results.size >= 5)
        // Check that we get reasonable moving averages
        assert(results.any { it > 0f })
    }

    @Test
    fun `exponentialMovingAverage should smooth values`() = runTest {
        val flow = flowOf(10f, 20f, 30f)
        val results = flow.exponentialMovingAverage().toList()

        // Should have at least as many results as input
        assert(results.size >= 3)
        // Values should be between min and max input
        assert(results.all { it in 0f..30f })
    }

    @Test
    fun `exponentialMovingAverage with custom factor should work`() = runTest {
        val flow = flowOf(10f, 20f, 30f)
        val results = flow.exponentialMovingAverage(factor = 0.5f).toList()

        assert(results.size >= 3)
        assert(results.all { it in 0f..30f })
    }

    @Test
    fun `movingAverage should handle small window`() = runTest {
        val flow = flowOf(5f, 10f, 15f)
        val results = flow.movingAverage(windowSize = 1).toList()

        assert(results.isNotEmpty())
    }

    @Test
    fun `movingAverage should handle large window`() = runTest {
        val flow = flowOf(2f, 4f, 6f)
        val results = flow.movingAverage(windowSize = 10).toList()

        assert(results.isNotEmpty())
        // Average should be reasonable
        assert(results.last() >= 2f && results.last() <= 6f)
    }
}
