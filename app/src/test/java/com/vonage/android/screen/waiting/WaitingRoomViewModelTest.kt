package com.vonage.android.screen.waiting

import app.cash.turbine.test
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.VeraPublisher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WaitingRoomViewModelTest {

    val videoClient: VonageVideoClient = mockk()
    val userRepository: UserRepository = mockk()
    val sut = WaitingRoomViewModel(
        userRepository = userRepository,
        videoClient = videoClient,
    )

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.buildPublisher() } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.init("roomName")

        verify { videoClient.buildPublisher() }
        sut.uiState.test {
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = publisher.name,
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when update user name then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            userName = "",
        )
        every { videoClient.buildPublisher() } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.uiState.test {
            assertEquals(WaitingRoomUiState.Idle, awaitItem())
            sut.init("roomName")
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = "",
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
            sut.updateUserName("update")
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = "update",
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when mic toggle then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            isMicEnabled = false,
        )
        every { videoClient.buildPublisher() } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.uiState.test {
            assertEquals(WaitingRoomUiState.Idle, awaitItem())
            sut.init("roomName")
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = false,
                    userName = publisher.name,
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
            sut.onMicToggle()
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = true,
                    userName = publisher.name,
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when camera toggle then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            isCameraEnabled = true,
        )
        every { videoClient.buildPublisher() } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.uiState.test {
            assertEquals(WaitingRoomUiState.Idle, awaitItem())
            sut.init("roomName")
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = true,
                    userName = publisher.name,
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
            sut.onCameraToggle()
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = false,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = publisher.name,
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel with cached user name then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            userName = "Cached user name"
        )
        coEvery { userRepository.getUserName() } returns "Cached user name"
        every { videoClient.buildPublisher() } returns publisher

        sut.uiState.test {
            sut.init("roomName")
            awaitItem()
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = "Cached user name",
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when join room then user name is cached`() = runTest {
        coEvery { userRepository.getUserName() } returns "initial"
        coEvery { userRepository.saveUserName(any()) } returns Unit
        every { videoClient.buildPublisher() } returns buildMockPublisher(
            isCameraEnabled = false,
            isMicEnabled = false,
        )
        every { videoClient.configurePublisher(any()) } returns Unit
        every { videoClient.destroyPublisher() } returns Unit

        sut.uiState.test {
            assertEquals(WaitingRoomUiState.Idle, awaitItem())
            sut.init("roomName")
            awaitItem()
            sut.joinRoom("roomName", "save user name")
            assertEquals(
                WaitingRoomUiState.Success(
                    roomName = "roomName",
                ), awaitItem()
            )
        }
        coVerify { userRepository.saveUserName("save user name") }
        verify {
            videoClient.configurePublisher(
                PublisherConfig(
                    name = "save user name",
                    publishVideo = false,
                    publishAudio = false,
                    blurLevel = BlurLevel.NONE,
                    cameraIndex = 0,
                )
            )
        }
        verify { videoClient.destroyPublisher() }
    }

    private fun buildMockPublisher(
        userName: String = "",
        isCameraEnabled: Boolean = true,
        isMicEnabled: Boolean = true,
    ): VeraPublisher = VeraPublisher(
        id = "ignored",
        name = userName,
        isMicEnabled = isMicEnabled,
        isCameraEnabled = isCameraEnabled,
        blurLevel = BlurLevel.NONE,
        view = mockk(),
        cycleCamera = { },
        setCameraBlur = { },
        cameraIndex = 0,
    )
}
