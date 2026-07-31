package com.vonage.android.kotlin.ext

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BooleanExtTest {

    @Test
    fun `should toggle true to false`() {
        val result = true.toggle()
        assertFalse(result)
    }

    @Test
    fun `should toggle false to true`() {
        val result = false.toggle()
        assertTrue(result)
    }

    @Test
    fun `should toggle twice to return original value`() {
        val original = true
        val result = original.toggle().toggle()
        assertTrue(result)
    }

    @Test
    fun `should toggle false twice to return false`() {
        val original = false
        val result = original.toggle().toggle()
        assertFalse(result)
    }

    @Test
    fun `should work with variable assignment`() {
        var enabled = true
        enabled = enabled.toggle()
        assertFalse(enabled)
        
        enabled = enabled.toggle()
        assertTrue(enabled)
    }
}
