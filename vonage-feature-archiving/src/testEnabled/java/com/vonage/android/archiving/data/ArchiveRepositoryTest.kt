package com.vonage.android.archiving.data

import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ArchiveRepositoryTest {

    val apiService: ArchivingApi = mockk()
    val sut = ArchiveRepository(
        archivingApi = apiService,
    )

    @Test
    fun `given repository when getRecordings api success returns success`() = runTest {
        coEvery { apiService.getArchives("any-room-name") } returns Response<GetArchivesResponse>.success(
            GetArchivesResponse(archives = serverArchives)
        )
        val response = sut.getRecordings("any-room-name")
        assertEquals(Result.success(archives), response)
    }

    @Test
    fun `given repository when getRecordings api success with empty returns success`() = runTest {
        coEvery { apiService.getArchives("any-room-name") } returns Response.success(null)
        val response = sut.getRecordings("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when getRecordings api fails returns error`() = runTest {
        coEvery { apiService.getArchives("any-room-name") } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.getRecordings("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when getRecordings api fails with exception returns error`() = runTest {
        coEvery { apiService.getArchives("any-room-name") } throws Exception("Network error")
        val response = sut.getRecordings("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api success returns success`() = runTest {
        coEvery { apiService.startArchiving("any-room-name") } returns Response<StartArchivingResponse>.success(
            StartArchivingResponse(archiveId = "archive-id")
        )
        val response = sut.startArchive("any-room-name")
        assertEquals(Result.success(ArchiveId("archive-id")), response)
    }

    @Test
    fun `given repository when startArchiving api success with empty returns success`() = runTest {
        coEvery { apiService.startArchiving("any-room-name") } returns Response.success(null)
        val response = sut.startArchive("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api fails returns error`() = runTest {
        coEvery { apiService.startArchiving("any-room-name") } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.startArchive("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when startArchiving api fails with exception returns error`() = runTest {
        coEvery { apiService.startArchiving("any-room-name") } throws Exception("Network error")
        val response = sut.startArchive("any-room-name")
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when stopArchiving api success returns success`() = runTest {
        coEvery { apiService.stopArchiving("any-room-name", "archive-id") } returns
                Response<StartArchivingResponse>.success(StopArchivingResponse(archiveId = "archive-id"))
        val response = sut.stopArchive("any-room-name", ArchiveId("archive-id"))
        assertEquals(Result.success(true), response)
    }

    @Test
    fun `given repository when stopArchiving api fails returns error`() = runTest {
        coEvery { apiService.stopArchiving("any-room-name", "archive-id") } returns Response.error(
            500, ResponseBody.EMPTY
        )
        val response = sut.stopArchive("any-room-name", ArchiveId("archive-id"))
        assertTrue(response.isFailure)
    }

    @Test
    fun `given repository when stopArchiving api fails with exception returns error`() = runTest {
        coEvery { apiService.stopArchiving("any-room-name", "archive-id") } throws Exception("Network error")
        val response = sut.stopArchive("any-room-name", ArchiveId("archive-id"))
        assertTrue(response.isFailure)
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