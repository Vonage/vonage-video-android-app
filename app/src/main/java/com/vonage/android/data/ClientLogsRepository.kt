package com.vonage.android.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.vonage.android.data.network.APIService
import com.vonage.logger.DefaultVonageLogger
import com.vonage.logger.LogLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientLogsRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val apiService: APIService,
) {

    private val _logsEnabled = MutableStateFlow(DefaultVonageLogger.isEnabled)
    val logsEnabled: StateFlow<Boolean> = _logsEnabled.asStateFlow()

    private val _logLevel = MutableStateFlow(DefaultVonageLogger.currentMinLogLevel)
    val logLevel: StateFlow<LogLevel> = _logLevel.asStateFlow()

    fun setLogsEnabled(enabled: Boolean) {
        DefaultVonageLogger.setEnabled(enabled)
        _logsEnabled.value = enabled
    }

    fun setLogLevel(level: LogLevel) {
        DefaultVonageLogger.setMinLogLevel(level)
        _logLevel.value = level
    }

    fun getLatestLogFile(): File? {
        val logDir = File(context.filesDir, DefaultVonageLogger.LOGS_DIRECTORY_NAME)
        if (!logDir.exists() || !logDir.isDirectory) return null
        return logDir.listFiles { file ->
            file.isFile && file.name.endsWith(LOG_FILE_SUFFIX)
        }?.maxByOrNull { it.name }
    }

    fun getLatestLogUri(): Uri? {
        val file = getLatestLogFile() ?: return null
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    }

    suspend fun sendLogs(): SendClientLogsResult {        val payload = buildPayload() ?: return SendClientLogsResult.NoLogsAvailable
        return runCatching {
            apiService.sendClientLogs(payload.toRequestBody(JSON_MEDIA_TYPE))
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    SendClientLogsResult.Success
                } else {
                    SendClientLogsResult.Failure
                }
            },
            onFailure = {
                SendClientLogsResult.Failure
            },
        )
    }

    private fun buildPayload(): String? {
        val logDir = File(context.filesDir, DefaultVonageLogger.LOGS_DIRECTORY_NAME)
        if (!logDir.exists() || !logDir.isDirectory) return null

        val latestLogFile = logDir.listFiles { file ->
            file.isFile && file.name.endsWith(LOG_FILE_SUFFIX)
        }
            ?.maxByOrNull { it.name }

        val entries = latestLogFile
            ?.readLines(Charsets.UTF_8)
            ?.filter(String::isNotBlank)
            .orEmpty()

        if (entries.isEmpty()) return null

        return entries.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]",
        )
    }

    companion object {
        private const val LOG_FILE_SUFFIX = ".json.log"
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }
}

sealed interface SendClientLogsResult {
    data object Success : SendClientLogsResult
    data object NoLogsAvailable : SendClientLogsResult
    data object Failure : SendClientLogsResult
}



