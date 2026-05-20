package com.vonage.android.fx.data

import com.vonage.android.kotlin.model.CaptureResolution
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageCropUtilsTest {

    @Test
    fun `given LOW resolution when computing portrait dimensions then returns 288 by 352`() {
        // Given
        val resolution = CaptureResolution.LOW

        // When
        val result = portraitDimensionsFor(resolution)

        // Then
        assertEquals(288 to 352, result)
    }

    @Test
    fun `given MEDIUM resolution when computing portrait dimensions then returns 480 by 640`() {
        // Given
        val resolution = CaptureResolution.MEDIUM

        // When
        val result = portraitDimensionsFor(resolution)

        // Then
        assertEquals(480 to 640, result)
    }

    @Test
    fun `given HIGH resolution when computing portrait dimensions then returns 720 by 1280`() {
        // Given
        val resolution = CaptureResolution.HIGH

        // When
        val result = portraitDimensionsFor(resolution)

        // Then
        assertEquals(720 to 1280, result)
    }

    @Test
    fun `given HIGH_1080P resolution when computing portrait dimensions then returns 1080 by 1920`() {
        // Given
        val resolution = CaptureResolution.HIGH_1080P

        // When
        val result = portraitDimensionsFor(resolution)

        // Then
        assertEquals(1080 to 1920, result)
    }

    @Test
    fun `given null resolution when computing portrait dimensions then returns default 720 by 1280`() {
        // Given
        val resolution = null

        // When
        val result = portraitDimensionsFor(resolution)

        // Then
        assertEquals(720 to 1280, result)
    }
}
