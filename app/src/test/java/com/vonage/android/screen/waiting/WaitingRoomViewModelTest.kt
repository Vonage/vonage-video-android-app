package com.vonage.android.screen.waiting

import app.cash.turbine.test
import com.vonage.android.audio.util.MicVolume
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WaitingRoomViewModelTest {

    val videoClient: VonageVideoClient = mockk()
    val userRepository: UserRepository = mockk()
    val micVolume: MicVolume = mockk(relaxed = true)
    val sut = WaitingRoomViewModel(
        roomName = ANY_ROOM_NAME,
        userRepository = userRepository,
        videoClient = videoClient,
        micVolume = micVolume,
    )

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.buildPublisher() } returns publisher
        coEvery { userRepository.getUserName() } returns ""
        every { micVolume.volume() } returns flowOf(0.5f)

        sut.init()

        verify { videoClient.buildPublisher() }
        sut.uiState.test {
            assertEquals(WaitingRoomUiState.Idle, awaitItem())
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = publisher.name,
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
        }
        sut.audioLevel.test {
            awaitItem() // initial value
            assertEquals(0.5f, awaitItem())
        }
        verify { micVolume.start() }
        verify { micVolume.volume() }
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
            sut.init()
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
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
                    roomName = ANY_ROOM_NAME,
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = "update",
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
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
            sut.init()
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
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
                    roomName = ANY_ROOM_NAME,
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = true,
                    userName = publisher.name,
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
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
            sut.init()
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
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
                    roomName = ANY_ROOM_NAME,
                    isCameraEnabled = false,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = publisher.name,
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
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
            sut.init()
            awaitItem()
            assertEquals(
                WaitingRoomUiState.Content(
                    roomName = ANY_ROOM_NAME,
                    isCameraEnabled = publisher.isCameraEnabled,
                    isMicEnabled = publisher.isMicEnabled,
                    userName = "Cached user name",
                    view = publisher.view,
                    blurLevel = BlurLevel.NONE,
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
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
            sut.init()
            awaitItem()
            sut.joinRoom(ANY_ROOM_NAME, "save user name")
            assertEquals(
                WaitingRoomUiState.Success(
                    roomName = ANY_ROOM_NAME,
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
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

    @Test
    fun `given viewmodel when onCameraSwitch then publisher cycle camera`() = runTest {
        val publisher = buildMockPublisher(
            cycleCamera = mockk(relaxed = true),
        )
        every { videoClient.buildPublisher() } returns publisher
        coEvery { userRepository.getUserName() } returns "not relevant"

        sut.init()
        sut.onCameraSwitch()

        verify(exactly = 1) { publisher.cycleCamera() }
    }

    @Test
    fun `given viewmodel when setBlur then publisher set camera blur`() = runTest {
        val publisher = buildMockPublisher(
            setCameraBlur = mockk(relaxed = true),
        )
        every { videoClient.buildPublisher() } returns publisher
        coEvery { userRepository.getUserName() } returns "not relevant"

        sut.init()
        sut.setBlur()
        verify(exactly = 1) { publisher.setCameraBlur(BlurLevel.LOW) }

        sut.setBlur()
        verify(exactly = 1) { publisher.setCameraBlur(BlurLevel.HIGH) }

        sut.setBlur()
        verify(exactly = 1) { publisher.setCameraBlur(BlurLevel.NONE) }
    }

    @Test
    fun `given viewmodel when stop then destroy publisher`() = runTest {
        every { videoClient.destroyPublisher() } returns Unit

        sut.onStop()

        verify { micVolume.stop() }
        verify { videoClient.destroyPublisher() }
    }

    private fun buildMockPublisher(
        userName: String = "",
        isCameraEnabled: Boolean = true,
        isMicEnabled: Boolean = true,
        cameraIndex: Int = 0,
        cycleCamera: () -> Unit = {},
        blurLevel: BlurLevel = BlurLevel.NONE,
        setCameraBlur: (BlurLevel) -> Unit = {},
    ): VeraPublisher = VeraPublisher(
        id = "ignored",
        name = userName,
        isMicEnabled = isMicEnabled,
        isCameraEnabled = isCameraEnabled,
        blurLevel = blurLevel,
        view = mockk(relaxed = true),
        cycleCamera = cycleCamera,
        setCameraBlur = setCameraBlur,
        cameraIndex = cameraIndex,
        isSpeaking = false,
    )

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
