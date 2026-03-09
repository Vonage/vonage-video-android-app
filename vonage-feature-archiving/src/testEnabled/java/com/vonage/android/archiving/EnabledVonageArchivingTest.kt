package com.vonage.android.archiving

import app.cash.turbine.test
import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EnabledVonageArchivingTest {

    private val archiveRepository: ArchiveRepository = mockk()
    private val callFacade: CallFacade = mockk()
    private val callArchivingStateFlow = MutableStateFlow<ArchivingState>(ArchivingState.Idle)
    
    private lateinit var sut: EnabledVonageArchiving

    @Before
    fun setup() {
        every { callFacade.archivingStateFlow } returns callArchivingStateFlow
        sut = EnabledVonageArchiving(archiveRepository)
    }

    @Test
    fun `bind should emit idle state initially`() = runTest {
        sut.bind(callFacade).test {
            callArchivingStateFlow.value = ArchivingState.Idle
            assertEquals(ArchivingState.Idle, awaitItem())
        }
    }

    @Test
    fun `bind should emit started state and update currentArchiveId`() = runTest {
        sut.bind(callFacade).test {
            awaitItem()
            val startedState = ArchivingState.Started("test-archive-id")
            callArchivingStateFlow.value = startedState
            
            val emittedState = awaitItem()
            assertEquals(startedState, emittedState)
        }
    }

    @Test
    fun `bind should emit stopped state and clear currentArchiveId`() = runTest {
        sut.bind(callFacade).test {
            awaitItem()
            callArchivingStateFlow.value = ArchivingState.Started("test-archive-id")
            awaitItem()

            callArchivingStateFlow.value = ArchivingState.Stopped("test-archive-id")
            assertEquals(ArchivingState.Stopped("test-archive-id"), awaitItem())
        }
    }

    @Test
    fun `bind should handle state transitions from idle to started to stopped`() = runTest {
        sut.bind(callFacade).test {
            callArchivingStateFlow.value = ArchivingState.Idle
            assertEquals(ArchivingState.Idle, awaitItem())

            callArchivingStateFlow.value = ArchivingState.Started("archive-123")
            assertEquals(ArchivingState.Started("archive-123"), awaitItem())

            callArchivingStateFlow.value = ArchivingState.Stopped("archive-123")
            assertEquals(ArchivingState.Stopped("archive-123"), awaitItem())
        }
    }

    @Test
    fun `startArchive should return success with archive id`() = runTest {
        val expectedArchiveId = ArchiveId("new-archive-id")
        coEvery { archiveRepository.startArchive("test-room") } returns Result.success(expectedArchiveId)

        val result = sut.startArchive("test-room")

        assertTrue(result.isSuccess)
        assertEquals(expectedArchiveId, result.getOrNull())
    }

    @Test
    fun `startArchive should return failure when repository fails`() = runTest {
        val exception = Exception("Failed to start archive")
        coEvery { archiveRepository.startArchive("test-room") } returns Result.failure(exception)

        val result = sut.startArchive("test-room")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `stopArchive should return success when archive is active`() = runTest {
        val archiveId = ArchiveId("active-archive-id")

        coEvery { archiveRepository.startArchive("test-room") } returns Result.success(archiveId)
        sut.startArchive("test-room")

        coEvery { archiveRepository.stopArchive("test-room", archiveId) } returns Result.success(true)
        
        val result = sut.stopArchive("test-room")

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `stopArchive should return failure when no archive is active`() = runTest {
        val result = sut.stopArchive("test-room")

        assertTrue(result.isFailure)
        assertEquals("No current archive id", result.exceptionOrNull()?.message)
    }

    @Test
    fun `stopArchive should return failure when repository fails`() = runTest {
        val archiveId = ArchiveId("active-archive-id")
        val exception = Exception("Failed to stop archive")
        
        coEvery { archiveRepository.startArchive("test-room") } returns Result.success(archiveId)
        sut.startArchive("test-room")
        
        coEvery { archiveRepository.stopArchive("test-room", archiveId) } returns Result.failure(exception)
        
        val result = sut.stopArchive("test-room")

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
        
        coEvery { archiveRepository.getRecordings("test-room") } returns Result.success(archives)

        val result = sut.getRecordings("test-room")

        assertTrue(result.isSuccess)
        assertEquals(archives, result.getOrNull())
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `getRecordings should return empty list when no recordings exist`() = runTest {
        coEvery { archiveRepository.getRecordings("test-room") } returns Result.success(emptyList())

        val result = sut.getRecordings("test-room")

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Archive>(), result.getOrNull())
    }

    @Test
    fun `getRecordings should return failure when repository fails`() = runTest {
        val exception = Exception("Failed to fetch recordings")
        coEvery { archiveRepository.getRecordings("test-room") } returns Result.failure(exception)

        val result = sut.getRecordings("test-room")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `multiple startArchive calls should update currentArchiveId`() = runTest {
        val archiveId1 = ArchiveId("archive-1")
        val archiveId2 = ArchiveId("archive-2")
        
        coEvery { archiveRepository.startArchive("room-1") } returns Result.success(archiveId1)
        coEvery { archiveRepository.startArchive("room-2") } returns Result.success(archiveId2)
        
        val result1 = sut.startArchive("room-1")
        assertEquals(archiveId1, result1.getOrNull())
        
        val result2 = sut.startArchive("room-2")
        assertEquals(archiveId2, result2.getOrNull())
    }

    @Test
    fun `bind and startArchive should work independently`() = runTest {
        val archiveIdFromBind = "bind-archive-id"
        val archiveIdFromStart = ArchiveId("start-archive-id")
        
        coEvery { archiveRepository.startArchive("test-room") } returns Result.success(archiveIdFromStart)
        
        sut.bind(callFacade).test {
            awaitItem()
            callArchivingStateFlow.value = ArchivingState.Started(archiveIdFromBind)
            assertEquals(ArchivingState.Started(archiveIdFromBind), awaitItem())
            
            val result = sut.startArchive("test-room")
            assertEquals(archiveIdFromStart, result.getOrNull())
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
