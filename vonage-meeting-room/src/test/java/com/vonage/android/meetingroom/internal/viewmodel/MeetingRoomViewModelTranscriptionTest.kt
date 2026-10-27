package com.vonage.android.meetingroom.internal.viewmodel

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveType
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.fx.data.BackgroundsResult
import com.vonage.android.fx.data.GetBackgroundsUseCase
import com.vonage.android.fx.data.UserBackgroundRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.meetingroom.MainDispatcherRule
import com.vonage.android.meetingroom.api.MeetingRoomConfiguration
import com.vonage.android.meetingroom.api.MeetingRoomFeature
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.meetingroom.api.SessionKeyHolder
import com.vonage.android.meetingroom.internal.container.MeetingRoomContainer
import com.vonage.android.meetingroom.internal.screen.TranscriptionNotification
import com.vonage.android.meetingroom.internal.screen.audio.MeetingRoomAudioDevicesHandler
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.android.shared.session.SessionInfo
import com.vonage.android.shared.session.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.Result.Companion.success

/**
 * Post-call transcription in [MeetingRoomViewModel]: a transcription archive tracked separately
 * from, and concurrently with, the recording. General behavior lives in [MeetingRoomViewModelTest].
 */
class MeetingRoomViewModelTranscriptionTest {

    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val container: MeetingRoomContainer = mockk(relaxed = true)
    private val prebuilt: MeetingRoomPrebuilt = mockk(relaxed = true)
    private val sessionRepository: SessionRepository = mockk()
    private val vonageArchiving: VonageArchiving = mockk(relaxed = true)
    private val videoClient: VonageVideoClient = mockk(relaxed = true)
    private val sessionKeyHolder = SessionKeyHolder()
    private val audioDevicesHandler: MeetingRoomAudioDevicesHandler = mockk(relaxed = true) {
        every { audioDevicesState } returns mockk(relaxed = true)
    }
    private val foregroundServiceHandler: MeetingRoomForegroundServiceHandler = mockk(relaxed = true) {
        every { actions } returns MutableSharedFlow()
    }
    private val getBackgroundsUseCase: GetBackgroundsUseCase = mockk {
        coEvery { invoke(captureResolution = null) } returns BackgroundsResult(
            persistentListOf(),
            remainingBackgroundSlots = UserBackgroundRepository.MAX_USER_BACKGROUNDS,
        )
    }

    private lateinit var sut: MeetingRoomViewModel

    @BeforeEach
    fun setUp() {
        every { container.prebuilt } returns prebuilt
        every { container.sessionRepository } returns sessionRepository
        every { container.sessionKeyHolder } returns sessionKeyHolder
        every { container.vonageArchiving } returns vonageArchiving
        every { container.videoClient } returns videoClient
        every { container.foregroundServiceHandler } returns foregroundServiceHandler
        every { container.audioDevicesHandler } returns audioDevicesHandler
        every { container.callSettingsHolder } returns CallSettingsHolder()
        every { container.getBackgroundsUseCase } returns getBackgroundsUseCase

        every { prebuilt.roomName } returns ANY_ROOM_NAME
        every { prebuilt.configuration } returns MeetingRoomConfiguration()
        every { prebuilt.publisherSettings } returns PublisherSettings()
        every { prebuilt.enabledFeatures } returns MeetingRoomFeature.all
        every { prebuilt.foregroundServiceEnabled } returns true
        every { prebuilt.hangUpCommand } returns MutableSharedFlow()

        sut = MeetingRoomViewModel(container)
    }


    @Test
    fun `given viewmodel when transcribeCall true then starts transcription archive only`() = runTest {
        givenMockCall()
        coEvery { vonageArchiving.startArchive(ANY_SESSION_KEY, ArchiveType.TRANSCRIPTION, withSummary = true) } returns
            success(ArchiveId("transcription-id"))

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.transcribeCall(true)
            assertEquals(ArchivingUiState.STARTING, awaitItem().transcriptionUiState)
            val started = awaitItem()
            assertEquals(ArchivingUiState.RECORDING, started.transcriptionUiState)
            assertEquals(ArchivingUiState.IDLE, started.archivingUiState)
            coVerify { vonageArchiving.startArchive(ANY_SESSION_KEY, ArchiveType.TRANSCRIPTION, withSummary = true) }
            coVerify(exactly = 0) { vonageArchiving.startArchive(any(), ArchiveType.RECORDING, any()) }
        }
    }

    @Test
    fun `given summary unchecked when transcription starts then requests it without summary`() = runTest {
        givenMockCall()
        coEvery { vonageArchiving.startArchive(ANY_SESSION_KEY, ArchiveType.TRANSCRIPTION, withSummary = false) } returns
            success(ArchiveId("transcription-id"))

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.transcribeCall(enable = true, withSummary = false)
            awaitItem() // starting
            assertEquals(ArchivingUiState.RECORDING, awaitItem().transcriptionUiState)
            coVerify { vonageArchiving.startArchive(ANY_SESSION_KEY, ArchiveType.TRANSCRIPTION, withSummary = false) }
        }
    }

    @Test
    fun `given transcription running when transcribeCall false then stops transcription archive`() = runTest {
        givenMockCall()
        coEvery { vonageArchiving.startArchive(ANY_SESSION_KEY, ArchiveType.TRANSCRIPTION, withSummary = true) } returns
            success(ArchiveId("transcription-id"))
        coEvery { vonageArchiving.stopArchive(ANY_SESSION_KEY, ArchiveType.TRANSCRIPTION) } returns success(true)

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.transcribeCall(true)
            awaitItem() // starting
            awaitItem() // transcribing

            sut.transcribeCall(false)
            assertEquals(ArchivingUiState.STOPPING, awaitItem().transcriptionUiState)
            assertEquals(ArchivingUiState.IDLE, awaitItem().transcriptionUiState)
            coVerify { vonageArchiving.stopArchive(ANY_SESSION_KEY, ArchiveType.TRANSCRIPTION) }
            coVerify(exactly = 0) { vonageArchiving.stopArchive(any(), ArchiveType.RECORDING) }
        }
    }

    @Test
    fun `given transcription start fails then transcription returns to idle`() = runTest {
        givenMockCall()
        coEvery { vonageArchiving.startArchive(ANY_SESSION_KEY, ArchiveType.TRANSCRIPTION, withSummary = true) } returns
            Result.failure(Exception("boom"))

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.transcribeCall(true)
            assertEquals(ArchivingUiState.STARTING, awaitItem().transcriptionUiState)
            assertEquals(ArchivingUiState.IDLE, awaitItem().transcriptionUiState)
        }
    }

    @Test
    fun `given recording and transcription running when transcription stops then recording keeps running`() =
        runTest {
            val mockCall = givenMockCall()
            val runningArchives = MutableSharedFlow<Map<ArchiveType, ArchiveId>>()
            every { vonageArchiving.bind(mockCall) } returns runningArchives

            sut.uiState.test {
                awaitItem()
                sut.setup(context)
                testScheduler.advanceUntilIdle()
                awaitItem() // audio devices
                awaitItem() // connected

                runningArchives.emit(mapOf(RUNNING_RECORDING))
                runningArchives.emit(mapOf(RUNNING_RECORDING, RUNNING_TRANSCRIPTION))
                testScheduler.runCurrent()
                with(sut.uiState.value) {
                    assertEquals(ArchivingUiState.RECORDING, archivingUiState)
                    assertEquals(ArchivingUiState.RECORDING, transcriptionUiState)
                }

                runningArchives.emit(mapOf(RUNNING_RECORDING))
                testScheduler.runCurrent()
                with(sut.uiState.value) {
                    assertEquals(ArchivingUiState.RECORDING, archivingUiState)
                    assertEquals(ArchivingUiState.IDLE, transcriptionUiState)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given recording and transcription running when recording stops then transcription keeps running`() =
        runTest {
            val mockCall = givenMockCall()
            val runningArchives = MutableSharedFlow<Map<ArchiveType, ArchiveId>>()
            every { vonageArchiving.bind(mockCall) } returns runningArchives

            sut.uiState.test {
                awaitItem()
                sut.setup(context)
                testScheduler.advanceUntilIdle()
                awaitItem() // audio devices
                awaitItem() // connected

                runningArchives.emit(mapOf(RUNNING_RECORDING))
                runningArchives.emit(mapOf(RUNNING_RECORDING, RUNNING_TRANSCRIPTION))
                runningArchives.emit(mapOf(RUNNING_TRANSCRIPTION))
                testScheduler.runCurrent()
                with(sut.uiState.value) {
                    assertEquals(ArchivingUiState.IDLE, archivingUiState)
                    assertEquals(ArchivingUiState.RECORDING, transcriptionUiState)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given both archives already running when joining then both are shown`() = runTest {
        val mockCall = givenMockCall()
        every { vonageArchiving.bind(mockCall) } returns MutableStateFlow(
            mapOf(
                ArchiveType.RECORDING to ArchiveId("recording-id"),
                ArchiveType.TRANSCRIPTION to ArchiveId("transcription-id"),
            ),
        )

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.runCurrent()
            with(sut.uiState.value) {
                assertEquals(ArchivingUiState.RECORDING, archivingUiState)
                assertEquals(ArchivingUiState.RECORDING, transcriptionUiState)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given remote transcription starts then shows started notification without recording overlay`() = runTest {
        val mockCall = givenMockCall()
        val runningArchives = MutableSharedFlow<Map<ArchiveType, ArchiveId>>()
        every { vonageArchiving.bind(mockCall) } returns runningArchives

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            runningArchives.emit(mapOf(RUNNING_TRANSCRIPTION))
            val started = awaitItem()
            assertEquals(ArchivingUiState.RECORDING, started.transcriptionUiState)
            assertEquals(TranscriptionNotification.STARTED, started.transcriptionNotification)
            assertEquals(ArchivingUiState.IDLE, started.archivingUiState)
            assertFalse(started.recordingStartedByOthers)

            // Notification auto-dismiss
            testScheduler.advanceTimeBy(6000L)
            assertEquals(null, awaitItem().transcriptionNotification)
        }
    }

    @Test
    fun `given transcription stops then shows stopped notification`() = runTest {
        val mockCall = givenMockCall()
        val runningArchives = MutableSharedFlow<Map<ArchiveType, ArchiveId>>()
        every { vonageArchiving.bind(mockCall) } returns runningArchives

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            runningArchives.emit(mapOf(RUNNING_TRANSCRIPTION))
            awaitItem()
            runningArchives.emit(emptyMap())
            val stopped = awaitItem()
            assertEquals(ArchivingUiState.IDLE, stopped.transcriptionUiState)
            assertEquals(TranscriptionNotification.STOPPED, stopped.transcriptionNotification)

            testScheduler.advanceTimeBy(6000L)
            assertEquals(null, awaitItem().transcriptionNotification)
        }
    }

    private fun givenMockCall(): CallFacade {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns success(
            SessionInfo(
                applicationId = "application-id",
                sessionId = "session-id",
                token = "token",
                sessionKey = ANY_SESSION_KEY,
            ),
        )
        val mockCall: CallFacade = mockk(relaxed = true) {
            every { publisher } returns MutableStateFlow<PublisherState?>(null)
            every { participantsCount } returns MutableStateFlow(0)
            every { connect(any()) } returns flowOf()
        }
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        return mockCall
    }

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
        const val ANY_SESSION_KEY = "session-key"
        val RUNNING_RECORDING = ArchiveType.RECORDING to ArchiveId("recording-id")
        val RUNNING_TRANSCRIPTION = ArchiveType.TRANSCRIPTION to ArchiveId("transcription-id")
    }
}
