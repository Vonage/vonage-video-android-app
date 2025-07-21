package com.vonage.android.screen.room

import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class MeetingRoomScreenViewModelTest {

    val sessionRepository: SessionRepository = mockk()
    val videoClient: VonageVideoClient = mockk()
    val sut = MeetingRoomScreenViewModel(
        sessionRepository = sessionRepository,
        videoClient = videoClient,
    )

    @Test
    fun `should do something`() = runTest {
        sut.init("room-name")
    }
}
