package com.vonage.android.data

import com.vonage.android.data.storage.UserStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserRepositoryTest {

    val userStorage: UserStorage = mockk()
    val sut = UserRepository(
        userStorage = userStorage,
    )

    @Test
    fun `given repository when getUserName then returns value from storage`() = runTest {
        coEvery { userStorage.getUserName() } returns "sample"
        assertEquals("sample", sut.getUserName())
    }

    @Test
    fun `given repository when saveUserName then saves value to storage`() = runTest {
        coEvery { userStorage.saveUserName("saved") } returns Unit
        sut.saveUserName("saved")
        coVerify { userStorage.saveUserName("saved") }
    }
}
