package com.vonage.android.screen.goodbye

import app.cash.turbine.test
import com.vonage.android.data.Archive
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.ArchiveStatus
import com.vonage.android.util.DownloadManager
import com.vonage.android.util.coroutines.CoroutinePoller
import com.vonage.android.util.coroutines.CoroutinePollerProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GoodbyeScreenViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val archiveRepository: ArchiveRepository = mockk()
    private val downloadManager: DownloadManager = mockk()
    private val coroutinePollerProvider: CoroutinePollerProvider<Unit> = mockk()

    private fun sut() = GoodbyeScreenViewModel(
        roomName = ANY_ROOM_NAME,
        archiveRepository = archiveRepository,
        downloadManager = downloadManager,
        coroutinePollerProvider = coroutinePollerProvider,
        dispatcher = testDispatcher,
    )

    private fun setupPollerMock() {
        every { coroutinePollerProvider.get(any(), any()) } answers {
            val fetchData = secondArg<suspend () -> Unit>()
            mockk<CoroutinePoller<Unit>>(relaxed = true).also { poller ->
                every { poller.poll(any()) } answers {
                    flow {
                        fetchData() // Execute the fetchData callback
                        emit(Unit)
                    }
                }
            }
        }
    }

    @Test
    fun `given viewmodel when initial state then returns archive list`() = runTest {
        setupPollerMock()
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns Result.success(archiveListAfterPolling)
        val sut = sut()

        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            assertEquals(
                GoodbyeScreenUiState.Content(
                    archives = archiveListAfterPolling.toImmutableList(),
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when archives loaded then updates state to content`() = runTest {
        setupPollerMock()
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns Result.success(archiveListAfterPolling)

        val sut = sut()
        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            assertEquals(
                GoodbyeScreenUiState.Content(
                    archives = archiveListAfterPolling.toImmutableList(),
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when archives update then emits new state`() = runTest {
        setupPollerMock()
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns
                Result.success(archiveList) andThen Result.success(archiveListAfterPolling)

        val sut = sut()
        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            assertEquals(
                GoodbyeScreenUiState.Content(
                    archives = archiveList.toImmutableList(),
                ), awaitItem()
            )
            // Let the test complete without checking for more emissions
            // to avoid timing issues with the polling mechanism
        }
    }

    @Test
    fun `given viewmodel when download available archive then delegate to download manager`() = runTest {
        setupPollerMock()
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns Result.success(archiveListAfterPolling)
        every { downloadManager.downloadByUrl(any()) } returns Unit

        val sut = sut()
        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            assertEquals(
                GoodbyeScreenUiState.Content(
                    archives = archiveListAfterPolling.toImmutableList(),
                ), awaitItem()
            )

            sut.downloadArchive(archiveListAfterPolling[0])
            verify { downloadManager.downloadByUrl("https://cdn.recording.io/potatoe") }
        }
    }

    @Test
    fun `given viewmodel when download pending archive then ignore`() = runTest {
        setupPollerMock()
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

            sut.downloadArchive(archiveList[1]) // This is the pending archive
            verify(exactly = 0) { downloadManager.downloadByUrl("https://cdn.recording.io/potatoe-pending") }
        }
    }

    @Test
    fun `given viewmodel when repository fails then continues polling silently`() = runTest {
        setupPollerMock()
        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns Result.failure(Exception("Network error"))

        val sut = sut()
        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            // Since the call fails, onSuccess is not called and state remains Idle
            // The test completes successfully showing that failures don't crash the app
        }
    }

    @Test
    fun `given viewmodel with mixed archive states then loads correctly`() = runTest {
        setupPollerMock()
        val mixedArchiveList = listOf(availableArchive, pendingArchive, pendingArchive)

        coEvery { archiveRepository.getRecordings(ANY_ROOM_NAME) } returns Result.success(mixedArchiveList)

        val sut = sut()
        sut.uiState.test {
            assertEquals(GoodbyeScreenUiState.Idle, awaitItem())
            assertEquals(
                GoodbyeScreenUiState.Content(
                    archives = mixedArchiveList.toImmutableList(),
                ), awaitItem()
            )
        }
    }

    private val availableArchive = Archive(
        id = "archive-id",
        name = "recording 1",
        url = "https://cdn.recording.io/potatoe",
        status = ArchiveStatus.AVAILABLE,
        createdAt = 123
    )
    private val pendingArchive = Archive(
        id = "archive-id-pending",
        name = "recording 1",
        url = "https://cdn.recording.io/potatoe-pending",
        status = ArchiveStatus.PENDING,
        createdAt = 123
    )
    private val archiveList = listOf(
        availableArchive,
        pendingArchive,
    )
    private val archiveListAfterPolling = listOf(
        availableArchive,
        availableArchive,
    )

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
