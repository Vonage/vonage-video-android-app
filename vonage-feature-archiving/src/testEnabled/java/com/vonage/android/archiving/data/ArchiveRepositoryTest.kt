package com.vonage.android.archiving.data

import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.android.archiving.ArchiveType
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
        coEvery { apiService.startArchiving(START_ARCHIVE_REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "archive-id", status = "started")))
        )
        val response = sut.startArchive(SESSION_KEY)
        assertEquals(Result.success(ArchiveId("archive-id")), response)
    }

    @Test
    fun `given repository when startArchiving api success with empty returns success`() = runTest {
        coEvery { apiService.startArchiving(START_ARCHIVE_REQUEST) } returns Response.success(null)
        val response = sut.startArchive(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api fails returns error`() = runTest {
        coEvery { apiService.startArchiving(START_ARCHIVE_REQUEST) } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.startArchive(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api fails with exception returns error`() = runTest {
        coEvery { apiService.startArchiving(START_ARCHIVE_REQUEST) } throws Exception("Network error")
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
    fun `given repository when startArchiving with transcription sends transcription request`() = runTest {
        val request = StartArchiveRequest(
            sessionKey = SESSION_KEY,
            withTranscription = true,
            archiveOptions = StartArchiveOptions(TranscriptionProperties(hasSummary = true)),
        )
        coEvery { apiService.startArchiving(request) } returns
            Response.success(
                TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "transcription-id", status = "started")))
            )
        val response = sut.startArchive(SESSION_KEY, withTranscription = true, withSummary = true)
        assertEquals(Result.success(ArchiveId("transcription-id")), response)
    }

    @Test
    fun `start archive request only encodes withTranscription when requested`() {
        val json = Json { ignoreUnknownKeys = true }
        assertEquals("""{"sessionKey":"key"}""", json.encodeToString(StartArchiveRequest("key")))
        assertEquals(
            """{"sessionKey":"key","withTranscription":true}""",
            json.encodeToString(StartArchiveRequest("key", withTranscription = true)),
        )
    }

    @Test
    fun `start archive request encodes the summary choice as transcription properties`() {
        val json = Json { ignoreUnknownKeys = true }
        assertEquals(
            """{"sessionKey":"key","withTranscription":true,""" +
                """"archiveOptions":{"transcriptionProperties":{"hasSummary":false}}}""",
            json.encodeToString(
                StartArchiveRequest(
                    sessionKey = "key",
                    withTranscription = true,
                    archiveOptions = StartArchiveOptions(TranscriptionProperties(hasSummary = false)),
                ),
            ),
        )
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

    @Test
    fun `search archives response decodes transcription archive`() {
        val json = Json { ignoreUnknownKeys = true }
        // Shape returned by the backend for a finished transcription (signed URLs replaced).
        val body = """{"result":{"data":{"count":1,"items":[{
            "id":"transcription-id","status":"available","name":"transcription","reason":"session ended",
            "sessionId":"s","applicationId":"a","createdAt":1793102510000,"size":2546374,"duration":644,
            "outputMode":"individual","streamMode":"auto","hasAudio":true,"hasVideo":false,
            "hasTranscription":true,"sha256sum":"sum","password":"","updatedAt":1793103218000,
            "multiArchiveTag":"transcription","event":"archive","url":"$AUDIO_URL",
            "transcription":{"status":"available","reason":"","url":"$TRANSCRIPT_URL",
            "primaryLanguageCode":"en-US","hasSummary":true}}]}}}"""
        val decoded = json.decodeFromString<TrpcResponse<SearchArchivesResponse>>(body)
        assertEquals(
            listOf(transcriptionArchive(transcription = ServerTranscription("available", TRANSCRIPT_URL))),
            decoded.result.data.items,
        )
    }

    @Test
    fun `given transcription archive with available transcript then maps transcript url and type`() = runTest {
        givenSearchArchives(transcriptionArchive(transcription = ServerTranscription("available", TRANSCRIPT_URL)))

        val archive = sut.getRecordings(SESSION_KEY).getOrThrow().single()

        assertEquals(ArchiveType.TRANSCRIPTION, archive.type)
        assertEquals(ArchiveStatus.AVAILABLE, archive.status)
        assertEquals(TRANSCRIPT_URL, archive.url)
    }

    @Test
    fun `given transcription archive with transcript in progress then it is pending`() = runTest {
        givenSearchArchives(transcriptionArchive(transcription = ServerTranscription("started", null)))

        assertEquals(ArchiveStatus.PENDING, sut.getRecordings(SESSION_KEY).getOrThrow().single().status)
    }

    @Test
    fun `given available transcription archive without transcript yet then it is pending`() = runTest {
        givenSearchArchives(transcriptionArchive(transcription = null))

        val archive = sut.getRecordings(SESSION_KEY).getOrThrow().single()

        assertEquals(ArchiveStatus.PENDING, archive.status)
        assertEquals("", archive.url)
    }

    @Test
    fun `given failed transcription archive then it is failed`() = runTest {
        givenSearchArchives(transcriptionArchive(status = "failed", transcription = null))

        assertEquals(ArchiveStatus.FAILED, sut.getRecordings(SESSION_KEY).getOrThrow().single().status)
    }

    @Test
    fun `given recording archive then keeps archive url and recording type`() = runTest {
        givenSearchArchives(serverArchives.first())

        val archive = sut.getRecordings(SESSION_KEY).getOrThrow().single()

        assertEquals(ArchiveType.RECORDING, archive.type)
        assertEquals("url", archive.url)
    }

    private fun givenSearchArchives(vararg items: ServerArchive) {
        coEvery { apiService.searchArchives(SESSION_KEY_REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(SearchArchivesResponse(items = items.toList(), count = items.size)))
        )
    }

    private fun transcriptionArchive(status: String = "available", transcription: ServerTranscription?) =
        ServerArchive(
            id = "transcription-id",
            name = "transcription",
            url = AUDIO_URL,
            status = status,
            createdAt = 1793102510000,
            duration = 644,
            size = 2546374,
            hasTranscription = true,
            transcription = transcription,
        )

    private companion object {
        const val SESSION_KEY = "any-session-key"
        const val AUDIO_URL = "https://archives.example.com/transcription-id/archive.zip"
        const val TRANSCRIPT_URL = "https://archives.example.com/transcription-id/transcription.zip"
        val SESSION_KEY_REQUEST = SessionKeyRequest(SESSION_KEY)
        val START_ARCHIVE_REQUEST = StartArchiveRequest(SESSION_KEY)
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