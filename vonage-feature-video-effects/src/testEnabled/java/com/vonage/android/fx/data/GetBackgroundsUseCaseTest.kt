package com.vonage.android.fx.data

import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GetBackgroundsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val backgroundEffectsRepository = mockk<BackgroundEffectsRepository>()
    private val userBackgroundRepository = mockk<UserBackgroundRepository>()
    private val sut = GetBackgroundsUseCase(backgroundEffectsRepository, userBackgroundRepository)

    private fun makeItem(id: String) = VideoBackgroundItem(id = id)

    @Test
    fun `given both sources have items when invoked then returns merged list with built-in first`() = runTest {
        // Given
        coEvery { backgroundEffectsRepository.getBackgrounds(any()) } returns persistentListOf(makeItem("b1"), makeItem("b2"))
        coEvery { userBackgroundRepository.getUserBackgrounds(any()) } returns persistentListOf(makeItem("u1"))

        // When
        val result = sut()

        // Then
        assertEquals(listOf("b1", "b2", "u1"), result.backgrounds.map { it.id })
    }

    @Test
    fun `given user count is below MAX when invoked then remainingBackgroundSlots is positive`() = runTest {
        // Given
        coEvery { backgroundEffectsRepository.getBackgrounds(any()) } returns persistentListOf()
        coEvery { userBackgroundRepository.getUserBackgrounds(any()) } returns persistentListOf(makeItem("u1"))

        // When
        val result = sut()

        // Then
        assertTrue(result.remainingBackgroundSlots > 0)
    }

    @Test
    fun `given user count equals MAX when invoked then remainingBackgroundSlots is 0`() = runTest {
        // Given
        val maxUserItems = (1..UserBackgroundRepository.MAX_USER_BACKGROUNDS)
            .map { makeItem("u$it") }
            .toImmutableList()
        coEvery { backgroundEffectsRepository.getBackgrounds(any()) } returns persistentListOf()
        coEvery { userBackgroundRepository.getUserBackgrounds(any()) } returns maxUserItems

        // When
        val result = sut()

        // Then
        assertEquals(0, result.remainingBackgroundSlots)
    }

    @Test
    fun `given built-in source throws when invoked then returns only user items and remainingBackgroundSlots is positive`() = runTest {
        // Given
        coEvery { backgroundEffectsRepository.getBackgrounds(any()) } throws RuntimeException("built-in failure")
        coEvery { userBackgroundRepository.getUserBackgrounds(any()) } returns persistentListOf(makeItem("u1"))

        // When
        val result = sut()

        // Then
        assertEquals(listOf("u1"), result.backgrounds.map { it.id })
        assertTrue(result.remainingBackgroundSlots > 0)
    }

    @Test
    fun `given user source throws when invoked then returns only built-in items and remainingBackgroundSlots is MAX`() = runTest {
        // Given
        coEvery { backgroundEffectsRepository.getBackgrounds(any()) } returns persistentListOf(makeItem("b1"))
        coEvery { userBackgroundRepository.getUserBackgrounds(any()) } throws RuntimeException("user failure")

        // When
        val result = sut()

        // Then
        assertEquals(listOf("b1"), result.backgrounds.map { it.id })
        assertEquals(UserBackgroundRepository.MAX_USER_BACKGROUNDS, result.remainingBackgroundSlots)
    }

    @Test
    fun `given both sources throw when invoked then returns empty list and remainingBackgroundSlots is MAX`() = runTest {
        // Given
        coEvery { backgroundEffectsRepository.getBackgrounds(any()) } throws RuntimeException("built-in failure")
        coEvery { userBackgroundRepository.getUserBackgrounds(any()) } throws RuntimeException("user failure")

        // When
        val result = sut()

        // Then
        assertTrue(result.backgrounds.isEmpty())
        assertEquals(UserBackgroundRepository.MAX_USER_BACKGROUNDS, result.remainingBackgroundSlots)
    }

    @Test
    fun `given both sources are empty when invoked then returns empty list and remainingBackgroundSlots is MAX`() = runTest {
        // Given
        coEvery { backgroundEffectsRepository.getBackgrounds(any()) } returns persistentListOf()
        coEvery { userBackgroundRepository.getUserBackgrounds(any()) } returns persistentListOf()

        // When
        val result = sut()

        // Then
        assertTrue(result.backgrounds.isEmpty())
        assertEquals(UserBackgroundRepository.MAX_USER_BACKGROUNDS, result.remainingBackgroundSlots)
    }

    @Test
    fun `given a capture resolution when invoked then forwards it to both repositories`() = runTest {
        // Given
        coEvery { backgroundEffectsRepository.getBackgrounds(CaptureResolution.MEDIUM) } returns persistentListOf()
        coEvery { userBackgroundRepository.getUserBackgrounds(CaptureResolution.MEDIUM) } returns persistentListOf()

        // When
        sut(CaptureResolution.MEDIUM)

        // Then
        coVerify(exactly = 1) { backgroundEffectsRepository.getBackgrounds(CaptureResolution.MEDIUM) }
        coVerify(exactly = 1) { userBackgroundRepository.getUserBackgrounds(CaptureResolution.MEDIUM) }
    }

    @Test
    fun `given null capture resolution when invoked then forwards null to both repositories`() = runTest {
        // Given
        coEvery { backgroundEffectsRepository.getBackgrounds(null) } returns persistentListOf()
        coEvery { userBackgroundRepository.getUserBackgrounds(null) } returns persistentListOf()

        // When
        sut(null)

        // Then
        coVerify(exactly = 1) { backgroundEffectsRepository.getBackgrounds(null) }
        coVerify(exactly = 1) { userBackgroundRepository.getUserBackgrounds(null) }
    }
}
