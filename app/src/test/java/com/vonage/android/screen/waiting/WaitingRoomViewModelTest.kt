package com.vonage.android.screen.waiting

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import com.vonage.android.config.Config
import com.vonage.android.config.GetConfig
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CameraType
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.PreviewPublisherState
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.screen.components.audio.AudioDevicesHandler
import com.vonage.android.settings.CallSettingsHolder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    private val audioDevicesHandler: AudioDevicesHandler = mockk(relaxed = true)
    private val callSettingsHolder: CallSettingsHolder = mockk(relaxed = true) {
        every { senderStatsEnabled } returns MutableStateFlow(true)
        every { captureFrameRate } returns MutableStateFlow(CaptureFrameRate.FPS_15)
        every { captureResolution } returns MutableStateFlow(null)
        every { preferredVideoCodecOrder } returns MutableStateFlow(null)
        every { audioBitrate } returns MutableStateFlow(null)
        every { opusDtxEnabled } returns MutableStateFlow(true)
        every { publisherAudioFallbackEnabled } returns MutableStateFlow(true)
        every { subscriberAudioFallbackEnabled } returns MutableStateFlow(true)
        every { videoBitrateConfig } returns MutableStateFlow(VideoBitrateConfig())
    }

    private lateinit var sut: WaitingRoomViewModel

    @Before
    fun setUp() {
        sut = WaitingRoomViewModel(
            roomName = ANY_ROOM_NAME,
            appContext = context,
            userRepository = userRepository,
            videoClient = videoClient,
            getConfig = getConfig,
            audioDevicesHandler = audioDevicesHandler,
            callSettingsHolder = callSettingsHolder,
        )

        every { getConfig.invoke() } returns Config(
            allowCameraControl = true,
            allowMicrophoneControl = true,
            allowShowParticipantList = true,
        )
        every { videoClient.configurePublisher(any()) } returns Unit
    }

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val publisher = givenPreviewPublisher()
        coEvery { userRepository.getUserName() } returns ""

        sut.init(context)

        sut.uiState.test {
            val initialState = awaitItem()
            assertEquals(ANY_ROOM_NAME, initialState.roomName)

            val updatedState = awaitItem()
            assertEquals(ANY_ROOM_NAME, updatedState.roomName)
            assertEquals(publisher.name, updatedState.userName)
        }

        verify { videoClient.createPreviewPublisher(context) }
    }

    @Test
    fun `given viewmodel when update user name then returns correct state`() = runTest {
        givenPreviewPublisher()
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
        val publisher = givenPreviewPublisher()
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
        givenPreviewPublisher()
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
        every { videoClient.createPreviewPublisher(context) } returns publisher

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
        givenPreviewPublisher()
        every { videoClient.configurePublisher(any()) } returns Unit
        every { videoClient.destroyPublisher() } returns Unit

        sut.uiState.test {
            awaitItem()                // initial state
            sut.init(context)
            awaitItem()                // post-init: publisher now set in uiState
            sut.joinRoom("save user name")
            val state = awaitItem()    // post-join: isSuccess = true
            assertTrue(state.isSuccess)
            assertEquals("save user name", state.joinSettings.username)
        }

        coVerify { userRepository.saveUserName("save user name") }
        verify { videoClient.destroyPublisher() }
    }

    @Test
    fun `given viewmodel when join room with whitespace-only name then state is invalid and room is not joined`() = runTest {
        coEvery { userRepository.getUserName() } returns "initial"
        givenPreviewPublisher()
        every { videoClient.destroyPublisher() } returns Unit

        sut.uiState.test {
            awaitItem()                // initial state
            sut.init(context)
            awaitItem()                // post-init: publisher now set in uiState
            sut.joinRoom("   ")        // whitespace-only — trimmed to empty, must be rejected
            val state = awaitItem()
            assertFalse(state.isSuccess)
            assertFalse(state.isUserNameValid)
        }

        coVerify(exactly = 0) { userRepository.saveUserName(any()) }
    }

    @Test
    fun `given initialized viewmodel when join room then joinSettings contains publisher state`() = runTest {
        coEvery { userRepository.getUserName() } returns "initial"
        coEvery { userRepository.saveUserName(any()) } returns Unit
        givenPreviewPublisher()
        every { videoClient.configurePublisher(any()) } returns Unit
        every { videoClient.destroyPublisher() } returns Unit

        sut.uiState.test {
            awaitItem() // initial state
            sut.init(context)
            awaitItem() // post-init: publisher is now set in _uiState
            sut.joinRoom("Alice")
            val state = awaitItem() // post-join state
            assertTrue(state.isSuccess)
            assertEquals(
                PublisherSettings(
                    username = "Alice",
                    publishAudio = false,      // isMicEnabled = false per buildMockPublisher()
                    publishVideo = true,       // isCameraEnabled = true per buildMockPublisher()
                    initialVideoEffect = VideoEffect.None,
                ),
                state.joinSettings,
            )
        }
    }

    @Test
    fun `given viewmodel when onCameraSwitch then publisher cycle camera`() = runTest {
        val publisher = givenPreviewPublisher()
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
    fun `given viewmodel when applyVideoEffect then delegate to publisher`() = runTest {
        val publisher = givenPreviewPublisher()
        coEvery { userRepository.getUserName() } returns ""

        sut.init(context)
        sut.uiState.test {
            awaitItem() // initial state
            awaitItem() // after init

            sut.applyVideoEffect(VideoEffect.BlurLow)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { publisher.applyVideoEffect(VideoEffect.BlurLow) }
    }

    @Test
    fun `given viewmodel when stop then destroy publisher`() = runTest {
        every { videoClient.destroyPublisher() } returns Unit

        sut.onStop()

        verify { videoClient.destroyPublisher() }
    }

    private fun givenPreviewPublisher(): PreviewPublisherState {
        val publisher = buildMockPublisher()
        every { videoClient.createPreviewPublisher(context) } returns publisher
        return publisher
    }

    @Suppress("LongParameterList")
    private fun buildMockPublisher() = mockk<PreviewPublisherState>(relaxed = true) {
        every { isCameraEnabled } returns MutableStateFlow(true)
        every { isMicEnabled } returns MutableStateFlow(false)
        every { videoEffect } returns MutableStateFlow(VideoEffect.None)
        every { camera } returns MutableStateFlow(CameraType.BACK)
    }

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
