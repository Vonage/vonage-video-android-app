package com.vonage.android.screen.room

import app.cash.turbine.test
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VeraPublisher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MeetingRoomScreenViewModelTest {

    val sessionRepository: SessionRepository = mockk()
    val archiveRepository: ArchiveRepository = mockk()
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(MeetingRoomUiState.SessionError, awaitItem())
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
        val sut = sut()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
                ), awaitItem()
            )
            sut.endCall()
            verify { mockCall.endSession() }
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
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
                ), awaitItem()
            )
            sut.archiveCall(true, ANY_ROOM_NAME)
            coVerify { archiveRepository.startArchive(ANY_ROOM_NAME) }
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = true,
                ), awaitItem()
            )
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
                ), awaitItem()
            )
            sut.archiveCall(true, ANY_ROOM_NAME)
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = true,
                ), awaitItem()
            )
            sut.archiveCall(false, ANY_ROOM_NAME)
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
                    isRecording = false,
                ), awaitItem()
            )
            coVerify { archiveRepository.stopArchive(ANY_ROOM_NAME, "archiveId") }
        }
    }

    private fun sut(): MeetingRoomScreenViewModel =
        MeetingRoomScreenViewModel(
            roomName = ANY_ROOM_NAME,
            sessionRepository = sessionRepository,
            archiveRepository = archiveRepository,
            videoClient = videoClient,
        )

    private fun buildSuccessSessionResponse() = Result.success(
        SessionInfo(
            apiKey = "api-key",
            sessionId = "session-id",
            token = "token",
        )
    )

    private fun buildMockPublisher() = VeraPublisher(
        id = "publisher",
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

    private fun buildMockCall(): CallFacade = mockk<CallFacade> {
        every { toggleLocalAudio() } returns Unit
        every { toggleLocalVideo() } returns Unit
        every { toggleLocalCamera() } returns Unit
        every { observeLocalAudioLevel() } returns flowOf()
        every { connect() } returns flowOf(SessionEvent.Connected)
        every { sendEmoji(any()) } returns Unit
        every { resumeSession() } returns Unit
        every { pauseSession() } returns Unit
        every { endSession() } returns Unit
        every { listenUnreadChatMessages(any()) } returns Unit
        every { sendChatMessage(any()) } returns Unit
    }

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
