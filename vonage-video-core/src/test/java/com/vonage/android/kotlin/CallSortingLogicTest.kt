package com.vonage.android.kotlin

import android.view.View
import app.cash.turbine.test
import com.vonage.android.kotlin.ext.mapSorted
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VideoSource
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CallSortingLogicTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should sort participants with screen sharing first`() = runTest {
        val cameraParticipant1 = createMockParticipant(
            id = "camera1",
            videoSource = VideoSource.CAMERA,
            creationTime = 1000L,
        )
        val cameraParticipant2 = createMockParticipant(
            id = "camera2",
            videoSource = VideoSource.CAMERA,
            creationTime = 2000L,
        )
        val screenParticipant = createMockParticipant(
            id = "screen1",
            videoSource = VideoSource.SCREEN,
            creationTime = 1500L,
        )

        val participants = flowOf(persistentListOf(cameraParticipant1, cameraParticipant2, screenParticipant))
        val sorted = participants.mapSorted()

        sorted.test {
            awaitItem().let { sorted ->
                assertEquals(3, sorted.size)
                assertEquals("screen1", sorted[0].id)
                assertEquals(VideoSource.SCREEN, sorted[0].videoSource)
                assertEquals("camera2", sorted[1].id)
                assertEquals("camera1", sorted[2].id)
            }
            awaitComplete()
        }
    }

    @Test
    fun `should sort participants by creation time when no screen sharing`() = runTest {
        val participant1 = createMockParticipant(
            id = "camera1",
            videoSource = VideoSource.CAMERA,
            creationTime = 1000L
        )
        val participant2 = createMockParticipant(
            id = "camera2",
            videoSource = VideoSource.CAMERA,
            creationTime = 3000L
        )
        val participant3 = createMockParticipant(
            id = "camera3",
            videoSource = VideoSource.CAMERA,
            creationTime = 2000L
        )

        val participants = flowOf(persistentListOf(participant1, participant2, participant3))
        val sorted = participants.mapSorted()

        sorted.test {
            awaitItem().let { sorted ->
                assertEquals("camera2", sorted[0].id) // 3000L
                assertEquals("camera3", sorted[1].id) // 2000L
                assertEquals("camera1", sorted[2].id) // 1000L
            }
            awaitComplete()
        }
    }

    @Test
    fun `should sort participants by creation time when multiple screen sharing`() = runTest {
        val screen1 = createMockParticipant(
            id = "screen1",
            videoSource = VideoSource.SCREEN,
            creationTime = 1000L
        )
        val screen2 = createMockParticipant(
            id = "screen2",
            videoSource = VideoSource.SCREEN,
            creationTime = 2000L
        )
        val camera = createMockParticipant(
            id = "camera1",
            videoSource = VideoSource.CAMERA,
            creationTime = 3000L
        )

        val participants = flowOf(persistentListOf(screen1, screen2, camera))
        val sorted = participants.mapSorted()

        sorted.test {
            awaitItem().let { sorted ->
                assertEquals("screen2", sorted[0].id) // Screen, 2000L
                assertEquals("screen1", sorted[1].id) // Screen, 1000L
                assertEquals("camera1", sorted[2].id) // Camera, 3000L
            }
            awaitComplete()
        }
    }

    private fun createMockParticipant(
        id: String,
        videoSource: VideoSource,
        creationTime: Long
    ): Participant {
        return object : Participant {
            override val id: String = id
            override val isPublisher: Boolean = false
            override val connectionId: String = "conn-$id"
            override val creationTime: Long = creationTime
            override val isScreenShare: Boolean = videoSource == VideoSource.SCREEN
            override val videoSource: VideoSource = videoSource
            override val name: String = "Participant $id"
            override val isMicEnabled: StateFlow<Boolean> = MutableStateFlow(true)
            override val isCameraEnabled: StateFlow<Boolean> = MutableStateFlow(true)
            override val isTalking: StateFlow<Boolean> = MutableStateFlow(false)
            override val audioLevel: StateFlow<Float> = MutableStateFlow(0f)
            override val view: View = mockk(relaxed = true)
        }
    }
}
