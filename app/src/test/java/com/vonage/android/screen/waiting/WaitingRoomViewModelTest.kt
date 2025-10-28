package com.vonage.android.screen.waiting

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import com.vonage.android.audio.util.MicVolumeListener
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.model.VideoSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WaitingRoomViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val videoClient: VonageVideoClient = mockk()
    private val userRepository: UserRepository = mockk()
    private val micVolumeListener: MicVolumeListener = mockk {
        every { start() } returns Unit
        every { stop() } returns Unit
        every { volume() } returns flowOf(0.33f)
    }

    private lateinit var sut: WaitingRoomViewModel

    @Before
    fun setUp() {
        sut = WaitingRoomViewModel(
            roomName = ANY_ROOM_NAME,
            userRepository = userRepository,
            videoClient = videoClient,
            micVolumeListener = micVolumeListener,
        )
    }

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns ""
        every { micVolumeListener.volume() } returns flowOf(0.5f)

        sut.init(context)

        sut.uiState.test {
            val initialState = awaitItem()
            assertEquals(ANY_ROOM_NAME, initialState.roomName)

            val updatedState = awaitItem()
            assertEquals(ANY_ROOM_NAME, updatedState.roomName)
            assertEquals(publisher.isCameraEnabled.value, updatedState.isCameraEnabled)
            assertEquals(publisher.isMicEnabled.value, updatedState.isMicEnabled)
            assertEquals(publisher.name, updatedState.userName)
            assertEquals(BlurLevel.NONE, updatedState.blurLevel)
            assertEquals(0.5f, updatedState.audioLevel.value)
        }

        verify { videoClient.buildPublisher(context) }
        verify { micVolumeListener.start() }
        verify { micVolumeListener.volume() }
    }

    @Test
    fun `given viewmodel when update user name then returns correct state`() = runTest {
        val publisher = buildMockPublisher(userName = "")
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.uiState.test {
            assertEquals(ANY_ROOM_NAME, awaitItem().roomName)

            sut.init(context)

            val afterInitState = awaitItem()
            assertEquals(ANY_ROOM_NAME, afterInitState.roomName)
            assertEquals(publisher.isCameraEnabled.value, afterInitState.isCameraEnabled)
            assertEquals(publisher.isMicEnabled.value, afterInitState.isMicEnabled)
            assertEquals("", afterInitState.userName)
            assertEquals(BlurLevel.NONE, afterInitState.blurLevel)

            sut.updateUserName("update")
            assertEquals("update", awaitItem().userName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewmodel when mic toggle then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            isMicEnabled = true, // Set to true initially
            toggleMic = { false }, // When toggled, returns false
        )
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.init(context)

        sut.uiState.test {
            assertEquals(ANY_ROOM_NAME, awaitItem().roomName)

            val afterInitState = awaitItem()
            assertEquals(ANY_ROOM_NAME, afterInitState.roomName)
            assertEquals(publisher.isCameraEnabled.value, afterInitState.isCameraEnabled)
            assertEquals(true, afterInitState.isMicEnabled) // Should be true initially
            assertEquals(publisher.name, afterInitState.userName)
            assertEquals(BlurLevel.NONE, afterInitState.blurLevel)

            sut.onMicToggle()
            assertEquals(false, awaitItem().isMicEnabled) // After toggle, should be false
        }
    }

    @Test
    fun `given viewmodel when camera toggle then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            isCameraEnabled = true, // Set to true initially
            toggleCamera = { false }, // When toggled, returns false
        )
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.init(context)

        sut.uiState.test {
            assertEquals(true, awaitItem().isCameraEnabled) // Default from initial state
            assertEquals(true, awaitItem().isCameraEnabled) // Updated from publisher
            sut.onCameraToggle()
            assertEquals(false, awaitItem().isCameraEnabled) // After toggle, should be false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewmodel with cached user name then returns correct state`() = runTest {
        val publisher = buildMockPublisher(userName = "Cached user name")
        coEvery { userRepository.getUserName() } returns "Cached user name"
        every { videoClient.buildPublisher(context) } returns publisher

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
        every { videoClient.buildPublisher(context) } returns mockPublisher
        every { videoClient.configurePublisher(any()) } returns Unit
        every { videoClient.destroyPublisher() } returns Unit

        sut.init(context)
        delay(100)
        sut.joinRoom("save user name")
        delay(100)

        coVerify { userRepository.saveUserName("save user name") }
        verify { videoClient.destroyPublisher() }
    }

    @Test
    fun `given viewmodel when onCameraSwitch then publisher cycle camera`() = runTest {
        val publisher = buildMockPublisher(
            cycleCamera = mockk(relaxed = true),
        )
        every { videoClient.buildPublisher(context) } returns publisher
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
        val setCameraBlurCallbacks = mutableListOf<BlurLevel>()
        val setCameraBlurFunction: (BlurLevel) -> Unit = { blurLevel ->
            setCameraBlurCallbacks.add(blurLevel)
        }

        val publisher = buildMockPublisher(
            setCameraBlur = setCameraBlurFunction,
        )
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns "not relevant"

        sut.init(context)

        sut.uiState.test {
            awaitItem() // initial state
            awaitItem() // after init

            sut.setBlur()
            assertEquals(BlurLevel.LOW, awaitItem().blurLevel)

            sut.setBlur()
            assertEquals(BlurLevel.HIGH, awaitItem().blurLevel)

            sut.setBlur()
            assertEquals(BlurLevel.NONE, awaitItem().blurLevel)

            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(3, setCameraBlurCallbacks.size)
        assertEquals(BlurLevel.LOW, setCameraBlurCallbacks[0])
        assertEquals(BlurLevel.HIGH, setCameraBlurCallbacks[1])
        assertEquals(BlurLevel.NONE, setCameraBlurCallbacks[2])
    }

    @Test
    fun `given viewmodel when stop then destroy publisher`() = runTest {
        every { videoClient.destroyPublisher() } returns Unit

        sut.onStop()

        verify { micVolumeListener.stop() }
        verify { videoClient.destroyPublisher() }
    }

    @Suppress("LongParameterList")
    private fun buildMockPublisher(
        userName: String = "",
        type: VideoSource = VideoSource.CAMERA,
        isCameraEnabled: Boolean = true,
        isMicEnabled: Boolean = true,
        cameraIndex: Int = 0,
        cycleCamera: () -> Unit = {},
        blurLevel: BlurLevel = BlurLevel.NONE,
        setCameraBlur: (BlurLevel) -> Unit = {},
        toggleMic: () -> Boolean = { false },
        toggleCamera: () -> Boolean = { false },
    ): VeraPublisher = VeraPublisher(
        id = "ignored",
        videoSource = type,
        name = userName,
        isMicEnabled = MutableStateFlow(isMicEnabled),
        isCameraEnabled = MutableStateFlow(isCameraEnabled),
        blurLevel = blurLevel,
        view = mockk(relaxed = true),
        cycleCamera = cycleCamera,
        setCameraBlur = setCameraBlur,
        cameraIndex = cameraIndex,
        isSpeaking = MutableStateFlow(false),
        toggleMic = toggleMic,
        toggleCamera = toggleCamera,
    )

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
