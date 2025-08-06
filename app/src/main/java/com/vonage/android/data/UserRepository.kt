package com.vonage.android.data

import com.vonage.android.data.storage.UserStorage
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userStorage: UserStorage,
) {
    suspend fun saveUserName(userName: String) {
        userStorage.saveUserName(userName)
    }

    suspend fun getUserName(): String =
        userStorage.getUserName().orEmpty()
}
