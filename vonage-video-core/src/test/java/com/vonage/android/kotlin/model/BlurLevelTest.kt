package com.vonage.android.kotlin.model

import org.junit.Assert.assertEquals
import org.junit.Test

class BlurLevelTest {

    @Test
    fun `should get LOW blur level by index 0`() {
        val result = BlurLevel by 0
        assertEquals(BlurLevel.LOW, result)
    }

    @Test
    fun `should get HIGH blur level by index 1`() {
        val result = BlurLevel by 1
        assertEquals(BlurLevel.HIGH, result)
    }

    @Test
    fun `should get NONE blur level by index 2`() {
        val result = BlurLevel by 2
        assertEquals(BlurLevel.NONE, result)
    }

    @Test
    fun `should wrap index using modulo for index 3`() {
        val result = BlurLevel by 3
        assertEquals(BlurLevel.LOW, result)
    }

    @Test
    fun `should wrap index using modulo for index 4`() {
        val result = BlurLevel by 4
        assertEquals(BlurLevel.HIGH, result)
    }

    @Test
    fun `should wrap index using modulo for large index`() {
        val result = BlurLevel by 11
        assertEquals(BlurLevel.NONE, result) // 11 % 3 = 2, which is NONE (index 2)
    }

    @Test
    fun `should cycle through values with positive indices`() {
        assertEquals(BlurLevel.LOW, BlurLevel by 0)
        assertEquals(BlurLevel.HIGH, BlurLevel by 1)
        assertEquals(BlurLevel.NONE, BlurLevel by 2)
        assertEquals(BlurLevel.LOW, BlurLevel by 3)
        assertEquals(BlurLevel.HIGH, BlurLevel by 4)
    }

    @Test
    fun `should have correct enum values`() {
        val values = BlurLevel.entries.toTypedArray()
        assertEquals(3, values.size)
        assertEquals(BlurLevel.LOW, values[0])
        assertEquals(BlurLevel.HIGH, values[1])
        assertEquals(BlurLevel.NONE, values[2])
    }
}
