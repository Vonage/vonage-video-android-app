package com.vonage.android.screen.room

import android.content.Intent
import android.media.projection.MediaProjection
import app.cash.turbine.test
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.VideoSource
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screensharing.ScreenSharingServiceListener
import com.vonage.android.screensharing.VeraScreenSharingManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MeetingRoomScreenViewModelTest {

    val sessionRepository: SessionRepository = mockk()
    val archiveRepository: ArchiveRepository = mockk()
    val screenSharingManager: VeraScreenSharingManager = mockk()
    val videoClient: VonageVideoClient = mockk()

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
            sut.archiveCall(true, ANY_ROOM_NAME)
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
            sut.archiveCall(true, ANY_ROOM_NAME)
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
            sut.archiveCall(false, ANY_ROOM_NAME)
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

    private fun sut(): MeetingRoomScreenViewModel =
        MeetingRoomScreenViewModel(
            roomName = ANY_ROOM_NAME,
            sessionRepository = sessionRepository,
            archiveRepository = archiveRepository,
            screenSharingManager = screenSharingManager,
            videoClient = videoClient,
        )

    private fun buildSuccessSessionResponse() = Result.success(
        SessionInfo(
            apiKey = "api-key",
            sessionId = "session-id",
            token = "token",
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
