package com.vonage.android.meetingroom.internal.viewmodel

import com.vonage.android.meetingroom.MainDispatcherRule
import com.vonage.android.meetingroom.api.MeetingRoomConfiguration
import com.vonage.android.meetingroom.api.MeetingRoomFeature
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.meetingroom.internal.container.MeetingRoomContainer
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler
import com.vonage.audioselector.AudioDevicesHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/** Activity lifecycle hooks in [MeetingRoomViewModel]; general behavior lives in [MeetingRoomViewModelTest]. */
class MeetingRoomViewModelLifecycleTest {

    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private val container: MeetingRoomContainer = mockk(relaxed = true)
    private val prebuilt: MeetingRoomPrebuilt = mockk(relaxed = true)
    private val audioDevicesHandler: AudioDevicesHandler = mockk(relaxed = true)
    private val foregroundServiceHandler: MeetingRoomForegroundServiceHandler = mockk(relaxed = true) {
        every { actions } returns MutableSharedFlow()
    }

    private lateinit var sut: MeetingRoomViewModel

    @BeforeEach
    fun setUp() {
        every { container.prebuilt } returns prebuilt
        every { container.audioDevicesHandler } returns audioDevicesHandler
        every { container.foregroundServiceHandler } returns foregroundServiceHandler

        every { prebuilt.roomName } returns ROOM_NAME
        every { prebuilt.configuration } returns MeetingRoomConfiguration()
        every { prebuilt.publisherSettings } returns PublisherSettings()
        every { prebuilt.enabledFeatures } returns MeetingRoomFeature.all
        every { prebuilt.foregroundServiceEnabled } returns true
        every { prebuilt.hangUpCommand } returns MutableSharedFlow()

        sut = MeetingRoomViewModel(container)
    }

    @Test
    fun `given active call when onResume then restore audio focus`() {
        sut.onResume()

        verify { audioDevicesHandler.restoreAudioFocus() }
    }

    @Test
    fun `given ended call when onResume then audio focus is not restored`() {
        sut.endCall()

        sut.onResume()

        verify(exactly = 0) { audioDevicesHandler.restoreAudioFocus() }
    }

    private companion object {
        const val ROOM_NAME = "room"
    }
}
