package com.vonage.android.screen.room

import android.content.Context
import android.content.Intent
import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.config.Config
import com.vonage.android.config.GetConfig
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.notifications.VeraNotificationChannelRegistry.CallAction
import com.vonage.android.screen.components.audio.AudioDevicesHandler
import com.vonage.android.screen.components.audio.AudioDevicesState
import com.vonage.android.screensharing.ScreenSharingState
import com.vonage.android.screensharing.VonageScreenSharing
import com.vonage.android.service.VeraForegroundServiceHandler
import com.vonage.android.util.ActivityContextProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.Result.Companion.success

class MeetingRoomScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val activityContextProvider: ActivityContextProvider = mockk(relaxed = true)
    private val sessionRepository: SessionRepository = mockk()
    private val vonageArchiving: VonageArchiving = mockk(relaxed = true)
    private val vonageCaptions: VonageCaptions = mockk(relaxed = true)
    private val vonageScreenSharing: VonageScreenSharing = mockk(relaxed = true)
    private val videoClient: VonageVideoClient = mockk()
    private val getConfig: GetConfig = mockk()
    private val audioDevicesStateMock: AudioDevicesState = mockk()
    private val audioDevicesHandler: AudioDevicesHandler = mockk(relaxed = true) {
        every { audioDevicesState } returns audioDevicesStateMock
    }
    private val foregroundServiceHandler: VeraForegroundServiceHandler = mockk {
        every { startForegroundService(any()) } returns Unit
        every { stopForegroundService() } returns Unit
        every { actions } returns MutableSharedFlow()
    }

    private lateinit var sut: MeetingRoomScreenViewModel

    @Before
    fun setUp() {
        sut = MeetingRoomScreenViewModel(
            roomName = ANY_ROOM_NAME,
            sessionRepository = sessionRepository,
            vonageArchiving = vonageArchiving,
            vonageScreenSharing = vonageScreenSharing,
            vonageCaptions = vonageCaptions,
            videoClient = videoClient,
            foregroundServiceHandler = foregroundServiceHandler,
            activityContextProvider = activityContextProvider,
            getConfig = getConfig,
            audioDevicesHandler = audioDevicesHandler,
        )

        every { getConfig.invoke() } returns Config(
            allowCameraControl = true,
            allowMicrophoneControl = true,
            allowShowParticipantList = true,
        )
    }

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val mockCall = givenMockCall()

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
        }

        verify { activityContextProvider.setActivityContext(context) }
        verify { mockCall.connect(any(Context::class)) }
    }

    @Test
    fun `given viewmodel when initialize fails then returns error state`() = runTest {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns Result.failure(Exception("Empty response"))

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    isLoading = true,
                    isError = false,
                    audioDevicesState = null,
                ), awaitItem()
            )
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    isLoading = false,
                    isError = true,
                    audioDevicesState = audioDevicesStateMock,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when onToggleMic then delegate to call`() = runTest {
        val mockCall = givenMockCall()

        sut.setup(context)

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            awaitItem()
            sut.onToggleMic()
            verify { mockCall.toggleLocalAudio() }
        }
    }

    @Test
    fun `given viewmodel when onToggleCamera then delegate to call`() = runTest {
        val mockCall = givenMockCall()

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.onToggleCamera()
            verify { mockCall.toggleLocalVideo() }
        }
    }

    @Test
    fun `given viewmodel when endCall then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        every { vonageScreenSharing.stopSharingScreen() } returns Unit

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.endCall()
            verify { mockCall.endSession() }
            verify { vonageScreenSharing.stopSharingScreen() }
        }
    }

    @Test
    fun `given viewmodel when onSwitchCamera then delegate to call`() = runTest {
        val mockCall = givenMockCall()

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.onSwitchCamera()
            verify { mockCall.toggleLocalCamera() }
        }
    }

    @Test
    fun `given viewmodel when sendMessage then delegate to call`() = runTest {
        val mockCall = givenMockCall()

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.sendMessage("hi there!")
            verify { mockCall.sendChatMessage("hi there!") }
        }
    }

    @Test
    fun `given viewmodel when listenUnread then delegate to call`() = runTest {
        val mockCall = givenMockCall()

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.listenUnread(false)
            verify { mockCall.listenUnreadChatMessages(false) }
        }
    }

    @Test
    fun `given viewmodel when sendEmoji then delegate to call`() = runTest {
        val mockCall = givenMockCall()

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.sendEmoji("emoji :)")
            verify { mockCall.sendEmoji("emoji :)") }
        }
    }

    @Test
    fun `given viewmodel when archiveCall true then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageArchiving.startArchive(ANY_ROOM_NAME) } returns success(ArchiveId("archiveId"))

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
            sut.archiveCall(true)
            assertEquals(ArchivingUiState.STARTING, awaitItem().archivingUiState)
            assertEquals(ArchivingUiState.RECORDING, awaitItem().archivingUiState)
            coVerify { vonageArchiving.startArchive(ANY_ROOM_NAME) }
        }
    }

    @Test
    fun `given viewmodel when archiveCall false then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageArchiving.startArchive(ANY_ROOM_NAME) } returns success(ArchiveId("archiveId"))
        coEvery { vonageArchiving.stopArchive(ANY_ROOM_NAME) } returns success(true)

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
            sut.archiveCall(true)
            assertEquals(ArchivingUiState.STARTING, awaitItem().archivingUiState)
            coVerify { vonageArchiving.startArchive(ANY_ROOM_NAME) }
            assertEquals(ArchivingUiState.RECORDING, awaitItem().archivingUiState)
            sut.archiveCall(false)
            assertEquals(ArchivingUiState.STOPPING, awaitItem().archivingUiState)
            coVerify { vonageArchiving.stopArchive(ANY_ROOM_NAME) }
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
        }
    }

    @Test
    fun `given viewmodel when other participant start archiving then emit correct state`() =
        runTest {
            val mockCall = givenMockCall()
            val archivingStateFlow = MutableSharedFlow<ArchivingState>()
            every { vonageArchiving.bind(mockCall) } returns archivingStateFlow
            coEvery { vonageArchiving.startArchive(ANY_ROOM_NAME) } returns success(ArchiveId("archiveId"))

            sut.setup(context)
            testScheduler.advanceUntilIdle()

            sut.uiState.test {
                assertEquals(
                    MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                    awaitItem()
                )
                assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
                archivingStateFlow.emit(ArchivingState.Started("any-archiving-id"))
                assertEquals(ArchivingUiState.RECORDING, awaitItem().archivingUiState)
            }
        }

    @Test
    fun `given viewmodel when other participant stops archiving then emit correct state`() =
        runTest {
            val mockCall = givenMockCall()
            val archivingStateFlow = MutableSharedFlow<ArchivingState>()
            every { vonageArchiving.bind(mockCall) } returns archivingStateFlow
            coEvery { vonageArchiving.startArchive(ANY_ROOM_NAME) } returns success(ArchiveId("archiveId"))

            sut.setup(context)
            testScheduler.advanceUntilIdle()

            sut.uiState.test {
                assertEquals(
                    MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                    awaitItem()
                )
                assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
                archivingStateFlow.emit(ArchivingState.Started("any-archiving-id"))
                awaitItem()
                archivingStateFlow.emit(ArchivingState.Stopped("any-archiving-id"))
                assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
            }
        }

    @Test
    fun `given viewmodel when startScreenSharing started then emit correct state`() = runTest {
        val mockCall = givenMockCall()
        val mockIntent: Intent = mockk(relaxed = true)
        val onStartSlot = slot<() -> Unit>()
        val onStopSlot = slot<() -> Unit>()

        every {
            vonageScreenSharing.startScreenSharing(
                intent = mockIntent,
                call = mockCall,
                onStarted = capture(onStartSlot),
                onStopped = capture(onStopSlot),
            )
        } returns Unit

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            assertEquals(ScreenSharingState.IDLE, awaitItem().screenSharingState)
            sut.startScreenSharing(mockIntent)
            assertEquals(ScreenSharingState.STARTING, awaitItem().screenSharingState)
            onStartSlot.captured.invoke()
            assertEquals(ScreenSharingState.SHARING, awaitItem().screenSharingState)
            verify { vonageScreenSharing.startScreenSharing(mockIntent, mockCall, any(), any()) }
        }
    }

    @Test
    fun `given viewmodel when startScreenSharing stopped then emit correct state`() = runTest {
        val mockCall = givenMockCall()
        val mockIntent: Intent = mockk(relaxed = true)
        val onStartSlot = slot<() -> Unit>()
        val onStopSlot = slot<() -> Unit>()

        every {
            vonageScreenSharing.startScreenSharing(
                intent = mockIntent,
                call = mockCall,
                onStarted = capture(onStartSlot),
                onStopped = capture(onStopSlot),
            )
        } returns Unit

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            assertEquals(ScreenSharingState.IDLE, awaitItem().screenSharingState)
            sut.startScreenSharing(mockIntent)
            assertEquals(ScreenSharingState.STARTING, awaitItem().screenSharingState)
            onStopSlot.captured.invoke()
            assertEquals(ScreenSharingState.IDLE, awaitItem().screenSharingState)
        }
    }

    @Test
    fun `given viewmodel when stopScreenSharing stopped then emit correct state`() = runTest {
        givenMockCall()
        every { vonageScreenSharing.stopSharingScreen() } returns Unit

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            assertEquals(ScreenSharingState.IDLE, awaitItem().screenSharingState)
            sut.stopScreenSharing()
            assertEquals(ScreenSharingState.STOPPING, awaitItem().screenSharingState)
            verify { vonageScreenSharing.stopSharingScreen() }
        }
    }

    @Test
    fun `given viewmodel when init with captionsId then emit correct state`() = runTest {
        val mockCall = givenMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse(
            captionsId = "captionsId",
        )
        every { videoClient.buildPublisher(context) } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
        }
    }

    @Test
    fun `given viewmodel when enable captions then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageCaptions.enable() } returns success(Unit)

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.captions(true)
            assertEquals(CaptionsUiState.ENABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
            coVerify { vonageCaptions.enable() }
        }
    }

    @Test
    fun `given viewmodel when enable captions fails then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageCaptions.enable() } returns Result.failure(Exception("KO"))

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            sut.captions(true)
            assertEquals(CaptionsUiState.IDLE, awaitItem().captionsUiState)
        }
    }

    @Test
    fun `given viewmodel when disable captions then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageCaptions.enable() } returns success(Unit)
        coEvery { vonageCaptions.disable() } returns success(Unit)

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.captions(true)
            assertEquals(CaptionsUiState.ENABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
            coVerify { vonageCaptions.enable() }
            // disable captions
            sut.captions(false)
            assertEquals(CaptionsUiState.DISABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.IDLE, awaitItem().captionsUiState)
            coVerify { vonageCaptions.disable() }
        }
    }

    @Test
    fun `given viewmodel when disable captions fails then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageCaptions.enable() } returns success(Unit)
        coEvery { vonageCaptions.disable() } returns Result.failure(Exception("KO"))

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            // enable captions
            sut.captions(true)
            assertEquals(CaptionsUiState.ENABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
        }
        sut.uiState.test {
            // disable captions
            sut.captions(false)
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
        }
        coVerify { vonageCaptions.enable() }
    }

    @Test
    fun `given viewmodel when initialize then create foreground service`() {
        verify { foregroundServiceHandler.startForegroundService(ANY_ROOM_NAME) }
    }

    @Test
    fun `given viewmodel when receive CallActionHangUp then update state`() = runTest {
        givenMockCall()
        val callActionsFlow = MutableStateFlow<CallAction?>(null)
        every { foregroundServiceHandler.actions } returns callActionsFlow

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            callActionsFlow.value = CallAction.HangUp
            assertTrue(awaitItem().isEndCall)
        }
    }

    @Test
    fun `given viewmodel when change layout then update state`() = runTest {
        givenMockCall()

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(
                MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true),
                awaitItem()
            )
            awaitItem()
            sut.changeLayout(CallLayoutType.SPEAKER_LAYOUT)
            assertEquals(
                CallLayoutType.SPEAKER_LAYOUT, awaitItem().layoutType
            )
            sut.changeLayout(CallLayoutType.GRID)
            assertEquals(
                CallLayoutType.GRID, awaitItem().layoutType
            )
        }
    }

    private fun givenMockCall(): CallFacade {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher(any()) } returns buildMockPublisher()

        val mockCall = buildMockCall()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        every { mockCall.connect(any()) } returns flowOf()

        return mockCall
    }

    private fun buildSuccessSessionResponse(
        apiKey: String = "api-key",
        sessionId: String = "session-id",
        token: String = "token",
        captionsId: String? = null,
    ) = success(
        SessionInfo(
            apiKey = apiKey,
            sessionId = sessionId,
            token = token,
            captionsId = captionsId,
        )
    )

    @Suppress("LongParameterList")
    private fun buildMockPublisher() = mockk<PublisherState>()

    private fun buildMockCall(): CallFacade = mockk<CallFacade>(relaxed = true) {
        every { connect(any()) } returns flowOf()
    }

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
