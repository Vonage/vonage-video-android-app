package com.vonage.android.vera.api.archiving

import com.vonage.android.vera.api.VeraApiException
import com.vonage.android.vera.api.http.SessionKeyRequest
import com.vonage.android.vera.api.http.TrpcResponse
import com.vonage.android.vera.api.http.TrpcResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertIs

class ArchivingApiTest {

    private val service: ArchivingService = mockk()
    private val sut = DefaultArchivingApi(service)

    @Test
    fun `given searchArchives success returns the archives`() = runTest {
        coEvery { service.searchArchives(SESSION_KEY_REQUEST) } returns Response.success(
            TrpcResponse(TrpcResult(SearchArchivesResponse(items = listOf(SERVER_ARCHIVE), count = 1)))
        )
        assertEquals(Result.success(listOf(SERVER_ARCHIVE)), sut.searchArchives(SESSION_KEY))
    }

    @Test
    fun `given searchArchives empty body returns failure`() = runTest {
        coEvery { service.searchArchives(SESSION_KEY_REQUEST) } returns Response.success(null)
        assertTrue(sut.searchArchives(SESSION_KEY).isFailure)
    }

    @Test
    fun `given searchArchives error response returns failure with status and body`() = runTest {
        coEvery { service.searchArchives(SESSION_KEY_REQUEST) } returns Response.error(
            500, """{"error":"boom"}""".toResponseBody()
        )
        val error = sut.searchArchives(SESSION_KEY).exceptionOrNull()
        assertIs<VeraApiException>(error)
        assertEquals(500, error.httpCode)
        assertEquals("""{"error":"boom"}""", error.errorBody)
    }

    @Test
    fun `given searchArchives throws returns failure`() = runTest {
        coEvery { service.searchArchives(SESSION_KEY_REQUEST) } throws Exception("Network error")
        assertEquals("Network error", sut.searchArchives(SESSION_KEY).exceptionOrNull()?.message)
    }

    @Test
    fun `given startArchive success returns the archive id`() = runTest {
        coEvery { service.startArchiving(StartArchiveRequest(SESSION_KEY)) } returns Response.success(
            TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "archive-id", status = "started")))
        )
        assertEquals(Result.success("archive-id"), sut.startArchive(SESSION_KEY))
    }

    @Test
    fun `given startArchive empty body returns failure`() = runTest {
        coEvery { service.startArchiving(StartArchiveRequest(SESSION_KEY)) } returns Response.success(null)
        assertTrue(sut.startArchive(SESSION_KEY).isFailure)
    }

    @Test
    fun `given startArchive error response returns failure`() = runTest {
        coEvery { service.startArchiving(StartArchiveRequest(SESSION_KEY)) } returns Response.error(
            500, ResponseBody.EMPTY
        )
        assertTrue(sut.startArchive(SESSION_KEY).isFailure)
    }

    @Test
    fun `given startArchive throws returns failure`() = runTest {
        coEvery { service.startArchiving(StartArchiveRequest(SESSION_KEY)) } throws Exception("Network error")
        assertTrue(sut.startArchive(SESSION_KEY).isFailure)
    }

    @Test
    fun `given startArchive with transcription sends transcription request`() = runTest {
        val request = StartArchiveRequest(
            sessionKey = SESSION_KEY,
            withTranscription = true,
            archiveOptions = StartArchiveOptions(TranscriptionProperties(hasSummary = true)),
        )
        coEvery { service.startArchiving(request) } returns Response.success(
            TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "transcription-id", status = "started")))
        )
        assertEquals(
            Result.success("transcription-id"),
            sut.startArchive(SESSION_KEY, withTranscription = true, withSummary = true),
        )
    }

    @Test
    fun `given stopArchive success returns success`() = runTest {
        coEvery { service.stopArchiving(StopArchiveRequest(SESSION_KEY, "archive-id")) } returns
            Response.success(TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "archive-id", status = "stopped"))))
        assertEquals(Result.success(Unit), sut.stopArchive(SESSION_KEY, "archive-id"))
    }

    @Test
    fun `given stopArchive success without body returns success`() = runTest {
        coEvery { service.stopArchiving(StopArchiveRequest(SESSION_KEY, "archive-id")) } returns Response.success(null)
        assertEquals(Result.success(Unit), sut.stopArchive(SESSION_KEY, "archive-id"))
    }

    @Test
    fun `given stopArchive error response returns failure`() = runTest {
        coEvery { service.stopArchiving(StopArchiveRequest(SESSION_KEY, "archive-id")) } returns Response.error(
            500, ResponseBody.EMPTY
        )
        assertTrue(sut.stopArchive(SESSION_KEY, "archive-id").isFailure)
    }

    @Test
    fun `given stopArchive throws returns failure`() = runTest {
        coEvery { service.stopArchiving(StopArchiveRequest(SESSION_KEY, "archive-id")) } throws
            Exception("Network error")
        assertTrue(sut.stopArchive(SESSION_KEY, "archive-id").isFailure)
    }

    @Test
    fun `given stopArchive without archive id sends request without it`() = runTest {
        coEvery { service.stopArchiving(StopArchiveRequest(SESSION_KEY, null)) } returns
            Response.success(TrpcResponse(TrpcResult(ArchiveOperationResponse(id = "archive-id", status = "stopped"))))
        assertEquals(Result.success(Unit), sut.stopArchive(SESSION_KEY, null))
    }

    @Test
    fun `start archive request only encodes withTranscription when requested`() {
        assertEquals("""{"sessionKey":"key"}""", json.encodeToString(StartArchiveRequest("key")))
        assertEquals(
            """{"sessionKey":"key","withTranscription":true}""",
            json.encodeToString(StartArchiveRequest("key", withTranscription = true)),
        )
    }

    @Test
    fun `start archive request encodes the summary choice as transcription properties`() {
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
        assertEquals("""{"sessionKey":"key"}""", json.encodeToString(StopArchiveRequest("key")))
        assertEquals(
            """{"sessionKey":"key","archiveId":"id"}""",
            json.encodeToString(StopArchiveRequest("key", "id")),
        )
    }

    @Test
    fun `search archives response decodes tRPC payload`() {
        val body = """{"result":{"data":{"count":1,"items":[{"id":"id","duration":789,"name":"name",""" +
            """"url":"url","size":456,"status":"available","createdAt":123,"sessionId":"s"}]}}}"""
        val decoded = json.decodeFromString<TrpcResponse<SearchArchivesResponse>>(body)
        assertEquals(listOf(SERVER_ARCHIVE), decoded.result.data.items)
    }

    @Test
    fun `search archives response decodes transcription archive`() {
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
            listOf(
                ServerArchive(
                    id = "transcription-id",
                    name = "transcription",
                    url = AUDIO_URL,
                    status = "available",
                    createdAt = 1793102510000,
                    duration = 644,
                    size = 2546374,
                    hasTranscription = true,
                    transcription = ServerTranscription("available", TRANSCRIPT_URL),
                ),
            ),
            decoded.result.data.items,
        )
    }

    private val json = Json { ignoreUnknownKeys = true }

    private companion object {
        const val SESSION_KEY = "any-session-key"
        const val AUDIO_URL = "https://archives.example.com/transcription-id/archive.zip"
        const val TRANSCRIPT_URL = "https://archives.example.com/transcription-id/transcription.zip"
        val SESSION_KEY_REQUEST = SessionKeyRequest(SESSION_KEY)
        val SERVER_ARCHIVE = ServerArchive(
            id = "id",
            name = "name",
            url = "url",
            status = "available",
            createdAt = 123,
            duration = 789,
            size = 456,
        )
    }
}
