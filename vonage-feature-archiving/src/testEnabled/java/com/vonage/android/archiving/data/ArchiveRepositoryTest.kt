package com.vonage.android.archiving.data

import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.android.shared.network.SessionKeyRequest
import com.vonage.android.shared.network.TrpcResponse
import com.vonage.android.shared.network.TrpcResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

class ArchiveRepositoryTest {

    val apiService: ArchivingApi = mockk()
    val sut = ArchiveRepository(
        archivingApi = apiService,
    )

    @Test
    fun `given repository when getRecordings api success returns success`() = runTest {
        coEvery { apiService.searchArchives(SESSION_KEY_REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(SearchArchivesResponse(items = serverArchives, count = serverArchives.size)))
        )
        val response = sut.getRecordings(SESSION_KEY)
        assertEquals(Result.success(archives), response)
    }

    @Test
    fun `given repository when getRecordings api success with empty returns success`() = runTest {
        coEvery { apiService.searchArchives(SESSION_KEY_REQUEST) } returns Response.success(null)
        val response = sut.getRecordings(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when getRecordings api fails returns error`() = runTest {
        coEvery { apiService.searchArchives(SESSION_KEY_REQUEST) } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.getRecordings(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when getRecordings api fails with exception returns error`() = runTest {
        coEvery { apiService.searchArchives(SESSION_KEY_REQUEST) } throws Exception("Network error")
        val response = sut.getRecordings(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api success returns success`() = runTest {
        coEvery { apiService.startArchiving(SESSION_KEY_REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "archive-id", status = "started")))
        )
        val response = sut.startArchive(SESSION_KEY)
        assertEquals(Result.success(ArchiveId("archive-id")), response)
    }

    @Test
    fun `given repository when startArchiving api success with empty returns success`() = runTest {
        coEvery { apiService.startArchiving(SESSION_KEY_REQUEST) } returns Response.success(null)
        val response = sut.startArchive(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api fails returns error`() = runTest {
        coEvery { apiService.startArchiving(SESSION_KEY_REQUEST) } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.startArchive(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api fails with exception returns error`() = runTest {
        coEvery { apiService.startArchiving(SESSION_KEY_REQUEST) } throws Exception("Network error")
        val response = sut.startArchive(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when stopArchiving api success returns success`() = runTest {
        coEvery { apiService.stopArchiving(StopArchiveRequest(SESSION_KEY, "archive-id")) } returns
                Response.success(TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "archive-id", status = "stopped"))))
        val response = sut.stopArchive(SESSION_KEY, ArchiveId("archive-id"))
        assertEquals(Result.success(true), response)
    }

    @Test
    fun `given repository when stopArchiving api fails returns error`() = runTest {
        coEvery { apiService.stopArchiving(StopArchiveRequest(SESSION_KEY, "archive-id")) } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.stopArchive(SESSION_KEY, ArchiveId("archive-id"))
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when stopArchiving api fails with exception returns error`() = runTest {
        coEvery { apiService.stopArchiving(StopArchiveRequest(SESSION_KEY, "archive-id")) } throws Exception("Network error")
        val response = sut.stopArchive(SESSION_KEY, ArchiveId("archive-id"))
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when stopArchiving without archive id sends request without it`() = runTest {
        coEvery { apiService.stopArchiving(StopArchiveRequest(SESSION_KEY, null)) } returns
                Response.success(TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "archive-id", status = "stopped"))))
        val response = sut.stopArchive(SESSION_KEY, null)
        assertEquals(Result.success(true), response)
    }

    @Test
    fun `stop archive request omits null archive id from json`() {
        val json = Json { ignoreUnknownKeys = true }
        assertEquals("""{"sessionKey":"key"}""", json.encodeToString(StopArchiveRequest("key")))
        assertEquals(
            """{"sessionKey":"key","archiveId":"id"}""",
            json.encodeToString(StopArchiveRequest("key", "id")),
        )
    }

    @Test
    fun `search archives response decodes tRPC payload`() {
        val json = Json { ignoreUnknownKeys = true }
        val body = """{"result":{"data":{"count":1,"items":[{"id":"id","duration":789,"name":"name",""" +
            """"url":"url","size":456,"status":"available","createdAt":123,"sessionId":"s"}]}}}"""
        val decoded = json.decodeFromString<TrpcResponse<SearchArchivesResponse>>(body)
        assertEquals(listOf(serverArchives.first()), decoded.result.data.items)
    }

    private companion object {
        const val SESSION_KEY = "any-session-key"
        val SESSION_KEY_REQUEST = SessionKeyRequest(SESSION_KEY)
    }

    private val serverArchives = listOf(
        ServerArchive(
            id = "id",
            name = "name",
            url = "url",
            status = "available",
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        ServerArchive(
            id = "id",
            name = "name",
            url = "url",
            status = "started",
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        ServerArchive(
            id = "id",
            name = "name",
            url = "url",
            status = "stopped",
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        ServerArchive(
            id = "id",
            name = "name",
            url = "url",
            status = "uploaded",
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        ServerArchive(
            id = "id",
            name = "name",
            url = "url",
            status = "paused",
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        ServerArchive(
            id = "id",
            name = "name",
            url = "url",
            status = "failed",
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
    )
    private val archives = listOf(
        Archive(
            id = ArchiveId("id"),
            name = "name",
            url = "url",
            status = ArchiveStatus.AVAILABLE,
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        Archive(
            id = ArchiveId("id"),
            name = "name",
            url = "url",
            status = ArchiveStatus.PENDING,
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        Archive(
            id = ArchiveId("id"),
            name = "name",
            url = "url",
            status = ArchiveStatus.PENDING,
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        Archive(
            id = ArchiveId("id"),
            name = "name",
            url = "url",
            status = ArchiveStatus.PENDING,
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        Archive(
            id = ArchiveId("id"),
            name = "name",
            url = "url",
            status = ArchiveStatus.PENDING,
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
        Archive(
            id = ArchiveId("id"),
            name = "name",
            url = "url",
            status = ArchiveStatus.FAILED,
            createdAt = 123,
            duration = 789,
            size = 456,
        ),
    )
}