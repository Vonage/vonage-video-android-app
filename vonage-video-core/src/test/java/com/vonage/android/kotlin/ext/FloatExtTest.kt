package com.vonage.android.kotlin.ext

import org.junit.Assert.assertEquals
import org.junit.Test

class FloatExtTest {

    @Test
    fun `should round 0_0 to 2 decimal places`() {
        val result = 0.0f.round2()
        assertEquals(0.0f, result, 0.001f)
    }

    @Test
    fun `should round 0_123 to 0_12`() {
        val result = 0.123f.round2()
        assertEquals(0.12f, result, 0.001f)
    }

    @Test
    fun `should round 0_125 to 0_12`() {
        val result = 0.125f.round2()
        assertEquals(0.12f, result, 0.001f) // Banker's rounding or standard rounding
    }

    @Test
    fun `should round 0_999 to 1_0`() {
        val result = 0.999f.round2()
        assertEquals(1.0f, result, 0.001f)
    }

    @Test
    fun `should round 1_234567 to 1_23`() {
        val result = 1.234567f.round2()
        assertEquals(1.23f, result, 0.001f)
    }

    @Test
    fun `should round negative number -0_123 to -0_12`() {
        val result = (-0.123f).round2()
        assertEquals(-0.12f, result, 0.001f)
    }

    @Test
    fun `should round negative number -0_999 to -1_0`() {
        val result = (-0.999f).round2()
        assertEquals(-1.0f, result, 0.001f)
    }

    @Test
    fun `should handle already rounded number`() {
        val result = 0.12f.round2()
        assertEquals(0.12f, result, 0.001f)
    }

    @Test
    fun `should handle single decimal place`() {
        val result = 0.1f.round2()
        assertEquals(0.1f, result, 0.001f)
    }

    @Test
    fun `should round audio level examples`() {
        assertEquals(0.45f, 0.4523f.round2(), 0.001f)
        assertEquals(0.67f, 0.6678f.round2(), 0.001f)
        assertEquals(0.89f, 0.8901f.round2(), 0.001f)
    }
}
