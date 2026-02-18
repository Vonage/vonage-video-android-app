package com.vonage.android.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ImageProcessorTest {

    private val context: Context = mockk(relaxed = true)
    private val contentResolver: ContentResolver = mockk(relaxed = true)
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private lateinit var imageProcessor: ImageProcessor

    @Before
    fun setUp() {
        every { context.contentResolver } returns contentResolver
        imageProcessor = ImageProcessor(context, testDispatcher)
    }

    @Test
    fun `encodeImageToBase64 should return empty string when imageBitmap is null`() = runTest {
        val result = imageProcessor.encodeImageToBase64(null)

        assertEquals("", result)
    }

    @Test
    fun `extractImageFromUri should return failure when content resolver throws exception`() = runTest {
        val uri = mockk<Uri>()
        val exception = IOException("Failed to open input stream")
        every { contentResolver.openInputStream(uri) } throws exception

        val result = imageProcessor.extractImageFromUri(uri)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `extractImageFromUri should handle null input stream gracefully`() = runTest {
        val uri = mockk<Uri>()
        every { contentResolver.openInputStream(uri) } returns null

        val result = imageProcessor.extractImageFromUri(uri)

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `extractImageFromUri should return failure when bitmap decoding returns null`() = runTest {
        val uri = mockk<Uri>()
        val inputStream = ByteArrayInputStream(byteArrayOf(1, 2, 3))
        mockkStatic(BitmapFactory::class)
        every { contentResolver.openInputStream(uri) } returns inputStream
        every { BitmapFactory.decodeStream(any(), any(), any()) } returns null

        val result = imageProcessor.extractImageFromUri(uri)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception.message?.contains("Unknown error while extracting image from URI") == true)
        unmockkStatic(BitmapFactory::class)
    }

    @Test
    fun `extractImageFromWindow should handle exceptions gracefully`() = runTest {
        val window = mockk<android.view.Window>(relaxed = true)
        val decorView = mockk<android.view.View>(relaxed = true)
        every { window.decorView } returns decorView
        every { decorView.drawingCache } throws RuntimeException("Drawing cache failed")

        val result = imageProcessor.extractImageFromWindow(window)

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}