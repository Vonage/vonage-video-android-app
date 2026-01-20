package com.vonage.android.kotlin.model

import org.junit.Assert.assertEquals
import org.junit.Test

class VideoSourceTest {

    @Test
    fun `should have CAMERA value`() {
        assertEquals(VideoSource.CAMERA, VideoSource.valueOf("CAMERA"))
    }

    @Test
    fun `should have SCREEN value`() {
        assertEquals(VideoSource.SCREEN, VideoSource.valueOf("SCREEN"))
    }

    @Test
    fun `should have exactly two values`() {
        val values = VideoSource.entries.toTypedArray()
        assertEquals(2, values.size)
    }

    @Test
    fun `should include both CAMERA and SCREEN in values`() {
        val values = VideoSource.entries.toTypedArray()
        assertEquals(VideoSource.CAMERA, values[0])
        assertEquals(VideoSource.SCREEN, values[1])
    }
}
