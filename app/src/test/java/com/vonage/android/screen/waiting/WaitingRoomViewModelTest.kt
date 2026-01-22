package com.vonage.android.screen.waiting

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import com.vonage.android.config.Config
import com.vonage.android.config.GetConfig
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.CameraType
import com.vonage.android.kotlin.model.PreviewPublisherState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WaitingRoomViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val videoClient: VonageVideoClient = mockk()
    private val userRepository: UserRepository = mockk()
    private val getConfig: GetConfig = mockk()

    private lateinit var sut: WaitingRoomViewModel

    @Before
    fun setUp() {
        sut = WaitingRoomViewModel(
            roomName = ANY_ROOM_NAME,
            userRepository = userRepository,
            videoClient = videoClient,
            getConfig = getConfig,
        )

        every { getConfig.invoke() } returns Config(
            allowCameraControl = true,
            allowMicrophoneControl = true,
            allowShowParticipantList = true,
        )
    }

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.createPreviewPublisher(context, any()) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.init(context)

        sut.uiState.test {
            val initialState = awaitItem()
            assertEquals(ANY_ROOM_NAME, initialState.roomName)

            val updatedState = awaitItem()
            assertEquals(ANY_ROOM_NAME, updatedState.roomName)
            assertEquals(publisher.name, updatedState.userName)
        }

        verify { videoClient.createPreviewPublisher(context, "") }
    }

    @Test
    fun `given viewmodel when update user name then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.createPreviewPublisher(context, any()) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.uiState.test {
            assertEquals(ANY_ROOM_NAME, awaitItem().roomName)

            sut.init(context)

            val afterInitState = awaitItem()
            assertEquals(ANY_ROOM_NAME, afterInitState.roomName)
            assertEquals("", afterInitState.userName)

            sut.updateUserName("update")
            assertEquals("update", awaitItem().userName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewmodel when mic toggle then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.createPreviewPublisher(context, any()) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.init(context)

        sut.uiState.test {
            assertEquals(ANY_ROOM_NAME, awaitItem().roomName)

            val afterInitState = awaitItem()
            assertEquals(ANY_ROOM_NAME, afterInitState.roomName)
            assertEquals(publisher.name, afterInitState.userName)

            sut.onMicToggle()
        }
    }

    @Test
    fun `given viewmodel when camera toggle then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.createPreviewPublisher(context, any()) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.init(context)

        sut.uiState.test {
            sut.onCameraToggle()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewmodel with cached user name then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        coEvery { userRepository.getUserName() } returns "Cached user name"
        every { videoClient.createPreviewPublisher(context, "Cached user name") } returns publisher

        sut.uiState.test {
            awaitItem()
            sut.init(context)
            assertEquals("Cached user name", awaitItem().userName)
        }
    }

    @Test
    fun `given viewmodel when join room then user name is cached`() = runTest {
        coEvery { userRepository.getUserName() } returns "initial"
        coEvery { userRepository.saveUserName(any()) } returns Unit
        val mockPublisher = buildMockPublisher()
        every { videoClient.createPreviewPublisher(context, "initial") } returns mockPublisher
        every { videoClient.configurePublisher(any()) } returns Unit
        every { videoClient.destroyPublisher() } returns Unit

        sut.uiState.test {
            awaitItem()
            sut.init(context)
            sut.joinRoom("save user name")
            assertTrue(awaitItem().isSuccess)
        }

        coVerify { userRepository.saveUserName("save user name") }
        verify { videoClient.destroyPublisher() }
    }

    @Test
    fun `given viewmodel when onCameraSwitch then publisher cycle camera`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.createPreviewPublisher(context, any()) } returns publisher
        coEvery { userRepository.getUserName() } returns "not relevant"

        sut.init(context)

        sut.uiState.test {
            awaitItem() // initial state
            awaitItem() // after init

            sut.onCameraSwitch()
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { publisher.cycleCamera() }
    }

    @Test
    fun `given viewmodel when setBlur then publisher set camera blur`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.createPreviewPublisher(context, any()) } returns publisher
        coEvery { userRepository.getUserName() } returns "not relevant"

        sut.init(context)

        sut.uiState.test {
            awaitItem() // initial state
            awaitItem() // after init

            sut.setBlur()
        }

        verify { publisher.cycleCameraBlur() }
    }

    @Test
    fun `given viewmodel when stop then destroy publisher`() = runTest {
        every { videoClient.destroyPublisher() } returns Unit

        sut.onStop()

        verify { videoClient.destroyPublisher() }
    }

    @Suppress("LongParameterList")
    private fun buildMockPublisher() = mockk<PreviewPublisherState>(relaxed = true) {
        every { isCameraEnabled } returns MutableStateFlow(true)
        every { isMicEnabled } returns MutableStateFlow(false)
        every { blurLevel } returns MutableStateFlow(BlurLevel.NONE)
        every { camera } returns MutableStateFlow(CameraType.BACK)
    }

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
