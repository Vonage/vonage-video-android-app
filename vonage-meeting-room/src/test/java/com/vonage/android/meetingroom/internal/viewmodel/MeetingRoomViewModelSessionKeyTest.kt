package com.vonage.android.meetingroom.internal.viewmodel

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.fx.data.BackgroundsResult
import com.vonage.android.fx.data.GetBackgroundsUseCase
import com.vonage.android.fx.data.UserBackgroundRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.meetingroom.MainDispatcherRule
import com.vonage.android.meetingroom.api.MeetingRoomConfiguration
import com.vonage.android.meetingroom.api.MeetingRoomFeature
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.meetingroom.api.SessionKeyHolder
import com.vonage.android.meetingroom.internal.container.MeetingRoomContainer
import com.vonage.android.meetingroom.internal.screen.audio.MeetingRoomAudioDevicesHandler
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.android.shared.session.SessionInfo
import com.vonage.android.shared.session.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/** Session key lifecycle in [MeetingRoomViewModel]; general behavior lives in [MeetingRoomViewModelTest]. */
class MeetingRoomViewModelSessionKeyTest {

    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val container: MeetingRoomContainer = mockk(relaxed = true)
    private val prebuilt: MeetingRoomPrebuilt = mockk(relaxed = true)
    private val sessionRepository: SessionRepository = mockk()
    private val vonageArchiving: VonageArchiving = mockk(relaxed = true)
    private val videoClient: VonageVideoClient = mockk(relaxed = true)
    private val sessionKeyHolder = SessionKeyHolder()
    private val audioDevicesHandler: MeetingRoomAudioDevicesHandler = mockk(relaxed = true) {
        every { audioDevicesState } returns mockk(relaxed = true)
    }
    private val foregroundServiceHandler: MeetingRoomForegroundServiceHandler = mockk(relaxed = true) {
        every { actions } returns MutableSharedFlow()
    }
    private val getBackgroundsUseCase: GetBackgroundsUseCase = mockk {
        coEvery { invoke(captureResolution = null) } returns BackgroundsResult(
            persistentListOf(),
            remainingBackgroundSlots = UserBackgroundRepository.MAX_USER_BACKGROUNDS,
        )
    }

    private lateinit var sut: MeetingRoomViewModel

    @BeforeEach
    fun setUp() {
        every { container.prebuilt } returns prebuilt
        every { container.sessionRepository } returns sessionRepository
        every { container.sessionKeyHolder } returns sessionKeyHolder
        every { container.vonageArchiving } returns vonageArchiving
        every { container.videoClient } returns videoClient
        every { container.foregroundServiceHandler } returns foregroundServiceHandler
        every { container.audioDevicesHandler } returns audioDevicesHandler
        every { container.callSettingsHolder } returns CallSettingsHolder()
        every { container.getBackgroundsUseCase } returns getBackgroundsUseCase

        every { prebuilt.roomName } returns ROOM_NAME
        every { prebuilt.configuration } returns MeetingRoomConfiguration()
        every { prebuilt.publisherSettings } returns PublisherSettings()
        every { prebuilt.enabledFeatures } returns MeetingRoomFeature.all
        every { prebuilt.foregroundServiceEnabled } returns true
        every { prebuilt.hangUpCommand } returns MutableSharedFlow()

        sut = MeetingRoomViewModel(container)
    }

    @Test
    fun `given user leaves while session is fetched then late session is not connected`() = runTest {
        val session = CompletableDeferred<Result<SessionInfo>>()
        coEvery { sessionRepository.getSession(ROOM_NAME) } coAnswers { session.await() }

        sut.setup(context)
        sut.endCall()
        session.complete(Result.success(SESSION_INFO))
        testScheduler.advanceUntilIdle()

        verify(exactly = 0) { videoClient.initializeSession(any(), any(), any()) }
        assertEquals(null, sessionKeyHolder.sessionKey)
    }

    @Test
    fun `given no session key when archiveCall true then fail without calling backend`() = runTest {
        coEvery { sessionRepository.getSession(ROOM_NAME) } returns Result.success(SESSION_INFO)
        every { videoClient.initializeSession(any(), any(), any()) } returns mockCall()

        sut.uiState.test {
            awaitItem()
            sut.setup(context)
            testScheduler.advanceUntilIdle()
            awaitItem() // audio devices
            awaitItem() // connected
            sessionKeyHolder.sessionKey = null

            sut.archiveCall(true)
            assertEquals(ArchivingUiState.STARTING, awaitItem().archivingUiState)
            assertEquals(ArchivingUiState.IDLE, awaitItem().archivingUiState)
        }
        coVerify(exactly = 0) { vonageArchiving.startArchive(any()) }
    }

    private fun mockCall(): CallFacade = mockk(relaxed = true) {
        every { publisher } returns MutableStateFlow<PublisherState?>(null)
        every { participantsCount } returns MutableStateFlow(0)
        every { connect(any()) } returns flowOf()
    }

    private companion object {
        const val ROOM_NAME = "room-name"
        val SESSION_INFO = SessionInfo(
            applicationId = "application-id",
            sessionId = "session-id",
            token = "token",
            sessionKey = "session-key",
        )
    }
}
