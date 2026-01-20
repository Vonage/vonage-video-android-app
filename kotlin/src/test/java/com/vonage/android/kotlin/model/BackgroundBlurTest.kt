package com.vonage.android.kotlin.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackgroundBlurTest {

    @Test
    fun `should serialize LOW blur level to JSON`() {
        val result = BackgroundBlur.params(BlurLevel.LOW)
        
        assertTrue(result.contains("\"radius\""))
        assertTrue(result.contains("\"Low\""))
    }

    @Test
    fun `should serialize HIGH blur level to JSON`() {
        val result = BackgroundBlur.params(BlurLevel.HIGH)
        
        assertTrue(result.contains("\"radius\""))
        assertTrue(result.contains("\"High\""))
    }

    @Test
    fun `should serialize NONE blur level to JSON`() {
        val result = BackgroundBlur.params(BlurLevel.NONE)
        
        assertTrue(result.contains("\"radius\""))
        assertTrue(result.contains("\"None\""))
    }

    @Test
    fun `should have correct KEY constant`() {
        assertEquals("BackgroundBlur", BackgroundBlur.KEY)
    }

    @Test
    fun `should deserialize JSON back to Radius object`() {
        val json = BackgroundBlur.params(BlurLevel.HIGH)
        val radius = Json.decodeFromString<Radius>(json)
        
        assertEquals(BlurLevel.HIGH, radius.radius)
    }

    @Test
    fun `should round-trip serialize and deserialize`() {
        val levels = listOf(BlurLevel.LOW, BlurLevel.HIGH, BlurLevel.NONE)
        
        levels.forEach { level ->
            val json = BackgroundBlur.params(level)
            val radius = Json.decodeFromString<Radius>(json)
            assertEquals(level, radius.radius)
        }
    }
}
