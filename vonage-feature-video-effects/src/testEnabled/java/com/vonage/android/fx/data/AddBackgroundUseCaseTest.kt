package com.vonage.android.fx.data

import android.net.Uri
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class AddBackgroundUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userBackgroundRepository = mockk<UserBackgroundRepository>()
    private val sut = AddBackgroundUseCase(userBackgroundRepository)

    @Test
    fun `given repository returns a saved item when invoked then returns that item`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val expected = VideoBackgroundItem(id = "saved-1", isUserUploaded = true)
        coEvery { userBackgroundRepository.saveBackground(uri, null) } returns expected

        // When
        val result = sut(uri, null)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { userBackgroundRepository.saveBackground(uri, null) }
    }

    @Test
    fun `given a non-null capture resolution when invoked then forwards it to saveBackground`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val expected = VideoBackgroundItem(id = "saved-2", isUserUploaded = true)
        coEvery { userBackgroundRepository.saveBackground(uri, CaptureResolution.LOW) } returns expected

        // When
        sut(uri, CaptureResolution.LOW)

        // Then
        coVerify(exactly = 1) { userBackgroundRepository.saveBackground(uri, CaptureResolution.LOW) }
    }

    @Test
    fun `given repository returns null when invoked then returns null`() = runTest {
        // Given
        val uri = mockk<Uri>()
        coEvery { userBackgroundRepository.saveBackground(uri, null) } returns null

        // When
        val result = sut(uri, null)

        // Then
        assertNull(result)
    }
}
