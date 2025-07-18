package com.vonage.android.screen.waiting

import app.cash.turbine.test
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
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

    val publisher: VeraPublisher = mockk()

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        every { videoClient.buildPublisher() } returns buildMockPublisher()
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
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when update user name then returns correct state`() = runTest {
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        coEvery { userRepository.getUserName() } returns ""

        sut.init("roomName")
        sut.updateUserName("update")

        verify { publisher.name = "update" }

        sut.uiState.test {
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = publisher.name,
                    view = publisher.view,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when mic toggle then returns correct state`() = runTest {
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        coEvery { userRepository.getUserName() } returns ""

        sut.init("roomName")
        sut.onMicToggle()

        verify { publisher.toggleAudio() }

        sut.uiState.test {
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = publisher.name,
                    view = publisher.view,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when camera toggle then returns correct state`() = runTest {
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        coEvery { userRepository.getUserName() } returns ""

        sut.init("roomName")
        sut.onCameraToggle()

        verify { publisher.toggleVideo() }

        sut.uiState.test {
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = publisher.name,
                    view = publisher.view,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel with cached user name then returns correct state`() = runTest {
        coEvery { userRepository.getUserName() } returns "Cached user name"
        every { videoClient.buildPublisher() } returns buildMockPublisher(
            userName = "Cached user name"
        )

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
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when join room then user name is cached`() = runTest {
        coEvery { userRepository.getUserName() } returns "initial"
        coEvery { userRepository.saveUserName(any()) } returns Unit
        every { videoClient.buildPublisher() } returns buildMockPublisher()
        every { videoClient.configurePublisher(any()) } returns Unit
        every { videoClient.destroyPublisher() } returns Unit

        sut.uiState.test {
            awaitItem() // initial state
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
        verify { videoClient.configurePublisher(PublisherConfig(
            name = "save user name",
            publishVideo = false,
            publishAudio = false,
        ))}
        verify { videoClient.destroyPublisher() }
    }

    private fun buildMockPublisher(
        userName: String = ""
    ): VeraPublisher {
        every { publisher.view } returns mockk()
        every { publisher.isCameraEnabled } returns false
        every { publisher.isMicEnabled } returns false
        every { publisher.name } returns userName
        every { publisher.name = any() } returns Unit
        every { publisher.toggleVideo() } returns true
        every { publisher.toggleAudio() } returns true
        return publisher
    }
}
