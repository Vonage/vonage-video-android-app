package com.vonage.android.screen.goodbye

import app.cash.turbine.test
import com.vonage.android.data.Archive
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.ArchiveStatus
import com.vonage.android.util.DownloadManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GoodbyeScreenViewModelTest {

    private val archiveRepository: ArchiveRepository = mockk()
    private val downloadManager: DownloadManager = mockk()
    private fun sut() = GoodbyeScreenViewModel(
        roomName = ANY_ROOM_NAME,
        archiveRepository = archiveRepository,
        downloadManager = downloadManager,
        dispatcher = UnconfinedTestDispatcher(),
    )

    @Ignore
    @Test
    fun `given viewmodel when initial state then returns archive list`() = runTest {
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns Result.success(archiveList)
        val sut = sut()
        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            assertEquals(
                GoodbyeScreenUiState.Content(
                    archives = archiveList.toImmutableList(),
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when initial state then poll archives`() = runTest {
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns
                Result.success(archiveList) andThen Result.success(archiveListAfterPolling)
        backgroundScope.launch {
            val sut = sut()
            sut.uiState.test {
                assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
                assertEquals(
                    GoodbyeScreenUiState.Content(
                        archives = archiveList.toImmutableList(),
                    ), awaitItem()
                )
                assertEquals(
                    GoodbyeScreenUiState.Content(
                        archives = archiveListAfterPolling.toImmutableList(),
                    ), awaitItem()
                )
                // because last response contains all archives with status available, polling ends
                expectNoEvents()
            }
        }
    }

    @Ignore
    @Test
    fun `given viewmodel when download then delegate to download manager`() = runTest {
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns Result.success(archiveList)
        every { downloadManager.downloadByUrl(any()) } returns Unit
        val sut = sut()
        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            assertEquals(
                GoodbyeScreenUiState.Content(
                    archives = archiveList.toImmutableList(),
                ), awaitItem()
            )
            sut.downloadArchive(archiveList[0])
            verify { downloadManager.downloadByUrl("https://cdn.recording.io/potatoe") }
        }
    }

    @Ignore
    @Test
    fun `given viewmodel when download pending archive then ignore`() = runTest {
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns Result.success(archiveList)
        every { downloadManager.downloadByUrl(any()) } returns Unit
        val sut = sut()
        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            assertEquals(
                GoodbyeScreenUiState.Content(
                    archives = archiveList.toImmutableList(),
                ), awaitItem()
            )
            sut.downloadArchive(archiveList[1])
            verify(exactly = 0) { downloadManager.downloadByUrl("https://cdn.recording.io/potatoe-pending") }
        }
    }

    val availableArchive = Archive(
        id = "archive-id",
        name = "recording 1",
        url = "https://cdn.recording.io/potatoe",
        status = ArchiveStatus.AVAILABLE,
        createdAt = 123
    )
    val pendingArchive = Archive(
        id = "archive-id-pending",
        name = "recording 1",
        url = "https://cdn.recording.io/potatoe-pending",
        status = ArchiveStatus.PENDING,
        createdAt = 123
    )
    val archiveList = listOf(
        availableArchive,
        pendingArchive,
    )
    val archiveListAfterPolling = listOf(
        availableArchive,
        availableArchive,
    )

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
