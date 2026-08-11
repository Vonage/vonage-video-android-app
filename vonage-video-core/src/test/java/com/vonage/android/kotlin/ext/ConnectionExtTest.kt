package com.vonage.android.kotlin.ext

import android.view.View
import com.vonage.android.kotlin.sdk.VonageConnection
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VideoSource
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConnectionExtTest {

    @Test
    fun `extractSenderName should return participant name when connection ID matches`() {
        val connection = VonageConnection(connectionId = "conn-123")

        val participant = createParticipant("conn-123", "John Doe")
        val participants = listOf(participant)

        val result = connection.extractSenderName(participants)

        assertEquals("John Doe", result)
    }

    @Test
    fun `extractSenderName should return empty string when no match found`() {
        val connection = VonageConnection(connectionId = "conn-999")

        val participant1 = createParticipant("conn-123", "John Doe")
        val participant2 = createParticipant("conn-456", "Jane Smith")
        val participants = listOf(participant1, participant2)

        val result = connection.extractSenderName(participants)

        assertEquals("", result)
    }

    @Test
    fun `extractSenderName should return empty string for empty participants list`() {
        val connection = VonageConnection(connectionId = "conn-123")

        val participants = emptyList<Participant>()

        val result = connection.extractSenderName(participants)

        assertEquals("", result)
    }

    @Test
    fun `extractSenderName should return first match when multiple participants with same connection ID`() {
        val connection = VonageConnection(connectionId = "conn-123")

        val participant1 = createParticipant("conn-123", "John Doe")
        val participant2 = createParticipant("conn-123", "Jane Smith")
        val participants = listOf(participant1, participant2)

        val result = connection.extractSenderName(participants)

        assertEquals("John Doe", result)
    }

    @Test
    fun `extractSenderName should handle participant at end of list`() {
        val connection = VonageConnection(connectionId = "conn-789")

        val participant1 = createParticipant("conn-123", "John Doe")
        val participant2 = createParticipant("conn-456", "Jane Smith")
        val participant3 = createParticipant("conn-789", "Bob Wilson")
        val participants = listOf(participant1, participant2, participant3)

        val result = connection.extractSenderName(participants)

        assertEquals("Bob Wilson", result)
    }

    @Test
    fun `extractSenderName should handle single participant match`() {
        val connection = VonageConnection(connectionId = "conn-only")

        val participant = createParticipant("conn-only", "Solo User")
        val participants = listOf(participant)

        val result = connection.extractSenderName(participants)

        assertEquals("Solo User", result)
    }

    private fun createParticipant(connectionId: String, name: String): Participant {
        return object : Participant {
            override val id: String = "id-$connectionId"
            override val isPublisher: Boolean = false
            override val connectionId: String = connectionId
            override val creationTime: Long = System.currentTimeMillis()
            override val isScreenShare: Boolean = false
            override val videoSource: VideoSource = VideoSource.CAMERA
            override val name: String = name
            override val isMicEnabled: StateFlow<Boolean> = MutableStateFlow(true)
            override val isCameraEnabled: StateFlow<Boolean> = MutableStateFlow(true)
            override val isTalking: StateFlow<Boolean> = MutableStateFlow(false)
            override val audioLevel: StateFlow<Float> = MutableStateFlow(0f)
            override val view: View = mockk(relaxed = true)
        }
    }
}
