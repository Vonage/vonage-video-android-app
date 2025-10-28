package com.vonage.android.screen.reporting

import android.net.Uri
import android.view.Window
import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import com.vonage.android.data.ReportingRepository
import com.vonage.android.data.network.ReportResponseData
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.util.ImageProcessor
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportingViewModelTest {

    private val reportingRepository: ReportingRepository = mockk()
    private val imageProcessor: ImageProcessor = mockk()
    private val vonageVideoClient: VonageVideoClient = mockk()
    private val sut = ReportingViewModel(
        reportingRepository = reportingRepository,
        imageProcessor = imageProcessor,
        vonageVideoClient = vonageVideoClient
    )

    @Test
    fun `should reset state`() = runTest {
        sut.reset()
        sut.uiState.test {
            assertEquals(ReportIssueScreenUiState(), awaitItem())
        }
    }

    @Test
    fun `should update title`() = runTest {
        sut.updateTitle("title updated")
        sut.uiState.test {
            awaitItem() // initial item
            with(awaitItem()) {
                assertEquals("title updated", title)
                assertTrue(isTitleValid)
            }
        }
    }

    @Test
    fun `should update username`() = runTest {
        sut.updateUsername("user name updated")
        sut.uiState.test {
            awaitItem() // initial item
            with(awaitItem()) {
                assertEquals("user name updated", userName)
                assertTrue(isUsernameValid)
            }
        }
    }

    @Test
    fun `should update description`() = runTest {
        sut.updateDescription("description updated")
        sut.uiState.test {
            awaitItem() // initial item
            with(awaitItem()) {
                assertEquals("description updated", description)
                assertTrue(isDescriptionValid)
            }
        }
    }

    @Test
    fun `should update title with error`() = runTest {
        sut.updateTitle("X".repeat(101))
        sut.uiState.test {
            awaitItem() // initial item
            with(awaitItem()) {
                assertEquals("X".repeat(101), title)
                assertFalse(isTitleValid)
            }
        }
    }

    @Test
    fun `should update username with error`() = runTest {
        sut.updateUsername("X".repeat(101))
        sut.uiState.test {
            awaitItem() // initial item
            with(awaitItem()) {
                assertEquals("X".repeat(101), userName)
                assertFalse(isUsernameValid)
            }
        }
    }

    @Test
    fun `should update description with error`() = runTest {
        sut.updateDescription("X".repeat(1001))
        sut.uiState.test {
            awaitItem() // initial item
            with(awaitItem()) {
                assertEquals("X".repeat(1001), description)
                assertFalse(isDescriptionValid)
            }
        }
    }

    @Test
    fun `should remove attachment`() = runTest {
        sut.uiState.test {
            sut.onRemoveAttachment()
            assertNull(awaitItem().attachment)
        }
    }

    @Test
    fun `should update state when extractImageFromUri success`() = runTest {
        val uri = mockk<Uri>(relaxed = true)
        val imageBitmap = mockk<ImageBitmap>(relaxed = true)
        coEvery { imageProcessor.extractImageFromUri(uri) } returns Result.success(imageBitmap)
        
        sut.processImage(uri)
        
        sut.uiState.test {
            skipItems(1) // skip initial state
            val finalState = awaitItem()
            assertFalse(finalState.isProcessingAttachment)
            assertEquals(imageBitmap, finalState.attachment)
        }
    }

    @Test
    fun `should update state when extractImageFromUri fails`() = runTest {
        val uri = mockk<Uri>(relaxed = true)
        coEvery { imageProcessor.extractImageFromUri(uri) } returns Result.failure(Exception("oops!"))
        
        sut.processImage(uri)

        sut.uiState.test {
            val currentState = awaitItem()
            assertFalse(currentState.isProcessingAttachment)
            assertNull(currentState.attachment)
        }
    }

    @Test
    fun `should update state when extractImageFromWindow success`() = runTest {
        val window = mockk<Window>(relaxed = true)
        val imageBitmap = mockk<ImageBitmap>(relaxed = true)
        coEvery { imageProcessor.extractImageFromWindow(window) } returns Result.success(imageBitmap)
        
        sut.processScreenshot(window)
        
        sut.uiState.test {
            skipItems(1) // skip initial state
            val finalState = awaitItem()
            assertFalse(finalState.isProcessingAttachment)
            assertEquals(imageBitmap, finalState.attachment)
        }
    }

    @Test
    fun `should update state when extractImageFromWindow fails`() = runTest {
        val window = mockk<Window>(relaxed = true)
        coEvery { imageProcessor.extractImageFromWindow(window) } returns Result.failure(Exception("oops!"))
        
        sut.processScreenshot(window)
        delay(100)
        
        sut.uiState.test {
            val currentState = awaitItem()
            assertFalse(currentState.isProcessingAttachment)
            assertNull(currentState.attachment)
        }
    }

    @Test
    fun `should update state when sendReport success`() = runTest {
        coEvery { imageProcessor.encodeImageToBase64(null) } returns ""
        every { vonageVideoClient.debugDump() } returns "debug info from SDK"
        coEvery { reportingRepository.sendReport(any()) } returns
                Result.success(ReportResponseData(
                    message = "message",
                    ticketUrl = "https://jira.host.io/ticket-968",
                    screenshotIncluded = false,
                ))
        
        sut.sendReport("title", "user name", "issue description", null)
        
        sut.uiState.test {
            skipItems(1) // skip initial state
            val finalState = awaitItem()
            assertFalse(finalState.isSending)
            assertFalse(finalState.isError)
            assertEquals(IssueData(
                message = "message",
                ticketUrl = "https://jira.host.io/ticket-968",
            ), finalState.isSuccess)
        }
    }

    @Test
    fun `should update state when sendReport fails`() = runTest {
        val imageBitmap = mockk<ImageBitmap>(relaxed = true)
        coEvery { imageProcessor.encodeImageToBase64(imageBitmap) } returns "base64Image"
        every { vonageVideoClient.debugDump() } returns "debug info from SDK"
        coEvery { reportingRepository.sendReport(any()) } returns Result.failure(Exception("oops!"))
        
        sut.sendReport("title", "user name", "issue description", imageBitmap)
        
        sut.uiState.test {
            skipItems(1) // Skip any initial states
            val finalState = awaitItem()
            assertFalse(finalState.isSending)
            assertTrue(finalState.isError)
            assertNull(finalState.isSuccess)
        }
    }

    @Test
    fun `should update state when sendReport with invalid fields`() = runTest {
        sut.uiState.test {
            awaitItem() // initial item
            sut.sendReport("", "", "", null)
            with(awaitItem()) {
                assertFalse(isTitleValid)
                assertFalse(isUsernameValid)
                assertFalse(isDescriptionValid)
                assertFalse(isSending)
            }
        }
    }
}
