package com.vonage.android.kotlin.ext

import android.view.View
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VideoSource
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParticipantExtTest {

    @Test
    fun `firstScreenSharing should return first screen share participant`() {
        val participants = listOf(
            createParticipant("camera1", VideoSource.CAMERA),
            createParticipant("screen1", VideoSource.SCREEN),
            createParticipant("camera2", VideoSource.CAMERA),
            createParticipant("screen2", VideoSource.SCREEN)
        )

        val result = participants.firstScreenSharing()

        assertEquals("screen1", result?.id)
    }

    @Test
    fun `firstScreenSharing should return null when no screen sharing`() {
        val participants = listOf(
            createParticipant("camera1", VideoSource.CAMERA),
            createParticipant("camera2", VideoSource.CAMERA),
            createParticipant("camera3", VideoSource.CAMERA)
        )

        val result = participants.firstScreenSharing()

        assertNull(result)
    }

    @Test
    fun `firstScreenSharing should return null for empty list`() {
        val participants = emptyList<Participant>()

        val result = participants.firstScreenSharing()

        assertNull(result)
    }

    @Test
    fun `firstScreenSharing should return only screen share in list`() {
        val participants = listOf(
            createParticipant("screen1", VideoSource.SCREEN)
        )

        val result = participants.firstScreenSharing()

        assertEquals("screen1", result?.id)
    }

    @Test
    fun `firstScreenSharing should return first when all are screen sharing`() {
        val participants = listOf(
            createParticipant("screen1", VideoSource.SCREEN),
            createParticipant("screen2", VideoSource.SCREEN),
            createParticipant("screen3", VideoSource.SCREEN)
        )

        val result = participants.firstScreenSharing()

        assertEquals("screen1", result?.id)
    }

    @Test
    fun `firstScreenSharing should work with single camera participant`() {
        val participants = listOf(
            createParticipant("camera1", VideoSource.CAMERA)
        )

        val result = participants.firstScreenSharing()

        assertNull(result)
    }

    private fun createParticipant(id: String, videoSource: VideoSource): Participant {
        return object : Participant {
            override val id: String = id
            override val isPublisher: Boolean = false
            override val connectionId: String = "conn-$id"
            override val creationTime: Long = System.currentTimeMillis()
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
