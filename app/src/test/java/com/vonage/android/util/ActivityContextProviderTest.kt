package com.vonage.android.util

import android.content.Context
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ActivityContextProviderTest {

    private lateinit var provider: ActivityContextProvider
    private val mockContext: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        provider = ActivityContextProvider()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should store and retrieve activity context`() {
        provider.setActivityContext(mockContext)

        val result = provider.getActivityContext()

        assertEquals(mockContext, result)
    }

    @Test
    fun `should return null when no context is set`() {
        val result = provider.getActivityContext()

        assertNull(result)
    }

    @Test
    fun `should return context when requireActivityContext is called after setting`() {
        provider.setActivityContext(mockContext)

        val result = provider.requireActivityContext()

        assertEquals(mockContext, result)
    }

    @Test
    fun `should clear context properly`() {
        provider.setActivityContext(mockContext)

        provider.clearActivityContext()
        val result = provider.getActivityContext()

        assertNull(result)
    }
}