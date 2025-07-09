package com.vonage.android.screen.join

import app.cash.turbine.test
import com.vonage.android.network.APIService
import com.vonage.android.network.GetSessionResponse
import com.vonage.android.util.RoomNameGenerator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import retrofit2.Response
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JoinMeetingRoomViewModelTest {

    val apiService: APIService = mockk()
    val roomNameGenerator: RoomNameGenerator = mockk()
    val sut = JoinMeetingRoomViewModel(
        apiService = apiService,
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
        coEvery { apiService.getSession(any()) } returns Response<GetSessionResponse>.success(
            GetSessionResponse(
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
        coEvery { apiService.getSession(any()) } returns Response.error(500, ResponseBody.EMPTY)

        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("validname")
            assertEquals(JoinMeetingRoomUiState.Loading, awaitItem())
            assertEquals(
                JoinMeetingRoomUiState.Content(
                    roomName = "validname",
                    isRoomNameWrong = true,
                ), awaitItem()
            )
        }
    }
}