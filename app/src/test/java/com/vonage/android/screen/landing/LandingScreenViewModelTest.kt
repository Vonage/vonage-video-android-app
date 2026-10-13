package com.vonage.android.screen.landing

import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import com.vonage.android.data.RoomAccessRepository
import com.vonage.android.util.RoomNameGenerator
import io.mockk.coEvery
import io.mockk.coVerify
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
    private val roomAccessRepository: RoomAccessRepository = mockk {
        coEvery { requiresAuthentication(any()) } returns false
    }

    private lateinit var sut: LandingScreenViewModel

    @BeforeEach
    fun setUp() {
        sut = LandingScreenViewModel(
            roomNameGenerator = roomNameGenerator,
            roomAccessRepository = roomAccessRepository,
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
            assertEquals(LandingScreenUiState.Content(isCheckingAccess = true), awaitItem())
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
            assertEquals(LandingScreenUiState.Content(isCheckingAccess = true), awaitItem())
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

    @Test
    fun `given authentication required when join room then asks for authentication`() = runTest {
        coEvery { roomAccessRepository.requiresAuthentication("validname") } returns true

        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("validname")
            assertEquals(LandingScreenUiState.Content(isCheckingAccess = true), awaitItem())
            assertEquals(
                LandingScreenUiState.Content(authRequiredRoomName = "validname"),
                awaitItem()
            )
        }
    }

    @Test
    fun `given authentication required when create room then asks for authentication`() = runTest {
        every { roomNameGenerator.generateRoomName() } returns "vonage-rocks"
        coEvery { roomAccessRepository.requiresAuthentication("vonage-rocks") } returns true

        sut.uiState.test {
            awaitItem() // initial state
            sut.createRoom()
            assertEquals(LandingScreenUiState.Content(isCheckingAccess = true), awaitItem())
            assertEquals(
                LandingScreenUiState.Content(authRequiredRoomName = "vonage-rocks"),
                awaitItem()
            )
        }
    }

    @Test
    fun `given authentication requested when user signs in then navigates to room`() = runTest {
        coEvery { roomAccessRepository.requiresAuthentication("validname") } returns true

        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("validname")
            awaitItem() // checking access
            awaitItem() // authentication required
            sut.onAuthenticated()
            assertEquals(LandingScreenUiState.Success(roomName = "validname"), awaitItem())
        }
    }

    @Test
    fun `given authentication requested when user dismisses then stays on landing`() = runTest {
        coEvery { roomAccessRepository.requiresAuthentication("validname") } returns true

        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("validname")
            awaitItem() // checking access
            awaitItem() // authentication required
            sut.onAuthenticationDismissed()
            assertEquals(LandingScreenUiState.Content(), awaitItem())
        }
    }

    @Test
    fun `given access check in progress when join room again then ignores it`() = runTest {
        sut.uiState.test {
            awaitItem() // initial state
            sut.joinRoom("validname")
            sut.joinRoom("validname")
            awaitItem() // checking access
            awaitItem() // success
        }
        coVerify(exactly = 1) { roomAccessRepository.requiresAuthentication(any()) }
    }
}
