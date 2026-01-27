package com.vonage.android.kotlin.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PublisherConfigTest {

    @Test
    fun `should create PublisherConfig with all properties`() {
        val config = PublisherConfig(
            name = "John Doe",
            publishVideo = true,
            publishAudio = false,
            blurLevel = BlurLevel.HIGH,
            cameraIndex = 1
        )

        assertEquals("John Doe", config.name)
        assertTrue(config.publishVideo)
        assertFalse(config.publishAudio)
        assertEquals(BlurLevel.HIGH, config.blurLevel)
        assertEquals(1, config.cameraIndex)
    }

    @Test
    fun `should create PublisherConfig with default camera and blur`() {
        val config = PublisherConfig(
            name = "Jane Smith",
            publishVideo = false,
            publishAudio = true,
            blurLevel = BlurLevel.NONE,
            cameraIndex = 0
        )

        assertEquals("Jane Smith", config.name)
        assertFalse(config.publishVideo)
        assertTrue(config.publishAudio)
        assertEquals(BlurLevel.NONE, config.blurLevel)
        assertEquals(0, config.cameraIndex)
    }

    @Test
    fun `should support data class copy`() {
        val original = PublisherConfig(
            name = "Test User",
            publishVideo = true,
            publishAudio = true,
            blurLevel = BlurLevel.LOW,
            cameraIndex = 1
        )

        val copy = original.copy(publishVideo = false)

        assertEquals("Test User", copy.name)
        assertFalse(copy.publishVideo)
        assertTrue(copy.publishAudio)
        assertEquals(BlurLevel.LOW, copy.blurLevel)
        assertEquals(1, copy.cameraIndex)
    }

    @Test
    fun `should support equality comparison`() {
        val config1 = PublisherConfig(
            name = "User",
            publishVideo = true,
            publishAudio = false,
            blurLevel = BlurLevel.HIGH,
            cameraIndex = 0
        )

        val config2 = PublisherConfig(
            name = "User",
            publishVideo = true,
            publishAudio = false,
            blurLevel = BlurLevel.HIGH,
            cameraIndex = 0
        )

        assertEquals(config1, config2)
        assertEquals(config1.hashCode(), config2.hashCode())
    }
}
