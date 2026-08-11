package com.vonage.android.kotlin.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PublisherConfigTest {

    @Test
    fun `should create PublisherConfig with all properties`() {
        val config = PublisherConfig(
            name = "John Doe",
            publishVideo = true,
            publishAudio = false,
            initialVideoEffect = VideoEffect.BlurHigh,
            cameraIndex = 1,
            senderStatsTrack = true,
        )

        assertEquals("John Doe", config.name)
        assertTrue(config.publishVideo)
        assertFalse(config.publishAudio)
        assertEquals(VideoEffect.BlurHigh, config.initialVideoEffect)
        assertEquals(1, config.cameraIndex)
        assertTrue(config.senderStatsTrack)
    }

    @Test
    fun `should create PublisherConfig with default camera and effect`() {
        val config = PublisherConfig(
            name = "Jane Smith",
            publishVideo = false,
            publishAudio = true,
            initialVideoEffect = VideoEffect.None,
            cameraIndex = 0,
        )

        assertEquals("Jane Smith", config.name)
        assertFalse(config.publishVideo)
        assertTrue(config.publishAudio)
        assertEquals(VideoEffect.None, config.initialVideoEffect)
        assertEquals(0, config.cameraIndex)
        assertFalse(config.senderStatsTrack)
    }

    @Test
    fun `should support data class copy`() {
        val original = PublisherConfig(
            name = "Test User",
            publishVideo = true,
            publishAudio = true,
            initialVideoEffect = VideoEffect.BlurLow,
            cameraIndex = 1,
            senderStatsTrack = true,
        )

        val copy = original.copy(publishVideo = false)

        assertEquals("Test User", copy.name)
        assertFalse(copy.publishVideo)
        assertTrue(copy.publishAudio)
        assertEquals(VideoEffect.BlurLow, copy.initialVideoEffect)
        assertEquals(1, copy.cameraIndex)
        assertTrue(copy.senderStatsTrack)
    }

    @Test
    fun `should copy with senderStatsTrack changed`() {
        val original = PublisherConfig(
            name = "Test User",
            publishVideo = true,
            publishAudio = true,
            initialVideoEffect = VideoEffect.BlurLow,
            cameraIndex = 1,
            senderStatsTrack = false,
        )

        val copy = original.copy(senderStatsTrack = true)

        assertTrue(copy.senderStatsTrack)
        assertEquals(original.name, copy.name)
    }

    @Test
    fun `should support equality comparison`() {
        val config1 = PublisherConfig(
            name = "User",
            publishVideo = true,
            publishAudio = false,
            initialVideoEffect = VideoEffect.BlurHigh,
            cameraIndex = 0,
            senderStatsTrack = true,
        )

        val config2 = PublisherConfig(
            name = "User",
            publishVideo = true,
            publishAudio = false,
            initialVideoEffect = VideoEffect.BlurHigh,
            cameraIndex = 0,
            senderStatsTrack = true,
        )

        assertEquals(config1, config2)
        assertEquals(config1.hashCode(), config2.hashCode())
    }

    @Test
    fun `should create PublisherConfig with custom videoBitrateConfig`() {
        val bitrateConfig = VideoBitrateConfig(
            preset = VideoBitratePreset.CUSTOM,
            maxBitrate = 500_000,
        )
        val config = PublisherConfig(
            name = "User",
            publishVideo = true,
            publishAudio = true,
            initialVideoEffect = VideoEffect.None,
            cameraIndex = 1,
            videoBitrateConfig = bitrateConfig,
        )

        assertEquals(VideoBitratePreset.CUSTOM, config.videoBitrateConfig.preset)
        assertEquals(500_000, config.videoBitrateConfig.maxBitrate)
    }

    @Test
    fun `should have default BW_SAVER videoBitrateConfig`() {
        val config = PublisherConfig(
            name = "User",
            publishVideo = true,
            publishAudio = true,
            initialVideoEffect = VideoEffect.None,
            cameraIndex = 1,
        )

        assertEquals(VideoBitratePreset.DEFAULT, config.videoBitrateConfig.preset)
        assertEquals(
            VideoBitratePreset.DEFAULT.defaultMaxBitrate,
            config.videoBitrateConfig.maxBitrate,
        )
    }
}
