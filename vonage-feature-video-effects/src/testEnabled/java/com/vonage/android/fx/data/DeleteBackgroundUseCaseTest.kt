package com.vonage.android.fx.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.Test

class DeleteBackgroundUseCaseTest {

    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private val userBackgroundRepository = mockk<UserBackgroundRepository>()
    private val sut = DeleteBackgroundUseCase(userBackgroundRepository)

    @Test
    fun `given repository successfully deletes when invoked then delegates and returns true`() = runTest {
        // Given
        coEvery { userBackgroundRepository.deleteBackground("bg-1") } returns true

        // When
        val result = sut("bg-1")

        // Then
        assertTrue(result)
        coVerify(exactly = 1) { userBackgroundRepository.deleteBackground("bg-1") }
    }

    @Test
    fun `given repository fails to find item when invoked then returns false`() = runTest {
        // Given
        coEvery { userBackgroundRepository.deleteBackground("bg-missing") } returns false

        // When
        val result = sut("bg-missing")

        // Then
        assertFalse(result)
    }
}
