package com.vonage.android.archiving

import app.cash.turbine.test
import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.kotlin.model.CallFacade
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EnabledVonageArchivingTest {

    private val archiveRepository: ArchiveRepository = mockk()
    private val callFacade: CallFacade = mockk()
    private val activeArchives = MutableStateFlow<Map<String, String?>>(emptyMap())
    
    private lateinit var sut: EnabledVonageArchiving

    @BeforeEach
    fun setup() {
        every { callFacade.activeArchives } returns activeArchives
        sut = EnabledVonageArchiving(archiveRepository)
    }

    @Test
    fun `bind should emit no archives initially`() = runTest {
        sut.bind(callFacade).test {
            assertEquals(emptyMap<ArchiveType, ArchiveId>(), awaitItem())
        }
    }

    @Test
    fun `bind should emit running archives by type`() = runTest {
        sut.bind(callFacade).test {
            awaitItem()
            activeArchives.value = mapOf("recording-id" to "recording", "transcription-id" to "transcription")

            assertEquals(
                mapOf(
                    ArchiveType.RECORDING to ArchiveId("recording-id"),
                    ArchiveType.TRANSCRIPTION to ArchiveId("transcription-id"),
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `bind should treat unnamed archives as recordings`() = runTest {
        sut.bind(callFacade).test {
            awaitItem()
            activeArchives.value = mapOf("archive-123" to null)
            assertEquals(mapOf(ArchiveType.RECORDING to ArchiveId("archive-123")), awaitItem())

            activeArchives.value = emptyMap()
            assertEquals(emptyMap<ArchiveType, ArchiveId>(), awaitItem())
        }
    }

    @Test
    fun `startArchive should return success with archive id`() = runTest {
        val expectedArchiveId = ArchiveId("new-archive-id")
        coEvery { archiveRepository.startArchive("test-session-key") } returns Result.success(expectedArchiveId)

        val result = sut.startArchive("test-session-key")

        assertTrue(result.isSuccess)
        assertEquals(expectedArchiveId, result.getOrNull())
    }

    @Test
    fun `startArchive should return failure when repository fails`() = runTest {
        val exception = Exception("Failed to start archive")
        coEvery { archiveRepository.startArchive("test-session-key") } returns Result.failure(exception)

        val result = sut.startArchive("test-session-key")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `stopArchive should return success when archive is active`() = runTest {
        val archiveId = ArchiveId("active-archive-id")

        coEvery { archiveRepository.startArchive("test-session-key") } returns Result.success(archiveId)
        sut.startArchive("test-session-key")

        coEvery { archiveRepository.stopArchive("test-session-key", archiveId) } returns Result.success(true)
        
        val result = sut.stopArchive("test-session-key")

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `stopArchive without tracked archive id lets backend resolve it`() = runTest {
        coEvery { archiveRepository.stopArchive("test-session-key", null) } returns Result.success(true)

        val result = sut.stopArchive("test-session-key")

        assertTrue(result.isSuccess)
        coVerify { archiveRepository.stopArchive("test-session-key", null) }
    }

    @Test
    fun `stopArchive sends archive id tracked from bind`() = runTest {
        coEvery { archiveRepository.stopArchive("test-session-key", ArchiveId("remote-id")) } returns
            Result.success(true)

        sut.bind(callFacade).test {
            awaitItem()
            activeArchives.value = mapOf("remote-id" to "recording")
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
        val result = sut.stopArchive("test-session-key")

        assertTrue(result.isSuccess)
        coVerify { archiveRepository.stopArchive("test-session-key", ArchiveId("remote-id")) }
    }

    @Test
    fun `stopArchive prefers the recording id reported by the session`() = runTest {
        coEvery { archiveRepository.startArchive("test-session-key") } returns Result.success(ArchiveId("local-id"))
        coEvery { archiveRepository.stopArchive("test-session-key", ArchiveId("rotated-id")) } returns
            Result.success(true)
        sut.startArchive("test-session-key")

        sut.bind(callFacade).test {
            awaitItem()
            activeArchives.value = mapOf("rotated-id" to "recording")
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(sut.stopArchive("test-session-key").isSuccess)
        coVerify { archiveRepository.stopArchive("test-session-key", ArchiveId("rotated-id")) }
    }

    @Test
    fun `stopArchive should return failure when repository fails`() = runTest {
        val archiveId = ArchiveId("active-archive-id")
        val exception = Exception("Failed to stop archive")
        
        coEvery { archiveRepository.startArchive("test-session-key") } returns Result.success(archiveId)
        sut.startArchive("test-session-key")
        
        coEvery { archiveRepository.stopArchive("test-session-key", archiveId) } returns Result.failure(exception)
        
        val result = sut.stopArchive("test-session-key")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getRecordings should return success with list of archives`() = runTest {
        val archives = listOf(
            Archive(
                id = ArchiveId("archive-1"),
                duration = 120,
                size = 1024,
                name = "Recording 1",
                url = "https://example.com/archive-1",
                status = ArchiveStatus.AVAILABLE,
                createdAt = 1234567890L
            ),
            Archive(
                id = ArchiveId("archive-2"),
                duration = 180,
                size = 2048,
                name = "Recording 2",
                url = "https://example.com/archive-2",
                status = ArchiveStatus.AVAILABLE,
                createdAt = 1234567900L
            )
        )
        
        coEvery { archiveRepository.getRecordings("test-session-key") } returns Result.success(archives)

        val result = sut.getRecordings("test-session-key")

        assertTrue(result.isSuccess)
        assertEquals(archives, result.getOrNull())
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `getRecordings should return empty list when no recordings exist`() = runTest {
        coEvery { archiveRepository.getRecordings("test-session-key") } returns Result.success(emptyList())

        val result = sut.getRecordings("test-session-key")

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Archive>(), result.getOrNull())
    }

    @Test
    fun `getRecordings should return failure when repository fails`() = runTest {
        val exception = Exception("Failed to fetch recordings")
        coEvery { archiveRepository.getRecordings("test-session-key") } returns Result.failure(exception)

        val result = sut.getRecordings("test-session-key")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `multiple startArchive calls should return each archive id`() = runTest {
        val archiveId1 = ArchiveId("archive-1")
        val archiveId2 = ArchiveId("archive-2")
        
        coEvery { archiveRepository.startArchive("session-key-1") } returns Result.success(archiveId1)
        coEvery { archiveRepository.startArchive("session-key-2") } returns Result.success(archiveId2)
        
        val result1 = sut.startArchive("session-key-1")
        assertEquals(archiveId1, result1.getOrNull())
        
        val result2 = sut.startArchive("session-key-2")
        assertEquals(archiveId2, result2.getOrNull())
    }

    @Test
    fun `bind and startArchive should work independently`() = runTest {
        val archiveIdFromBind = "bind-archive-id"
        val archiveIdFromStart = ArchiveId("start-archive-id")
        
        coEvery { archiveRepository.startArchive("test-session-key") } returns Result.success(archiveIdFromStart)
        
        sut.bind(callFacade).test {
            awaitItem()
            activeArchives.value = mapOf(archiveIdFromBind to null)
            assertEquals(mapOf(ArchiveType.RECORDING to ArchiveId(archiveIdFromBind)), awaitItem())
            
            val result = sut.startArchive("test-session-key")
            assertEquals(archiveIdFromStart, result.getOrNull())
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    // region Transcription

    @Test
    fun `startArchive transcription requests archive with transcription`() = runTest {
        coEvery { archiveRepository.startArchive(SESSION_KEY, withTranscription = true) } returns
            Result.success(ArchiveId("transcription-id"))

        val result = sut.startArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION)

        assertEquals(ArchiveId("transcription-id"), result.getOrNull())
        coVerify { archiveRepository.startArchive(SESSION_KEY, withTranscription = true) }
    }

    @Test
    fun `startArchive transcription forwards the summary choice`() = runTest {
        coEvery {
            archiveRepository.startArchive(SESSION_KEY, withTranscription = true, withSummary = true)
        } returns Result.success(ArchiveId("transcription-id"))

        sut.startArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION, withSummary = true)

        coVerify { archiveRepository.startArchive(SESSION_KEY, withTranscription = true, withSummary = true) }
    }

    @Test
    fun `stopArchive of each type sends its own archive id while both run`() = runTest {
        coEvery { archiveRepository.startArchive(SESSION_KEY, withTranscription = false) } returns
            Result.success(ArchiveId("recording-id"))
        coEvery { archiveRepository.startArchive(SESSION_KEY, withTranscription = true) } returns
            Result.success(ArchiveId("transcription-id"))
        coEvery { archiveRepository.stopArchive(SESSION_KEY, any()) } returns Result.success(true)

        sut.startArchive(SESSION_KEY, ArchiveType.RECORDING)
        sut.startArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION)

        assertTrue(sut.stopArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION).isSuccess)
        coVerify { archiveRepository.stopArchive(SESSION_KEY, ArchiveId("transcription-id")) }

        assertTrue(sut.stopArchive(SESSION_KEY, ArchiveType.RECORDING).isSuccess)
        coVerify { archiveRepository.stopArchive(SESSION_KEY, ArchiveId("recording-id")) }
    }

    @Test
    fun `stopArchive transcription without tracked archive id fails without calling backend`() = runTest {
        val result = sut.stopArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { archiveRepository.stopArchive(any(), any()) }
    }

    @Test
    fun `bind tracks archive ids by name and a stopped transcription keeps the recording id`() = runTest {
        coEvery { archiveRepository.stopArchive(SESSION_KEY, any()) } returns Result.success(true)

        sut.bind(callFacade).test {
            awaitItem()
            activeArchives.value = mapOf("recording-id" to "recording", "transcription-id" to "transcription")
            awaitItem()
            activeArchives.value = mapOf("recording-id" to "recording")
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(sut.stopArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION).isFailure)
        assertTrue(sut.stopArchive(SESSION_KEY, ArchiveType.RECORDING).isSuccess)
        coVerify { archiveRepository.stopArchive(SESSION_KEY, ArchiveId("recording-id")) }
    }

    @Test
    fun `stopArchive transcription sends archive id tracked from bind`() = runTest {
        coEvery { archiveRepository.stopArchive(SESSION_KEY, ArchiveId("remote-transcription-id")) } returns
            Result.success(true)

        sut.bind(callFacade).test {
            awaitItem()
            activeArchives.value = mapOf("remote-transcription-id" to "transcription")
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(sut.stopArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION).isSuccess)
        coVerify { archiveRepository.stopArchive(SESSION_KEY, ArchiveId("remote-transcription-id")) }
    }

    @Test
    fun `locally started transcription is kept while the session only reports the recording`() = runTest {
        coEvery { archiveRepository.startArchive(SESSION_KEY, withTranscription = true) } returns
            Result.success(ArchiveId("transcription-id"))
        coEvery { archiveRepository.stopArchive(SESSION_KEY, ArchiveId("transcription-id")) } returns
            Result.success(true)
        sut.startArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION)

        sut.bind(callFacade).test {
            awaitItem()
            activeArchives.value = mapOf("recording-id" to "recording")
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(sut.stopArchive(SESSION_KEY, ArchiveType.TRANSCRIPTION).isSuccess)
        coVerify { archiveRepository.stopArchive(SESSION_KEY, ArchiveId("transcription-id")) }
    }

    @Test
    fun `archive type is resolved from the archive name`() {
        assertEquals(ArchiveType.TRANSCRIPTION, ArchiveType.fromArchiveName("transcription"))
        assertEquals(ArchiveType.RECORDING, ArchiveType.fromArchiveName("recording"))
        assertEquals(ArchiveType.RECORDING, ArchiveType.fromArchiveName(null))
    }

    // endregion

    private companion object {
        const val SESSION_KEY = "test-session-key"
    }
}
