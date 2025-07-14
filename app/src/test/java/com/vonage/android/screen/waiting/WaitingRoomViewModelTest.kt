package com.vonage.android.screen.waiting

import app.cash.turbine.test
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.Participant
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WaitingRoomViewModelTest {

    val createPublisherUseCase: CreatePublisherUseCase = mockk()
    val userRepository: UserRepository = mockk()
    val sut = WaitingRoomViewModel(
        createPublisher = createPublisherUseCase,
        userRepository = userRepository,
    )

    val participant: Participant = mockk()

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        every { createPublisherUseCase.invoke() } returns buildMockParticipant()
        coEvery { userRepository.getUserName() } returns ""

        sut.init("roomName")

        verify { createPublisherUseCase.invoke() }
        sut.uiState.test {
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = participant.isCameraEnabled,
                    isMicEnabled = participant.isMicEnabled,
                    userName = participant.name,
                    view = participant.view,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when update user name then returns correct state`() = runTest {
        every { createPublisherUseCase.invoke() } returns buildMockParticipant()
        coEvery { userRepository.getUserName() } returns ""

        sut.init("roomName")
        sut.updateUserName("update")

        verify { participant.name = "update" }

        sut.uiState.test {
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = participant.isCameraEnabled,
                    isMicEnabled = participant.isMicEnabled,
                    userName = participant.name,
                    view = participant.view,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when mic toggle then returns correct state`() = runTest {
        every { createPublisherUseCase.invoke() } returns buildMockParticipant()
        coEvery { userRepository.getUserName() } returns ""

        sut.init("roomName")
        sut.onMicToggle()

        verify { participant.toggleAudio() }

        sut.uiState.test {
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = participant.isCameraEnabled,
                    isMicEnabled = participant.isMicEnabled,
                    userName = participant.name,
                    view = participant.view,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when camera toggle then returns correct state`() = runTest {
        every { createPublisherUseCase.invoke() } returns buildMockParticipant()
        coEvery { userRepository.getUserName() } returns ""

        sut.init("roomName")
        sut.onCameraToggle()

        verify { participant.toggleVideo() }

        sut.uiState.test {
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = participant.isCameraEnabled,
                    isMicEnabled = participant.isMicEnabled,
                    userName = participant.name,
                    view = participant.view,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel with cached user name then returns correct state`() = runTest {
        coEvery { userRepository.getUserName() } returns "Cached user name"
        every { createPublisherUseCase.invoke() } returns buildMockParticipant()

        sut.uiState.test {
            sut.init("roomName")
            awaitItem()
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = "roomName",
                    isCameraEnabled = participant.isCameraEnabled,
                    isMicEnabled = participant.isMicEnabled,
                    userName = "Cached user name",
                    view = participant.view,
                ), awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when join room then user name is cached`() = runTest {
        coEvery { userRepository.getUserName() } returns "initial"
        coEvery { userRepository.saveUserName(any()) } returns Unit
        every { createPublisherUseCase.invoke() } returns buildMockParticipant()

        sut.init("roomName")

        sut.uiState.test {
            awaitItem()
            sut.joinRoom("roomName", "save user name")
            assertEquals(
                WaitingRoomUiState.Success(
                    roomName = "roomName",
                ), awaitItem()
            )
        }
        coVerify { userRepository.saveUserName("save user name") }
    }

    private fun buildMockParticipant(): Participant {
        every { participant.view } returns mockk()
        every { participant.isCameraEnabled } returns false
        every { participant.isMicEnabled } returns false
        every { participant.name } returns ""
        every { participant.name = any() } returns Unit
        every { participant.toggleVideo() } returns true
        every { participant.toggleAudio() } returns true
        return participant
    }
}
