package com.vonage.android.kotlin.signal

import org.junit.Assert.assertEquals
import org.junit.Test

class RawSignalTest {

    @Test
    fun `should create RawSignal with type and data`() {
        val rawSignal = RawSignal(
            type = "chat",
            data = "{\"message\":\"Hello\"}"
        )

        assertEquals("chat", rawSignal.type)
        assertEquals("{\"message\":\"Hello\"}", rawSignal.data)
    }

    @Test
    fun `should support data class copy`() {
        val original = RawSignal(
            type = "reaction",
            data = "{\"emoji\":\"👍\"}"
        )

        val copy = original.copy(data = "{\"emoji\":\"❤️\"}")

        assertEquals("reaction", copy.type)
        assertEquals("{\"emoji\":\"❤️\"}", copy.data)
    }

    @Test
    fun `should support equality comparison`() {
        val signal1 = RawSignal("type1", "data1")
        val signal2 = RawSignal("type1", "data1")

        assertEquals(signal1, signal2)
        assertEquals(signal1.hashCode(), signal2.hashCode())
    }

    @Test
    fun `should handle empty type and data`() {
        val rawSignal = RawSignal("", "")

        assertEquals("", rawSignal.type)
        assertEquals("", rawSignal.data)
    }
}
