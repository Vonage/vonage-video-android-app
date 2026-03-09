package com.vonage.android.data.storage

import androidx.datastore.preferences.core.Preferences
import com.vonage.android.data.storage.GlobalDataStorage.Companion.USER_NAME
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserStorageTest {

    private val globalDataStorage: GlobalDataStorage = mockk(relaxed = true)
    private val sut = UserStorage(
        globalDataStorage = globalDataStorage
    )

    @Test
    fun `when saveUserName is called then it completes successfully`() = runTest {
        // Given
        val userName = "test_user_name"

        // When & Then (no exception thrown means success)
        sut.saveUserName(userName)
    }

    @Test
    fun `when saveUserName is called with empty string then it completes successfully`() = runTest {
        // Given
        val userName = ""

        // When & Then (no exception thrown means success)
        sut.saveUserName(userName)
    }

    @Test
    fun `given stored userName when getUserName then returns stored userName`() = runTest {
        // Given
        val storedUserName = "stored_user"
        val mockPreferences = mockk<Preferences> {
            coEvery { get(USER_NAME) } returns storedUserName
        }
        coEvery { globalDataStorage.data } returns flowOf(mockPreferences)

        // When
        val result = sut.getUserName()

        // Then
        assertEquals(storedUserName, result)
    }

    @Test
    fun `given no stored userName when getUserName then returns null`() = runTest {
        // Given
        val mockPreferences = mockk<Preferences> {
            coEvery { get(USER_NAME) } returns null
        }
        coEvery { globalDataStorage.data } returns flowOf(mockPreferences)

        // When
        val result = sut.getUserName()

        // Then
        assertNull(result)
    }

    @Test
    fun `given empty datastore when getUserName then returns null`() = runTest {
        // Given
        coEvery { globalDataStorage.data } returns flowOf()

        // When
        val result = sut.getUserName()

        // Then
        assertNull(result)
    }

    @Test
    fun `given userName with special characters when getUserName then returns correct userName`() = runTest {
        // Given
        val specialUserName = "user@domain.com"
        val mockPreferences = mockk<Preferences> {
            coEvery { get(USER_NAME) } returns specialUserName
        }
        coEvery { globalDataStorage.data } returns flowOf(mockPreferences)

        // When
        val result = sut.getUserName()

        // Then
        assertEquals(specialUserName, result)
    }

    @Test
    fun `given very long userName when getUserName then returns correct userName`() = runTest {
        // Given
        val longUserName = "a".repeat(1000)
        val mockPreferences = mockk<Preferences> {
            coEvery { get(USER_NAME) } returns longUserName
        }
        coEvery { globalDataStorage.data } returns flowOf(mockPreferences)

        // When
        val result = sut.getUserName()

        // Then
        assertEquals(longUserName, result)
    }
}
