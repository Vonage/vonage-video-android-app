package com.vonage.android.screen.join

import app.cash.turbine.test
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.util.RoomNameGenerator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JoinMeetingRoomViewModelTest {

    val sessionRepository: SessionRepository = mockk()
    val roomNameGenerator: RoomNameGenerator = mockk()
    val sut = JoinMeetingRoomViewModel(
        sessionRepository = sessionRepository,
        roomNameGenerator = roomNameGenerator,
    )

    @Test
    fun `given valid room name then state is correct`() = runTest {
        sut.updateName("validroomname")
        sut.uiState.test {
            assertEquals(
                JoinMeetingRoomUiState.Content(
                    roomName = "validroomname",
                    isRoomNameWrong = false,
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `given invalid room name then state is correct`() = runTest {
        sut.updateName("room@name")
        sut.uiState.test {
            assertEquals(
                JoinMeetingRoomUiState.Content(
                    roomName = "room@name",
                    isRoomNameWrong = true,
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when create room then state is correct`() = runTest {
        every { roomNameGenerator.generateRoomName() } returns "vonage-rocks"
        sut.createRoom()
        sut.uiState.test {
            assertEquals(
                JoinMeetingRoomUiState.Content(
                    roomName = "vonage-rocks",
                    isRoomNameWrong = false,
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when join room success then state is correct`() = runTest {
        coEvery { sessionRepository.getSession(any()) } returns Result.success(
            SessionInfo(
                apiKey = "apiKey",
                sessionId = "sessionId",
                token = "token",
            )
        )

        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("validname")
            assertEquals(JoinMeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                JoinMeetingRoomUiState.Success(
                    roomName = "validname",
                    apiKey = "apiKey",
                    sessionId = "sessionId",
                    token = "token",
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when join room failed then state is correct`() = runTest {
        coEvery { sessionRepository.getSession(any()) } returns Result.failure(Exception("Failure!"))

        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("validname")
            assertEquals(JoinMeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                JoinMeetingRoomUiState.Content(
                    roomName = "validname",
                    isRoomNameWrong = false,
                    isError = true,
                ), awaitItem()
            )
        }
    }
}