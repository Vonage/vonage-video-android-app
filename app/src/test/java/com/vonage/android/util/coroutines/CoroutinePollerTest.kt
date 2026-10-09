package com.vonage.android.util.coroutines

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinePollerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockFetchData: suspend () -> String = mockk()

    private fun createSut() = CoroutinePoller(
        dispatcher = testDispatcher,
        fetchData = mockFetchData
    )

    @Test
    fun `given poller when poll is called then emits data at specified intervals`() = runTest(testDispatcher) {
        // Given
        val pollingDelay = 1000L
        val expectedData = listOf("data1", "data2", "data3")
        coEvery { mockFetchData() } returnsMany expectedData
        val sut = createSut()

        // When & Then
        sut.poll(pollingDelay).test {
            // First emission
            assertEquals("data1", awaitItem())
            
            // Advance time by polling delay
            advanceTimeBy(pollingDelay)
            assertEquals("data2", awaitItem())
            
            // Advance time by polling delay again
            advanceTimeBy(pollingDelay)
            assertEquals("data3", awaitItem())
            
            // Cancel the flow
            cancel()
        }
        
        // Verify fetchData was called the expected number of times
        coVerify(exactly = 3) { mockFetchData() }
    }

    @Test
    fun `given poller when poll is called with different delay then uses correct timing`() = runTest(testDispatcher) {
        // Given
        val pollingDelay = 500L
        coEvery { mockFetchData() } returns "test_data"
        val sut = createSut()

        // When & Then
        sut.poll(pollingDelay).test {
            // First emission should happen immediately
            assertEquals("test_data", awaitItem())
            
            // Second emission after delay
            advanceTimeBy(pollingDelay)
            assertEquals("test_data", awaitItem())
            
            // Third emission after another delay
            advanceTimeBy(pollingDelay)
            assertEquals("test_data", awaitItem())
            
            cancel()
        }
        
        coVerify(atLeast = 3) { mockFetchData() }
    }

    @Test
    fun `given poller when fetchData throws exception then flow terminates`() = runTest(testDispatcher) {
        // Given
        val pollingDelay = 1000L
        coEvery { mockFetchData() } throws RuntimeException("Test error")
        val sut = createSut()

        // When & Then
        sut.poll(pollingDelay).test {
            // The flow should terminate due to the exception
            awaitError()
        }
        
        coVerify(exactly = 1) { mockFetchData() }
    }

    @Test
    fun `given poller when cancel is called then stops polling`() = runTest(testDispatcher) {
        // Given
        val pollingDelay = 1000L
        coEvery { mockFetchData() } returns "test_data"
        val sut = createSut()

        // When
        sut.poll(pollingDelay).test {
            assertEquals("test_data", awaitItem())
            
            // Cancel the poller
            sut.cancel()
            
            // Advance time and verify no more emissions
            advanceTimeBy(pollingDelay * 2)
            expectNoEvents()
            
            cancel()
        }
        
        // Verify job is cleaned up
        assertNull(sut.job)
    }

    @Test
    fun `given poller when cancel is called before polling then job is null`() = runTest {
        // Given
        val sut = createSut()
        
        // When
        sut.cancel()
        
        // Then
        assertNull(sut.job)
    }

    @Test
    fun `given poller when multiple poll calls then cancels previous job`() = runTest(testDispatcher) {
        // Given
        val pollingDelay = 1000L
        coEvery { mockFetchData() } returns "test_data"
        val sut = createSut()

        // When - Start first polling operation
        var firstJob: Job? = null
        sut.poll(pollingDelay).test {
            assertEquals("test_data", awaitItem())
            firstJob = sut.job
            cancel()
        }
        
        // Start a new polling operation
        sut.poll(pollingDelay).test {
            assertEquals("test_data", awaitItem())
            val secondJob = sut.job
            
            // Verify we have a new job
            assertTrue(firstJob != secondJob)
            cancel()
        }
    }

    @Test
    fun `given poller when flow is closed then cancels job automatically`() = runTest(testDispatcher) {
        // Given
        val pollingDelay = 1000L
        coEvery { mockFetchData() } returns "test_data"
        val sut = createSut()

        // When
        sut.poll(pollingDelay).test {
            assertEquals("test_data", awaitItem())
            // Flow gets closed when test block ends
        }
        
        // Then - job should be cancelled automatically
        assertNull(sut.job)
    }

    @Test
    fun `given poller when fetchData returns different types then emits correctly`() = runTest(testDispatcher) {
        // Given
        val pollingDelay = 1000L
        val mockIntFetchData: suspend () -> Int = mockk()
        coEvery { mockIntFetchData() } returnsMany listOf(1, 2, 3)
        
        val intPoller = CoroutinePoller(testDispatcher, mockIntFetchData)

        // When & Then
        intPoller.poll(pollingDelay).test {
            assertEquals(1, awaitItem())
            
            advanceTimeBy(pollingDelay)
            assertEquals(2, awaitItem())
            
            advanceTimeBy(pollingDelay)
            assertEquals(3, awaitItem())
            
            cancel()
        }
    }

    @Test
    fun `given poller when job is active then job property reflects correct state`() = runTest(testDispatcher) {
        // Given
        val pollingDelay = 1000L
        coEvery { mockFetchData() } returns "test_data"
        val sut = createSut()

        // Initially no job
        assertNull(sut.job)

        // When
        sut.poll(pollingDelay).test {
            // Job should be active during polling
            assertTrue(sut.job?.isActive == true)
            
            awaitItem() // Consume first emission
            cancel()
        }
        
        // After cancellation, job should be null
        assertNull(sut.job)
    }
}