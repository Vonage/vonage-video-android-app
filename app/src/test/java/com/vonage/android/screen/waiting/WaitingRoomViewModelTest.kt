package com.vonage.android.screen.waiting

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import com.vonage.android.audio.util.MicVolumeListener
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.model.VideoSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WaitingRoomViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    val context: Context = mockk(relaxed = true)
    val videoClient: VonageVideoClient = mockk()
    val userRepository: UserRepository = mockk()
    val micVolumeListener: MicVolumeListener = mockk {
        every { start() } returns Unit
        every { stop() } returns Unit
        every { volume() } returns flowOf(0.33f)
    }
    val sut = WaitingRoomViewModel(
        roomName = ANY_ROOM_NAME,
        userRepository = userRepository,
        videoClient = videoClient,
        micVolumeListener = micVolumeListener,
    )

//    @Before
//    fun setUp() {
//        setMainDispatcherToTestDispatcher()
//    }
//
//    @After
//    fun tearDown() {
//        testScheduler.advanceUntilIdle()
//        resetMain()
//        clearAllMocks()
//    }

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val publisher = buildMockPublisher()
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns ""
        every { micVolumeListener.volume() } returns flowOf(0.5f)

        sut.init(context)
        testScheduler.advanceUntilIdle()
        verify { videoClient.buildPublisher(context) }
        sut.uiState.test {
            awaitItem()
            awaitItem().let { uiState ->
                assertEquals(ANY_ROOM_NAME, uiState.roomName)
                assertEquals(publisher.isCameraEnabled.value, uiState.isCameraEnabled)
                assertEquals(publisher.isMicEnabled.value, uiState.isMicEnabled)
                assertEquals(publisher.name, uiState.userName)
                assertEquals(BlurLevel.NONE, uiState.blurLevel)
                assertEquals(0.5f, uiState.audioLevel.value)
            }
        }
        verify { micVolumeListener.start() }
        verify { micVolumeListener.volume() }
    }

    @Test
    fun `given viewmodel when update user name then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            userName = "",
        )
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.uiState.test {
            awaitItem()
            sut.init(context)
            testScheduler.advanceUntilIdle()
            awaitItem()
            awaitItem().let { uiState ->
                assertEquals(ANY_ROOM_NAME, uiState.roomName)
                assertEquals(publisher.isCameraEnabled.value, uiState.isCameraEnabled)
                assertEquals(publisher.isMicEnabled.value, uiState.isMicEnabled)
                assertEquals("", uiState.userName)
                assertEquals(BlurLevel.NONE, uiState.blurLevel)
            }
            sut.updateUserName("update")
            assertEquals("update", awaitItem().userName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewmodel when mic toggle then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            isMicEnabled = false,
            toggleMic = { false },
        )
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.uiState.test {
            sut.init(context)
            testScheduler.advanceUntilIdle()
            awaitItem().let { uiState ->
                assertEquals(ANY_ROOM_NAME, uiState.roomName)
                assertEquals(publisher.isCameraEnabled.value, uiState.isCameraEnabled)
                assertEquals(true, uiState.isMicEnabled)
                assertEquals(publisher.name, uiState.userName)
                assertEquals(BlurLevel.NONE, uiState.blurLevel)
            }
            sut.onMicToggle()
            testScheduler.advanceUntilIdle()
            assertEquals(false, awaitItem().isMicEnabled)
        }
    }

    @Test
    fun `given viewmodel when camera toggle then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            isCameraEnabled = false,
            toggleCamera = { false },
        )
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns ""

        sut.init(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            assertEquals(true, awaitItem().isCameraEnabled)
            sut.onCameraToggle()
            testScheduler.advanceUntilIdle()
            assertEquals(false, awaitItem().isCameraEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewmodel with cached user name then returns correct state`() = runTest {
        val publisher = buildMockPublisher(
            userName = "Cached user name"
        )
        coEvery { userRepository.getUserName() } returns "Cached user name"
        every { videoClient.buildPublisher(context) } returns publisher

        sut.uiState.test {
            sut.init(context)
            testScheduler.advanceUntilIdle()
            awaitItem()
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

        sut.uiState.test {
            awaitItem()
            sut.init(context)
            testScheduler.advanceUntilIdle()
            awaitItem()
            sut.joinRoom("save user name")
            assertEquals("initial", awaitItem().userName)
        }
        coVerify { userRepository.saveUserName("save user name") }
        verify {
            videoClient.configurePublisher(
                PublisherConfig(
                    name = "save user name",
                    publishVideo = true,
                    publishAudio = true,
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
        every { videoClient.buildPublisher(context) } returns publisher
        coEvery { userRepository.getUserName() } returns "not relevant"

        sut.init(context)
        testScheduler.advanceUntilIdle()

        sut.uiState.test {
            awaitItem() // initial state
            awaitItem() // after init

            sut.onCameraSwitch()
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
        testScheduler.advanceUntilIdle()

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
