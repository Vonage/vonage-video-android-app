package com.vonage.android.kotlin.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CameraTypeTest {

    @Test
    fun `should get UNKNOWN camera type from index -1`() {
        val result = CameraType fromInt -1
        assertEquals(CameraType.UNKNOWN, result)
    }

    @Test
    fun `should get BACK camera type from index 0`() {
        val result = CameraType fromInt 0
        assertEquals(CameraType.BACK, result)
    }

    @Test
    fun `should get FRONT camera type from index 1`() {
        val result = CameraType fromInt 1
        assertEquals(CameraType.FRONT, result)
    }

    @Test
    fun `should return null for invalid index 2`() {
        val result = CameraType fromInt 2
        assertNull(result)
    }

    @Test
    fun `should return null for invalid index 99`() {
        val result = CameraType fromInt 99
        assertNull(result)
    }

    @Test
    fun `should have correct index values`() {
        assertEquals(-1, CameraType.UNKNOWN.index)
        assertEquals(0, CameraType.BACK.index)
        assertEquals(1, CameraType.FRONT.index)
    }

    @Test
    fun `should have correct enum values`() {
        val values = CameraType.entries.toTypedArray()
        assertEquals(3, values.size)
        assertEquals(CameraType.UNKNOWN, values[0])
        assertEquals(CameraType.BACK, values[1])
        assertEquals(CameraType.FRONT, values[2])
    }
}
