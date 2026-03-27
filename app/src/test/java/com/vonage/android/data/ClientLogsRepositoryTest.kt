package com.vonage.android.data

import android.content.Context
import com.vonage.android.data.network.APIService
import com.vonage.logger.DefaultVonageLogger
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.File
import java.nio.file.Files

class ClientLogsRepositoryTest {

    private lateinit var filesDir: File
    private val apiService: APIService = mockk()
    private val context: Context = mockk()

    @Before
    fun setUp() {
        filesDir = Files.createTempDirectory("client-logs-repository").toFile()
        every { context.filesDir } returns filesDir
        DefaultVonageLogger.setEnabled(true)
    }

    @After
    fun tearDown() {
        DefaultVonageLogger.setEnabled(true)
        filesDir.deleteRecursively()
    }

    @Test
    fun `setLogsEnabled updates repository flow and default logger state`() {
        val repository = ClientLogsRepository(context, apiService)

        repository.setLogsEnabled(false)

        assertEquals(false, repository.logsEnabled.value)
        assertEquals(false, DefaultVonageLogger.isEnabled)
    }

    @Test
    fun `sendLogs returns NoLogsAvailable when no logs exist`() = runTest {
        val repository = ClientLogsRepository(context, apiService)

        val result = repository.sendLogs()

        assertEquals(SendClientLogsResult.NoLogsAvailable, result)
    }

    @Test
    fun `sendLogs posts only the latest log file lines as a single JSON array payload`() = runTest {
        val repository = ClientLogsRepository(context, apiService)
        val logsDir = File(filesDir, DefaultVonageLogger.LOGS_DIRECTORY_NAME).apply { mkdirs() }
        File(logsDir, "app-2026-03-26.json.log").writeText("{" + "\"message\":\"one\"}" + "\n{" + "\"message\":\"two\"}\n")
        File(logsDir, "app-2026-03-27.json.log").writeText("{" + "\"message\":\"three\"}\n")
        val requestBody = slot<RequestBody>()
        coEvery { apiService.sendClientLogs(capture(requestBody)) } returns Response.success(Unit)

        val result = repository.sendLogs()

        assertEquals(SendClientLogsResult.Success, result)
        assertEquals(
            "[{\"message\":\"three\"}]",
            requestBody.captured.readUtf8(),
        )
    }

    @Test
    fun `sendLogs returns Failure when backend responds with error`() = runTest {
        val repository = ClientLogsRepository(context, apiService)
        val logsDir = File(filesDir, DefaultVonageLogger.LOGS_DIRECTORY_NAME).apply { mkdirs() }
        File(logsDir, "app-2026-03-27.json.log").writeText("{" + "\"message\":\"one\"}\n")
        coEvery {
            apiService.sendClientLogs(any())
        } returns Response.error(500, "error".toResponseBody("text/plain".toMediaType()))

        val result = repository.sendLogs()

        assertTrue(result is SendClientLogsResult.Failure)
    }

    private fun RequestBody.readUtf8(): String {
        val buffer = Buffer()
        writeTo(buffer)
        return buffer.readUtf8()
    }
}


