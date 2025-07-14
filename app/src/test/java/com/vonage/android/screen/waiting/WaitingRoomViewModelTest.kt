package com.vonage.android.screen.waiting

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.kotlin.Participant
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WaitingRoomViewModelTest {

    val createPublisherUseCase: CreatePublisherUseCase = mockk()
    val sut = WaitingRoomViewModel(
        createPublisher = createPublisherUseCase,
    )

    val participant: Participant = mockk()
    val context: Context = mockk()

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        every { createPublisherUseCase.invoke(any()) } returns buildMockParticipant()

        sut.init(context, "roomName")

        verify { createPublisherUseCase.invoke(context) }
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
        every { createPublisherUseCase.invoke(any()) } returns buildMockParticipant()

        sut.init(context, "roomName")
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
        every { createPublisherUseCase.invoke(any()) } returns buildMockParticipant()

        sut.init(context, "roomName")
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
        every { createPublisherUseCase.invoke(any()) } returns buildMockParticipant()

        sut.init(context, "roomName")
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

    private fun buildMockParticipant(): Participant {
        every { participant.view } returns mockk()
        every { participant.isCameraEnabled } returns false
        every { participant.isMicEnabled } returns false
        every { participant.name } returns "Matt"
        every { participant.name = any() } returns Unit
        every { participant.toggleVideo() } returns true
        every { participant.toggleAudio() } returns true
        return participant
    }
}
