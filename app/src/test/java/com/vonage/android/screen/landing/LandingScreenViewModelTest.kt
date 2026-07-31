package com.vonage.android.screen.landing

import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import com.vonage.android.util.RoomNameGenerator
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class LandingScreenViewModelTest {

    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private val roomNameGenerator: RoomNameGenerator = mockk()

    private lateinit var sut: LandingScreenViewModel

    @BeforeEach
    fun setUp() {
        sut = LandingScreenViewModel(
            roomNameGenerator = roomNameGenerator,
        )
    }

    @Test
    fun `given valid room name then state is correct`() = runTest {
        sut.updateName("validroomname")
        sut.uiState.test {
            assertEquals(
                LandingScreenUiState.Content(
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
                LandingScreenUiState.Content(
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
                LandingScreenUiState.Success(
                    roomName = "vonage-rocks",
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
                LandingScreenUiState.Success(
                    roomName = "validname",
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `given viewmodel when join room fails then state is correct`() = runTest {
        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("invalid name")
            assertEquals(
                LandingScreenUiState.Content(
                    roomName = "invalid name",
                    isRoomNameWrong = true,
                ),
                awaitItem()
            )
        }
    }
}
