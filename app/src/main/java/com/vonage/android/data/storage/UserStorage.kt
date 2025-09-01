package com.vonage.android.data.storage

import androidx.datastore.preferences.core.edit
import com.vonage.android.data.storage.GlobalDataStorage.Companion.CAPTIONS_ID
import com.vonage.android.data.storage.GlobalDataStorage.Companion.USER_NAME
import com.vonage.android.util.ext.get
import javax.inject.Inject

class UserStorage @Inject constructor(
    private val globalDataStorage: GlobalDataStorage,
) {
    suspend fun saveUserName(userName: String) {
        globalDataStorage.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }

    suspend fun getUserName(): String? =
        globalDataStorage.get(USER_NAME)

    suspend fun saveCaptionsId(captionsId: String) {
        globalDataStorage.edit { preferences ->
            preferences[CAPTIONS_ID] = captionsId
        }
    }

    suspend fun getCaptionsId(): String? =
        globalDataStorage.get(CAPTIONS_ID)

}
