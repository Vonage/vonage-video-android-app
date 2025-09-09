package com.vonage.android.screen.room

import android.content.Intent
import android.media.projection.MediaProjection
import app.cash.turbine.test
import com.vonage.android.CoroutineTest
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.CaptionsRepository
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.model.VideoSource
import com.vonage.android.notifications.VeraNotificationChannelRegistry.CallAction
import com.vonage.android.screensharing.ScreenSharingServiceListener
import com.vonage.android.screensharing.VeraScreenSharingManager
import com.vonage.android.service.VeraForegroundServiceHandler
import io.mockk.clearAllMocks
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class MeetingRoomScreenViewModelTest : CoroutineTest() {

    val sessionRepository: SessionRepository = mockk()
    val archiveRepository: ArchiveRepository = mockk()
    val captionsRepository: CaptionsRepository = mockk()
    val screenSharingManager: VeraScreenSharingManager = mockk()
    val videoClient: VonageVideoClient = mockk()
    val foregroundServiceHandler: VeraForegroundServiceHandler = mockk {
        every { startForegroundService(any()) } returns Unit
        every { stopForegroundService() } returns Unit
        every { actions } returns MutableSharedFlow()
    }

    @BeforeEach
    fun setUp() {
        setMainDispatcherToTestDispatcher()
    }

    @AfterEach
    fun tearDown() {
        resetMain()
        clearAllMocks()
    }

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        every { mockCall.observeLocalAudioLevel() } returns flowOf(0.4f)
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
        }
        sut.audioLevel.test {
            assertEquals(0.0f, awaitItem()) // initial value
            assertEquals(0.4f, awaitItem())
        }
        verify { mockCall.connect() }
        verify { mockCall.observeLocalAudioLevel() }
    }

    @Test
    fun `given viewmodel when initialize fails then returns error state`() = runTest {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns Result.failure(Exception("Empty response"))

        sut().uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true, isError = false), awaitItem())
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true, isError = true), awaitItem())
        }
    }

    @Test
    fun `given viewmodel when onToggleMic then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.onToggleMic()
            verify { mockCall.toggleLocalAudio() }
        }
    }

    @Test
    fun `given viewmodel when onToggleCamera then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.onToggleCamera()
            verify { mockCall.toggleLocalVideo() }
        }
    }

    @Test
    fun `given viewmodel when endCall then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        every { screenSharingManager.stopSharingScreen() } returns Unit
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.endCall()
            verify { mockCall.endSession() }
            verify { screenSharingManager.stopSharingScreen() }
        }
    }

    @Test
    fun `given viewmodel when onPause then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            awaitItem()
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.onPause()
            verify { mockCall.pauseSession() }
        }
    }

    @Test
    fun `given viewmodel when onResume then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            awaitItem()
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.onResume()
            verify { mockCall.resumeSession() }
        }
    }

    @Test
    fun `given viewmodel when onSwitchCamera then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.onSwitchCamera()
            verify { mockCall.toggleLocalCamera() }
        }
    }

    @Test
    fun `given viewmodel when sendMessage then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.sendMessage("hi there!")
            verify { mockCall.sendChatMessage("hi there!") }
        }
    }

    @Test
    fun `given viewmodel when listenUnread then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.listenUnread(false)
            verify { mockCall.listenUnreadChatMessages(false) }
        }
    }

    @Test
    fun `given viewmodel when sendEmoji then delegate to call`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.sendEmoji("emoji :)")
            verify { mockCall.sendEmoji("emoji :)") }
        }
    }

    @Test
    fun `given viewmodel when archiveCall true then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        coEvery { archiveRepository.startArchive(ANY_ROOM_NAME) } returns Result.success("archiveId")
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.archiveCall(true)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.STARTING,
                ), awaitItem()
            )
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.RECORDING,
                ), awaitItem()
            )
            coVerify { archiveRepository.startArchive(ANY_ROOM_NAME) }
        }
    }

    @Test
    fun `given viewmodel when archiveCall false then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        coEvery { archiveRepository.startArchive(ANY_ROOM_NAME) } returns Result.success("archiveId")
        coEvery { archiveRepository.stopArchive(ANY_ROOM_NAME, "archiveId") } returns Result.success(true)
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
            sut.archiveCall(true)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.STARTING,
                ), awaitItem()
            )
            coVerify { archiveRepository.startArchive(ANY_ROOM_NAME) }
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.RECORDING,
                ), awaitItem()
            )
            sut.archiveCall(false)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.STOPPING,
                ), awaitItem()
            )
            coVerify { archiveRepository.stopArchive(ANY_ROOM_NAME, "archiveId") }
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    recordingState = RecordingState.IDLE,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when startScreenSharing started then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        val mediaProjection: MediaProjection = mockk(relaxed = true)
        val mockIntent: Intent = mockk(relaxed = true)
        val listenerSpy = slot<ScreenSharingServiceListener>()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        every {
            screenSharingManager.startScreenSharing(
                mockIntent, capture<ScreenSharingServiceListener>(listenerSpy)
            )
        } returns Unit
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    screenSharingState = ScreenSharingState.IDLE,
                ), awaitItem()
            )
            sut.startScreenSharing(mockIntent)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    screenSharingState = ScreenSharingState.STARTING,
                ), awaitItem()
            )
            listenerSpy.captured.onStarted(mediaProjection)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    screenSharingState = ScreenSharingState.SHARING,
                ), awaitItem()
            )
            verify { mockCall.startCapturingScreen(mediaProjection) }
        }
    }

    @Test
    fun `given viewmodel when startScreenSharing stopped then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        val mockIntent: Intent = mockk(relaxed = true)
        val listenerSpy = slot<ScreenSharingServiceListener>()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        every {
            screenSharingManager.startScreenSharing(
                mockIntent, capture<ScreenSharingServiceListener>(listenerSpy)
            )
        } returns Unit
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    screenSharingState = ScreenSharingState.IDLE,
                ), awaitItem()
            )
            sut.startScreenSharing(mockIntent)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    screenSharingState = ScreenSharingState.STARTING,
                ), awaitItem()
            )
            listenerSpy.captured.onStopped()
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    screenSharingState = ScreenSharingState.IDLE,
                ), awaitItem()
            )
            verify { mockCall.stopCapturingScreen() }
        }
    }

    @Test
    fun `given viewmodel when stopScreenSharing stopped then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        every { screenSharingManager.stopSharingScreen() } returns Unit
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    screenSharingState = ScreenSharingState.IDLE,
                ), awaitItem()
            )
            sut.stopScreenSharing()
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    screenSharingState = ScreenSharingState.STOPPING,
                ), awaitItem()
            )
            verify { screenSharingManager.stopSharingScreen() }
        }
    }

    @Test
    fun `given viewmodel when init with captionsId then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse(
            captionsId = "captionsId",
        )
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    captionsState = CaptionsState.ENABLED,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when enable captions then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        coEvery { captionsRepository.enableCaptions(ANY_ROOM_NAME) } returns Result.success("captionsId")
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            sut.captions(true)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    captionsState = CaptionsState.ENABLED,
                ), awaitItem()
            )
            verify { mockCall.enableCaptions(true) }
        }
    }

    @Test
    fun `given viewmodel when enable captions fails then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        coEvery { captionsRepository.enableCaptions(ANY_ROOM_NAME) } returns Result.failure(Exception("KO"))
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            sut.captions(true)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    captionsState = CaptionsState.IDLE,
                ), awaitItem()
            )
            verify(exactly = 0) { mockCall.enableCaptions(true) }
        }
    }

    @Test
    fun `given viewmodel when disable captions then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        coEvery { captionsRepository.enableCaptions(ANY_ROOM_NAME) } returns Result.success("captionsId")
        coEvery { captionsRepository.disableCaptions(ANY_ROOM_NAME, "captionsId") } returns Result.success("OK")
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            sut.captions(true)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    captionsState = CaptionsState.ENABLED,
                ), awaitItem()
            )
            verify { mockCall.enableCaptions(true) }
            // disable captions
            sut.captions(false)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    captionsState = CaptionsState.IDLE,
                ), awaitItem()
            )
            verify { mockCall.enableCaptions(false) }
        }
    }

    @Test
    fun `given viewmodel when disable captions fails then emit correct state`() = runTest {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall

        coEvery { captionsRepository.enableCaptions(ANY_ROOM_NAME) } returns Result.success("captionsId")
        coEvery { captionsRepository.disableCaptions(ANY_ROOM_NAME, "captionsId") } returns
                Result.failure(Exception("KO"))
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            // enable captions
            sut.captions(true)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    captionsState = CaptionsState.ENABLED,
                ), awaitItem()
            )
        }
        sut.uiState.test {
            // disable captions
            sut.captions(false)
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    captionsState = CaptionsState.ENABLED,
                ), awaitItem()
            )
        }
        verify { mockCall.enableCaptions(true) }
        verify(exactly = 0) { mockCall.enableCaptions(false) }
    }

    @Test
    fun `given viewmodel when initialize then create foreground service`() {
        val mockCall = buildMockCall()
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall

        sut()

        verify { foregroundServiceHandler.startForegroundService(ANY_ROOM_NAME) }
    }

    @Test
    fun `given viewmodel when receive CallActionHangUp then update state`() = runTest {
        val mockCall = buildMockCall()
        val callActionsFlow = MutableStateFlow<CallAction?>(null)
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        every { foregroundServiceHandler.actions } returns callActionsFlow
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            awaitItem()
            callActionsFlow.value = CallAction.HangUp
            assertEquals(
                MeetingRoomUiState(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isEndCall = true
                ), awaitItem()
            )
        }
    }

    private fun sut(): MeetingRoomScreenViewModel =
        MeetingRoomScreenViewModel(
            roomName = ANY_ROOM_NAME,
            sessionRepository = sessionRepository,
            archiveRepository = archiveRepository,
            screenSharingManager = screenSharingManager,
            captionsRepository = captionsRepository,
            videoClient = videoClient,
            foregroundServiceHandler = foregroundServiceHandler,
        )

    private fun buildSuccessSessionResponse(
        apiKey: String = "api-key",
        sessionId: String = "session-id",
        token: String = "token",
        captionsId: String? = null,
    ) = Result.success(
        SessionInfo(
            apiKey = apiKey,
            sessionId = sessionId,
            token = token,
            captionsId = captionsId,
        )
    )

    @Suppress("LongParameterList")
    private fun buildMockPublisher() = VeraPublisher(
        id = "publisher",
        videoSource = VideoSource.CAMERA,
        name = "I am a publisher",
        isMicEnabled = true,
        isCameraEnabled = true,
        view = mockk(),
        blurLevel = BlurLevel.NONE,
        cycleCamera = {},
        setCameraBlur = {},
        cameraIndex = 0,
        isSpeaking = false,
    )

    private fun buildMockCall(): CallFacade = mockk<CallFacade>(relaxed = true)

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
