package com.vonage.android.data.storage

import androidx.datastore.preferences.core.edit
import com.vonage.android.data.storage.GlobalDataStorage.Companion.CLIENT_LOGS_ENABLED
import com.vonage.android.data.storage.GlobalDataStorage.Companion.CLIENT_LOG_LEVEL
import com.vonage.android.util.ext.get
import com.vonage.logger.LogLevel
import javax.inject.Inject

class ClientLogsSettingsStorage @Inject constructor(
    private val globalDataStorage: GlobalDataStorage,
) {
    suspend fun saveLogsEnabled(enabled: Boolean) {
        globalDataStorage.edit { preferences ->
            preferences[CLIENT_LOGS_ENABLED] = enabled
        }
    }

    suspend fun getLogsEnabled(): Boolean? = globalDataStorage.get(CLIENT_LOGS_ENABLED)

    suspend fun saveLogLevel(level: LogLevel) {
        globalDataStorage.edit { preferences ->
            preferences[CLIENT_LOG_LEVEL] = level.name
        }
    }

    suspend fun getLogLevel(): LogLevel? = globalDataStorage
        .get(CLIENT_LOG_LEVEL)
        ?.let { value -> runCatching { LogLevel.valueOf(value) }.getOrNull() }
}

