package com.vonage.android.archiving.data

import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.android.archiving.ArchiveType
import com.vonage.android.vera.api.VeraApiException
import com.vonage.android.vera.api.archiving.ArchivingApi
import com.vonage.android.vera.api.archiving.ServerArchive
import com.vonage.android.vera.api.archiving.ServerTranscription
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ArchiveRepositoryTest {

    val archivingApi: ArchivingApi = mockk()
    val sut = ArchiveRepository(
        archivingApi = archivingApi,
    )

    @Test
    fun `given repository when getRecordings api success returns success`() = runTest {
        coEvery { archivingApi.searchArchives(SESSION_KEY) } returns Result.success(serverArchives)
        val response = sut.getRecordings(SESSION_KEY)
        assertEquals(Result.success(archives), response)
    }

    @Test
    fun `given repository when getRecordings api fails returns error`() = runTest {
        coEvery { archivingApi.searchArchives(SESSION_KEY) } returns Result.failure(API_ERROR)
        val response = sut.getRecordings(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when getRecordings api fails with exception returns error`() = runTest {
        coEvery { archivingApi.searchArchives(SESSION_KEY) } returns Result.failure(Exception("Network error"))
        val response = sut.getRecordings(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api success returns success`() = runTest {
        coEvery { archivingApi.startArchive(SESSION_KEY, false, false) } returns Result.success("archive-id")
        val response = sut.startArchive(SESSION_KEY)
        assertEquals(Result.success(ArchiveId("archive-id")), response)
    }

    @Test
    fun `given repository when startArchiving api fails returns error`() = runTest {
        coEvery { archivingApi.startArchive(SESSION_KEY, false, false) } returns Result.failure(API_ERROR)
        val response = sut.startArchive(SESSION_KEY)
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving with transcription forwards the transcription options`() = runTest {
        coEvery { archivingApi.startArchive(SESSION_KEY, true, true) } returns Result.success("transcription-id")
        val response = sut.startArchive(SESSION_KEY, withTranscription = true, withSummary = true)
        assertEquals(Result.success(ArchiveId("transcription-id")), response)
    }

    @Test
    fun `given repository when stopArchiving api success returns success`() = runTest {
        coEvery { archivingApi.stopArchive(SESSION_KEY, "archive-id") } returns Result.success(Unit)
        val response = sut.stopArchive(SESSION_KEY, ArchiveId("archive-id"))
        assertEquals(Result.success(true), response)
    }

    @Test
    fun `given repository when stopArchiving api fails returns error`() = runTest {
        coEvery { archivingApi.stopArchive(SESSION_KEY, "archive-id") } returns Result.failure(API_ERROR)
        val response = sut.stopArchive(SESSION_KEY, ArchiveId("archive-id"))
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when stopArchiving without archive id sends request without it`() = runTest {
        coEvery { archivingApi.stopArchive(SESSION_KEY, null) } returns Result.success(Unit)
        val response = sut.stopArchive(SESSION_KEY, null)
        assertEquals(Result.success(true), response)
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
        coEvery { archivingApi.searchArchives(SESSION_KEY) } returns Result.success(items.toList())
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
        val API_ERROR = VeraApiException("Failed", httpCode = 500)
    }

    private val serverArchives = listOf("available", "started", "stopped", "uploaded", "paused", "failed")
        .map { status ->
            ServerArchive(
                id = "id",
                name = "name",
                url = "url",
                status = status,
                createdAt = 123,
                duration = 789,
                size = 456,
            )
        }
    private val archives = listOf(
        ArchiveStatus.AVAILABLE,
        ArchiveStatus.PENDING,
        ArchiveStatus.PENDING,
        ArchiveStatus.PENDING,
        ArchiveStatus.PENDING,
        ArchiveStatus.FAILED,
    ).map { status ->
        Archive(
            id = ArchiveId("id"),
            name = "name",
            url = "url",
            status = status,
            createdAt = 123,
            duration = 789,
            size = 456,
        )
    }
}
