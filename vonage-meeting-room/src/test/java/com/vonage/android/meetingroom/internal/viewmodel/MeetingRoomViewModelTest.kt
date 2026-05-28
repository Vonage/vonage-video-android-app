package com.vonage.android.meetingroom.internal.viewmodel

import android.content.Context
import android.content.Intent
import app.cash.turbine.test
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.kotlin.sdk.VonageError
import com.vonage.android.meetingroom.MainDispatcherRule
import com.vonage.android.meetingroom.api.MeetingRoomConfiguration
import com.vonage.android.meetingroom.api.MeetingRoomFeature
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.meetingroom.internal.container.MeetingRoomContainer
import com.vonage.android.meetingroom.internal.data.MeetingRoomSessionRepository
import com.vonage.android.meetingroom.internal.data.SessionInfo
import com.vonage.android.meetingroom.internal.screen.CallLayoutType
import com.vonage.android.meetingroom.internal.screen.MeetingRoomUiState
import com.vonage.android.meetingroom.internal.screen.audio.MeetingRoomAudioDevicesHandler
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler.CallAction
import com.vonage.android.meetingroom.internal.util.ActivityContextHolder
import com.vonage.android.screensharing.ScreenSharingState
import com.vonage.android.screensharing.VonageScreenSharing
import com.vonage.android.settings.CallSettingsHolder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.net.Uri
import com.vonage.android.fx.data.AddBackgroundUseCase
import com.vonage.android.fx.data.BackgroundsResult
import com.vonage.android.fx.data.DeleteBackgroundUseCase
import com.vonage.android.fx.data.GetBackgroundsUseCase
import com.vonage.android.fx.data.UserBackgroundRepository
import com.vonage.android.fx.ui.VideoBackgroundItem
import kotlinx.collections.immutable.persistentListOf
import kotlin.Result.Companion.success

class MeetingRoomViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val container: MeetingRoomContainer = mockk(relaxed = true)
    private val prebuilt: MeetingRoomPrebuilt = mockk(relaxed = true)
    private val sessionRepository: MeetingRoomSessionRepository = mockk()
    private val vonageArchiving: VonageArchiving = mockk(relaxed = true)
    private val vonageCaptions: VonageCaptions = mockk(relaxed = true)
    private val vonageScreenSharing: VonageScreenSharing = mockk(relaxed = true)
    private val videoClient: VonageVideoClient = mockk(relaxed = true)
    private val audioDevicesStateMock = mockk<com.vonage.android.meetingroom.internal.screen.audio.AudioDevicesState>(relaxed = true)
    private val callSettingsHolder: CallSettingsHolder = CallSettingsHolder()
    private val audioDevicesHandler: MeetingRoomAudioDevicesHandler = mockk(relaxed = true) {
        every { audioDevicesState } returns audioDevicesStateMock
    }
    private val foregroundServiceHandler: MeetingRoomForegroundServiceHandler = mockk(relaxed = true) {
        every { actions } returns MutableSharedFlow()
    }
    private val hangUpCommands = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val activityContextHolder: ActivityContextHolder = mockk(relaxed = true)
    private val getBackgroundsUseCase: GetBackgroundsUseCase = mockk {
        coEvery { invoke(captureResolution = null) } returns BackgroundsResult(
            persistentListOf(),
            remainingBackgroundSlots = UserBackgroundRepository.MAX_USER_BACKGROUNDS,
        )
    }
    private val addBackgroundUseCase: AddBackgroundUseCase = mockk(relaxed = true)
    private val deleteBackgroundUseCase: DeleteBackgroundUseCase = mockk(relaxed = true)

    private lateinit var sut: MeetingRoomViewModel

    @Before
    fun setUp() {
        every { container.prebuilt } returns prebuilt
        every { container.sessionRepository } returns sessionRepository
        every { container.vonageArchiving } returns vonageArchiving
        every { container.vonageCaptions } returns vonageCaptions
        every { container.vonageScreenSharing } returns vonageScreenSharing
        every { container.videoClient } returns videoClient
        every { container.foregroundServiceHandler } returns foregroundServiceHandler
        every { container.activityContextHolder } returns activityContextHolder
        every { container.audioDevicesHandler } returns audioDevicesHandler
        every { container.callSettingsHolder } returns callSettingsHolder
        every { container.getBackgroundsUseCase } returns getBackgroundsUseCase
        every { container.addBackgroundUseCase } returns addBackgroundUseCase
        every { container.deleteBackgroundUseCase } returns deleteBackgroundUseCase

        every { prebuilt.roomName } returns ANY_ROOM_NAME
        every { prebuilt.configuration } returns MeetingRoomConfiguration()
        every { prebuilt.publisherSettings } returns PublisherSettings()
        every { prebuilt.enabledFeatures } returns MeetingRoomFeature.all
        every { prebuilt.foregroundServiceEnabled } returns true
        every { prebuilt.hangUpCommand } returns hangUpCommands

        sut = MeetingRoomViewModel(container)
    }

    // region Initialization

    @Test
    fun `given viewmodel when initialize then returns correct state`() = runTest {
        val mockCall = givenMockCall()

        sut.uiState.test {
            assertEquals(MeetingRoomUiState(roomName = ANY_ROOM_NAME, isLoading = true), awaitItem())
            sut.setup(context)
            testScheduler.advanceUntilIdle()

            awaitItem() // audio devices update
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState) // connected
        }

        verify { activityContextHolder.setActivityContext(context) }
        verify { mockCall.connect(any(Context::class)) }
    }

    @Test
    fun `given viewmodel when initialize fails then returns error state`() = runTest {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns Result.failure(Exception("Empty response"))

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        // observeUiStateForPublicBridge() keeps the stateIn upstream active, so .value is current.
        val errorState = sut.uiState.value
        assertEquals(false, errorState.isLoading)
        assertEquals(true, errorState.isError)
        assertEquals(audioDevicesStateMock, errorState.audioDevicesState)
    }

    @Test
    fun `given viewmodel when initialize then create foreground service`() {
        verify { foregroundServiceHandler.startForegroundService(ANY_ROOM_NAME) }
    }

    // endregion

    // region Call controls (delegation — no state emission needed)

    @Test
    fun `given viewmodel when onToggleMic then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.onToggleMic()
        verify { mockCall.toggleLocalAudio() }
    }

    @Test
    fun `given viewmodel when onToggleCamera then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.onToggleCamera()
        verify { mockCall.toggleLocalVideo() }
    }

    @Test
    fun `given viewmodel when onSwitchCamera then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.onSwitchCamera()
        verify { mockCall.toggleLocalCamera() }
    }

    @Test
    fun `given viewmodel when onTogglePinParticipant then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.onTogglePinParticipant("participant-id")
        verify { mockCall.togglePinParticipant("participant-id") }
    }

    @Test
    fun `given viewmodel when forceMuteParticipant then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.forceMuteParticipant("participant-id")
        verify { mockCall.forceMuteParticipant("participant-id") }
    }

    @Test
    fun `given viewmodel when sendMessage then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.sendMessage("hi there!")
        verify { mockCall.sendChatMessage("hi there!") }
    }

    @Test
    fun `given viewmodel when listenUnread then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.listenUnread(false)
        verify { mockCall.listenUnreadChatMessages(false) }
    }

    @Test
    fun `given viewmodel when sendEmoji then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.sendEmoji("emoji :)")
        verify { mockCall.sendEmoji("emoji :)") }
    }

    @Test
    fun `given viewmodel when applyVideoEffect then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.applyVideoEffect(VideoEffect.BlurLow)

        verify { mockCall.applyLocalVideoEffect(VideoEffect.BlurLow) }
    }

    // endregion

    // region End call

    @Test
    fun `given viewmodel when endCall then delegate to call`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.endCall()

        verify { mockCall.endSession() }
        verify { vonageScreenSharing.stopSharingScreen() }
    }

    @Test
    fun `given viewmodel when endCall called twice then only delegates once`() = runTest {
        val mockCall = givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        sut.endCall()
        testScheduler.advanceUntilIdle() // let observePublisherSettings settle after callSettingsHolder.clear()

        sut.endCall()

        verify(exactly = 1) { mockCall.endSession() }
        verify(exactly = 1) { foregroundServiceHandler.stopForegroundService() }
    }

    // endregion

    // region Layout

    @Test
    fun `given viewmodel when change layout then update state`() = runTest {
        givenMockCall()

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.changeLayout(CallLayoutType.SPEAKER_LAYOUT)
            assertEquals(CallLayoutType.SPEAKER_LAYOUT, awaitItem().layoutType)

            sut.changeLayout(CallLayoutType.GRID)
            assertEquals(CallLayoutType.GRID, awaitItem().layoutType)
        }
    }

    // endregion

    // region Notification actions

    @Test
    fun `given viewmodel when receive CallAction HangUp then update state`() = runTest {
        givenMockCall()
        val callActionsFlow = MutableStateFlow<CallAction?>(null)
        every { foregroundServiceHandler.actions } returns callActionsFlow
        sut = MeetingRoomViewModel(container)

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        callActionsFlow.value = CallAction.HangUp
        testScheduler.advanceUntilIdle()

        // observeUiStateForPublicBridge() keeps the stateIn upstream active, so .value is current.
        assertTrue(sut.uiState.value.isEndCall)
    }

    // endregion

    // region Archiving

    @Test
    fun `given viewmodel when archiveCall true then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageArchiving.startArchive(ANY_ROOM_NAME) } returns success(
            com.vonage.android.archiving.ArchiveId("archiveId"),
        )

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState) // connected

            sut.archiveCall(true)
            assertEquals(ArchivingUiState.STARTING, awaitItem().archivingUiState)
            assertEquals(ArchivingUiState.RECORDING, awaitItem().archivingUiState)
            coVerify { vonageArchiving.startArchive(ANY_ROOM_NAME) }
        }
    }

    @Test
    fun `given viewmodel when archiveCall false then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageArchiving.startArchive(ANY_ROOM_NAME) } returns success(
            com.vonage.android.archiving.ArchiveId("archiveId"),
        )
        coEvery { vonageArchiving.stopArchive(ANY_ROOM_NAME) } returns success(true)

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState) // connected

            sut.archiveCall(true)
            assertEquals(ArchivingUiState.STARTING, awaitItem().archivingUiState)
            assertEquals(ArchivingUiState.RECORDING, awaitItem().archivingUiState)
            coVerify { vonageArchiving.startArchive(ANY_ROOM_NAME) }

            sut.archiveCall(false)
            assertEquals(ArchivingUiState.STOPPING, awaitItem().archivingUiState)
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
            coVerify { vonageArchiving.stopArchive(ANY_ROOM_NAME) }
        }
    }

    @Test
    fun `given viewmodel when other participant starts archiving then emit correct state`() = runTest {
        val mockCall = givenMockCall()
        val archivingStateFlow = MutableSharedFlow<ArchivingState>()
        every { vonageArchiving.bind(mockCall) } returns archivingStateFlow

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState) // connected

            archivingStateFlow.emit(ArchivingState.Started("any-archiving-id"))
            assertEquals(ArchivingUiState.RECORDING, awaitItem().archivingUiState)
        }
    }

    @Test
    fun `given viewmodel when other participant stops archiving then emit correct state`() = runTest {
        val mockCall = givenMockCall()
        val archivingStateFlow = MutableSharedFlow<ArchivingState>()
        every { vonageArchiving.bind(mockCall) } returns archivingStateFlow

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState) // connected

            archivingStateFlow.emit(ArchivingState.Started("any-archiving-id"))
            assertEquals(ArchivingUiState.RECORDING, awaitItem().archivingUiState)

            archivingStateFlow.emit(ArchivingState.Stopped("any-archiving-id"))
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
        }
    }

    // endregion

    // region Screen sharing

    @Test
    fun `given viewmodel when startScreenSharing started then emit correct state`() = runTest {
        val mockCall = givenMockCall()
        val mockIntent: Intent = mockk(relaxed = true)
        val onStartSlot = slot<() -> Unit>()
        val onStopSlot = slot<() -> Unit>()

        every {
            vonageScreenSharing.startScreenSharing(
                intent = mockIntent,
                call = mockCall,
                onStarted = capture(onStartSlot),
                onStopped = capture(onStopSlot),
            )
        } returns Unit

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            assertEquals(ScreenSharingState.IDLE, awaitItem().screenSharingState) // connected

            sut.startScreenSharing(mockIntent)
            assertEquals(ScreenSharingState.STARTING, awaitItem().screenSharingState)
            onStartSlot.captured.invoke()
            assertEquals(ScreenSharingState.SHARING, awaitItem().screenSharingState)
            verify { vonageScreenSharing.startScreenSharing(mockIntent, mockCall, any(), any()) }
        }
    }

    @Test
    fun `given viewmodel when startScreenSharing stopped then emit correct state`() = runTest {
        val mockCall = givenMockCall()
        val mockIntent: Intent = mockk(relaxed = true)
        val onStartSlot = slot<() -> Unit>()
        val onStopSlot = slot<() -> Unit>()

        every {
            vonageScreenSharing.startScreenSharing(
                intent = mockIntent,
                call = mockCall,
                onStarted = capture(onStartSlot),
                onStopped = capture(onStopSlot),
            )
        } returns Unit

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            assertEquals(ScreenSharingState.IDLE, awaitItem().screenSharingState) // connected

            sut.startScreenSharing(mockIntent)
            assertEquals(ScreenSharingState.STARTING, awaitItem().screenSharingState)
            onStopSlot.captured.invoke()
            assertEquals(ScreenSharingState.IDLE, awaitItem().screenSharingState)
        }
    }

    @Test
    fun `given viewmodel when stopScreenSharing then emit correct state`() = runTest {
        givenMockCall()

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            assertEquals(ScreenSharingState.IDLE, awaitItem().screenSharingState) // connected

            sut.stopScreenSharing()
            assertEquals(ScreenSharingState.STOPPING, awaitItem().screenSharingState)
            verify { vonageScreenSharing.stopSharingScreen() }
        }
    }

    // endregion

    // region Captions

    @Test
    fun `given viewmodel when init with captionsId then emit correct state`() = runTest {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse(captionsId = "captionsId")
        val mockCall: CallFacade = buildMockCall()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
        }
    }

    @Test
    fun `given viewmodel when enable captions then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageCaptions.enable() } returns success(Unit)

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.captions(true)
            assertEquals(CaptionsUiState.ENABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
            coVerify { vonageCaptions.enable() }
        }
    }

    @Test
    fun `given viewmodel when enable captions fails then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageCaptions.enable() } returns Result.failure(Exception("KO"))

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.captions(true)
            assertEquals(CaptionsUiState.ENABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.IDLE, awaitItem().captionsUiState)
        }
    }

    @Test
    fun `given viewmodel when disable captions then emit correct state`() = runTest {
        givenMockCall()
        coEvery { vonageCaptions.enable() } returns success(Unit)
        coEvery { vonageCaptions.disable() } returns success(Unit)

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.captions(true)
            assertEquals(CaptionsUiState.ENABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
            coVerify { vonageCaptions.enable() }

            sut.captions(false)
            assertEquals(CaptionsUiState.DISABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.IDLE, awaitItem().captionsUiState)
            coVerify { vonageCaptions.disable() }
        }
    }

    @Test
    fun `given viewmodel when disable captions fails then keep enabled state`() = runTest {
        givenMockCall()
        coEvery { vonageCaptions.enable() } returns success(Unit)
        coEvery { vonageCaptions.disable() } returns Result.failure(Exception("KO"))

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected

            sut.captions(true)
            assertEquals(CaptionsUiState.ENABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState)
            coVerify { vonageCaptions.enable() }

            sut.captions(false)
            assertEquals(CaptionsUiState.DISABLING, awaitItem().captionsUiState)
            assertEquals(CaptionsUiState.ENABLED, awaitItem().captionsUiState) // remains ENABLED on failure
            coVerify { vonageCaptions.disable() }
        }
    }

    // endregion

    // region Session events

    @Test
    fun `given viewmodel when connection fails then sets error state`() = runTest {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns Result.failure(Exception("Network error"))

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        val state = sut.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(true, state.isError)
    }

    @Test
    fun `given viewmodel when SessionEvent Disconnected then endCall is invoked`() = runTest {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        val mockCall: CallFacade = mockk(relaxed = true) {
            every { publisher } returns MutableStateFlow<PublisherState?>(null)
            every { participantsCount } returns MutableStateFlow(0)
            every { connect(any()) } returns flowOf(SessionEvent.Disconnected)
        }
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        verify { foregroundServiceHandler.stopForegroundService() }
        verify { vonageScreenSharing.stopSharingScreen() }
        verify { mockCall.endSession() }
    }

    @Test
    fun `given viewmodel when SessionEvent Error then sets error state with message`() = runTest {
        val vonageError = VonageError(code = 1, message = "Session error")
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        val mockCall: CallFacade = mockk(relaxed = true) {
            every { publisher } returns MutableStateFlow<PublisherState?>(null)
            every { participantsCount } returns MutableStateFlow(0)
            every { connect(any()) } returns flowOf(SessionEvent.Error(vonageError))
        }
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall

        sut.setup(context)
        testScheduler.advanceUntilIdle()

        val state = sut.uiState.value
        assertEquals(true, state.isError)
        assertEquals("Session error", state.errorMessage)
    }

    // endregion

    // region Background management

    @Test
    fun `given setup when getBackgroundsUseCase returns result then backgrounds and remainingBackgroundSlots update in state`() = runTest {
        // Given
        val backgrounds = persistentListOf(VideoBackgroundItem(id = "bg-1"))
        coEvery { getBackgroundsUseCase(captureResolution = null) } returns BackgroundsResult(backgrounds, remainingBackgroundSlots = 0)
        givenMockCall()

        // When
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(backgrounds, sut.uiState.value.backgrounds)
        assertEquals(0, sut.uiState.value.remainingBackgroundSlots)
    }

    @Test
    fun `given setup when getBackgroundsUseCase throws then sets empty backgrounds and remainingBackgroundSlots is MAX`() = runTest {
        // Given
        coEvery { getBackgroundsUseCase(captureResolution = null) } throws RuntimeException("load failure")
        givenMockCall()

        // When
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        // Then
        assertTrue(sut.uiState.value.backgrounds.isEmpty())
        assertEquals(UserBackgroundRepository.MAX_USER_BACKGROUNDS, sut.uiState.value.remainingBackgroundSlots)
    }

    @Test
    fun `given addBackground is called then delegates to addBackgroundUseCase and refreshes backgrounds in state`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val updatedBackgrounds = persistentListOf(VideoBackgroundItem(id = "user-bg", isUserUploaded = true))
        givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()
        coEvery { getBackgroundsUseCase(captureResolution = null) } returns BackgroundsResult(
            updatedBackgrounds, remainingBackgroundSlots = 0,
        )

        // When
        sut.addBackground(listOf(uri))
        testScheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { addBackgroundUseCase(uri, any()) }
        assertEquals(updatedBackgrounds, sut.uiState.value.backgrounds)
        assertEquals(0, sut.uiState.value.remainingBackgroundSlots)
    }

    @Test
    fun `given deleteBackground is called then delegates to deleteBackgroundUseCase and refreshes backgrounds`() = runTest {
        // Given
        val item = VideoBackgroundItem(id = "user-bg", isUserUploaded = true)
        val updatedBackgrounds = persistentListOf(VideoBackgroundItem(id = "bg-1"))
        givenMockCall()
        sut.setup(context)
        testScheduler.advanceUntilIdle()
        coEvery { getBackgroundsUseCase(captureResolution = null) } returns BackgroundsResult(
            updatedBackgrounds, remainingBackgroundSlots = UserBackgroundRepository.MAX_USER_BACKGROUNDS,
        )

        // When
        sut.deleteBackground(item)
        testScheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { deleteBackgroundUseCase("user-bg") }
        assertEquals(updatedBackgrounds, sut.uiState.value.backgrounds)
    }

    @Test
    fun `given deleteBackground when deleted background is the active video effect then resets effect to None`() = runTest {
        // Given
        val item = VideoBackgroundItem(id = "user-bg", isUserUploaded = true)
        val mockPublisher = mockk<PublisherState>(relaxed = true) {
            every { videoEffect } returns MutableStateFlow(VideoEffect.BackgroundImage(id = "user-bg", imagePath = "path"))
            every { isMicEnabled } returns MutableStateFlow(true)
            every { isCameraEnabled } returns MutableStateFlow(true)
        }
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        val mockCall = mockk<CallFacade>(relaxed = true) {
            every { publisher } returns MutableStateFlow<PublisherState?>(mockPublisher)
            every { participantsCount } returns MutableStateFlow(0)
            every { connect(any()) } returns flowOf()
        }
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        // When
        sut.deleteBackground(item)
        testScheduler.advanceUntilIdle()

        // Then
        verify(exactly = 1) { mockCall.applyLocalVideoEffect(VideoEffect.None) }
    }

    @Test
    fun `given deleteBackground when a different background is the active effect then does not reset effect`() = runTest {
        // Given
        val item = VideoBackgroundItem(id = "user-bg", isUserUploaded = true)
        val mockPublisher = mockk<PublisherState>(relaxed = true) {
            every { videoEffect } returns MutableStateFlow(VideoEffect.BackgroundImage(id = "other-bg", imagePath = "path"))
            every { isMicEnabled } returns MutableStateFlow(true)
            every { isCameraEnabled } returns MutableStateFlow(true)
        }
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        val mockCall = mockk<CallFacade>(relaxed = true) {
            every { publisher } returns MutableStateFlow<PublisherState?>(mockPublisher)
            every { participantsCount } returns MutableStateFlow(0)
            every { connect(any()) } returns flowOf()
        }
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        sut.setup(context)
        testScheduler.advanceUntilIdle()

        // When
        sut.deleteBackground(item)
        testScheduler.advanceUntilIdle()

        // Then
        verify(exactly = 0) { mockCall.applyLocalVideoEffect(any()) }
    }

    // endregion

    // region Foreground service configurability

    @Test
    fun `given foregroundServiceEnabled false when initialize then foreground service is NOT started`() = runTest {
        val freshHandler: MeetingRoomForegroundServiceHandler = mockk(relaxed = true) {
            every { actions } returns MutableSharedFlow()
        }
        every { container.foregroundServiceHandler } returns freshHandler
        every { prebuilt.foregroundServiceEnabled } returns false
        MeetingRoomViewModel(container)
        testScheduler.advanceUntilIdle()
        verify(exactly = 0) { freshHandler.startForegroundService(any()) }
    }

    @Test
    fun `given foregroundServiceEnabled false when endCall then foreground service is NOT stopped`() = runTest {
        every { prebuilt.foregroundServiceEnabled } returns false
        val vm = MeetingRoomViewModel(container)
        testScheduler.advanceUntilIdle()
        vm.endCall()
        verify(exactly = 0) { foregroundServiceHandler.stopForegroundService() }
    }

    @Test
    fun `given hangUpCommand emits then isEndCall becomes true`() = runTest {
        sut.uiState.test {
            awaitItem() // initial state
            hangUpCommands.emit(Unit)
            val updated = awaitItem()
            assertTrue(updated.isEndCall)
        }
    }

    // endregion

    // region Helpers

    private fun givenMockCall(): CallFacade {
        coEvery { sessionRepository.getSession(ANY_ROOM_NAME) } returns buildSuccessSessionResponse()
        val mockCall = buildMockCall()
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall
        return mockCall
    }

    private fun buildMockCall(): CallFacade = mockk(relaxed = true) {
        // Explicitly type generic StateFlow properties to avoid MockK type-erasure ClassCastExceptions.
        every { publisher } returns MutableStateFlow<PublisherState?>(null)
        every { participantsCount } returns MutableStateFlow(0)
        every { connect(any()) } returns flowOf()
    }

    private fun buildSuccessSessionResponse(
        apiKey: String = "api-key",
        sessionId: String = "session-id",
        token: String = "token",
        captionsId: String? = null,
    ) = success(
        SessionInfo(apiKey = apiKey, sessionId = sessionId, token = token, captionsId = captionsId),
    )

    // endregion

    private companion object {
        const val ANY_ROOM_NAME = "room-name"
    }
}
