package com.vonage.android.screen.join

import app.cash.turbine.test
import com.vonage.android.util.RoomNameGenerator
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class JoinMeetingRoomViewModelTest {

    private val roomNameGenerator: RoomNameGenerator = mockk()

    private lateinit var sut: JoinMeetingRoomViewModel

    @Before
    fun setUp() {
        sut = JoinMeetingRoomViewModel(
            roomNameGenerator = roomNameGenerator,
        )
    }

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
                    isSuccess = true,
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when join room success then state is correct`() = runTest {
        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("validname")
            assertEquals(
                JoinMeetingRoomUiState.Content(
                    roomName = "validname",
                    isSuccess = true,
                ),
                awaitItem()
            )
        }
    }
}