package com.vonage.android.screen.room

import app.cash.turbine.test
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.VeraPublisher
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MeetingRoomScreenViewModelTest {

    val sessionRepository: SessionRepository = mockk()
    val videoClient: VonageVideoClient = mockk()

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
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
                ), awaitItem()
            )
            verify { mockCall.connect() }
        }
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
                ), awaitItem()
            )
            sut.onToggleMic()
            verify { mockCall.togglePublisherAudio() }
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
                ), awaitItem()
            )
            sut.onToggleCamera()
            verify { mockCall.togglePublisherVideo() }
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
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
            assertEquals(MeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                MeetingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    call = mockCall,
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
                ), awaitItem()
            )
            sut.onSwitchCamera()
            verify { mockCall.togglePublisherCamera() }
        }
    }

    private fun sut(): MeetingRoomScreenViewModel =
        MeetingRoomScreenViewModel(
            roomName = ANY_ROOM_NAME,
            sessionRepository = sessionRepository,
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
    )

    private fun buildMockCall(): CallFacade = mockk<CallFacade>(relaxed = true)

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
